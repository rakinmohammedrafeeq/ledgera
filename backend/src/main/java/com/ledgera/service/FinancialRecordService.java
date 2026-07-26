package com.ledgera.service;

import com.ledgera.dto.FinancialRecordRequest;
import com.ledgera.dto.FinancialRecordResponse;
import com.ledgera.entity.FinancialRecord;
import com.ledgera.entity.User;
import com.ledgera.entity.Workspace;
import com.ledgera.enums.TransactionType;
import com.ledgera.enums.WorkspacePermission;
import com.ledgera.exception.ForbiddenException;
import com.ledgera.exception.ResourceNotFoundException;
import com.ledgera.repository.FinancialRecordRepository;
import com.ledgera.repository.FinancialRecordSpecification;
import com.ledgera.repository.WorkspaceMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@Service
public class FinancialRecordService {

    private static final Logger logger = LoggerFactory.getLogger(FinancialRecordService.class);

    private final FinancialRecordRepository recordRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final CurrentUserService currentUserService;
    private final VectorSearchService vectorSearchService;

    public FinancialRecordService(FinancialRecordRepository recordRepository,
                                  WorkspaceMemberRepository workspaceMemberRepository,
                                  CurrentUserService currentUserService,
                                  VectorSearchService vectorSearchService) {
        this.recordRepository = recordRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.currentUserService = currentUserService;
        this.vectorSearchService = vectorSearchService;
    }

    @Transactional
    public FinancialRecordResponse createRecord(FinancialRecordRequest request) {
        User currentUser = currentUserService.requireCurrentUser();

        // Get current workspace
        Workspace workspace = currentUser.getCurrentWorkspace();
        if (workspace == null) {
            throw new ForbiddenException("No workspace selected");
        }

        // Check permission - need EDITOR or OWNER
        WorkspacePermission permission = workspaceMemberRepository
                .findPermissionByWorkspaceAndUser(workspace.getId(), currentUser.getId())
                .orElseThrow(() -> new ForbiddenException("You don't have access to this workspace"));

        if (permission == WorkspacePermission.VIEWER) {
            throw new ForbiddenException("Viewers cannot create records");
        }

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(TransactionType.valueOf(request.getType()))
                .category(request.getCategory())
                .date(request.getDate())
                .description(request.getDescription())
                .user(currentUser)
                .workspace(workspace)
                .build();

        FinancialRecord savedRecord = recordRepository.save(record);
        
        // Return immediately - indexing will happen asynchronously
        FinancialRecordResponse response = toResponse(savedRecord);
        
        // Index the record for vector search asynchronously (don't block the response)
        // This runs in a separate thread after the transaction commits
        Long recordId = savedRecord.getId();
        Long userId = currentUser.getId();
        Long workspaceId = workspace.getId();
        
        // Schedule async indexing - this happens after response is sent
        CompletableFuture.runAsync(() -> {
            try {
                vectorSearchService.indexFinancialRecord(
                    recordRepository.findById(recordId).orElse(null), 
                    userId, 
                    workspaceId
                );
            } catch (Exception e) {
                logger.error("Failed to index financial record {} for vector search: {}", recordId, e.getMessage());
            }
        });
        
        return response;
    }
    
    @Transactional
    public FinancialRecordResponse updateRecord(Long id, FinancialRecordRequest request) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        User currentUser = currentUserService.requireCurrentUser();

        // Check workspace access
        Workspace workspace = currentUser.getCurrentWorkspace();
        if (workspace == null || !record.getWorkspace().getId().equals(workspace.getId())) {
            throw new ForbiddenException("Record not found in current workspace");
        }

        // Check permission - need EDITOR or OWNER
        WorkspacePermission permission = workspaceMemberRepository
                .findPermissionByWorkspaceAndUser(workspace.getId(), currentUser.getId())
                .orElseThrow(() -> new ForbiddenException("You don't have access to this workspace"));

        if (permission == WorkspacePermission.VIEWER) {
            throw new ForbiddenException("Viewers cannot update records");
        }

        record.setAmount(request.getAmount());
        record.setType(TransactionType.valueOf(request.getType()));
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setDescription(request.getDescription());
        // Keep the original owner, don't change it

        FinancialRecord savedRecord = recordRepository.save(record);
        
        // Return immediately without waiting for indexing
        return toResponse(savedRecord);
    }

    @Transactional
    public void deleteRecord(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        User currentUser = currentUserService.requireCurrentUser();

        // Check workspace access
        Workspace workspace = currentUser.getCurrentWorkspace();
        if (workspace == null || !record.getWorkspace().getId().equals(workspace.getId())) {
            throw new ForbiddenException("Record not found in current workspace");
        }

        // Check permission - need EDITOR or OWNER
        WorkspacePermission permission = workspaceMemberRepository
                .findPermissionByWorkspaceAndUser(workspace.getId(), currentUser.getId())
                .orElseThrow(() -> new ForbiddenException("You don't have access to this workspace"));

        if (permission == WorkspacePermission.VIEWER) {
            throw new ForbiddenException("Viewers cannot delete records");
        }

        recordRepository.delete(record);
    }

    @Transactional(readOnly = true)
    public Page<FinancialRecordResponse> getAllRecords(
            LocalDate startDate, LocalDate endDate,
            String category, TransactionType type,
            int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        User currentUser = currentUserService.requireCurrentUser();

        // Get current workspace
        Workspace workspace = currentUser.getCurrentWorkspace();
        if (workspace == null) {
            throw new ForbiddenException("No workspace selected");
        }

        // Check workspace access
        WorkspacePermission permission = workspaceMemberRepository
                .findPermissionByWorkspaceAndUser(workspace.getId(), currentUser.getId())
                .orElseThrow(() -> new ForbiddenException("You don't have access to this workspace"));

        // All records are scoped to current workspace
        Specification<FinancialRecord> spec = FinancialRecordSpecification.withFilters(
                startDate, endDate, category, type, workspace.getId());

        return recordRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public FinancialRecordResponse getRecordById(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        User currentUser = currentUserService.requireCurrentUser();

        // Check workspace access
        Workspace workspace = currentUser.getCurrentWorkspace();
        if (workspace == null || !record.getWorkspace().getId().equals(workspace.getId())) {
            throw new ForbiddenException("Record not found in current workspace");
        }

        // Check workspace access
        workspaceMemberRepository
                .findPermissionByWorkspaceAndUser(workspace.getId(), currentUser.getId())
                .orElseThrow(() -> new ForbiddenException("You don't have access to this workspace"));

        return toResponse(record);
    }

    private FinancialRecordResponse toResponse(FinancialRecord record) {
        return FinancialRecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType().name())
                .category(record.getCategory())
                .date(record.getDate())
                .description(record.getDescription())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .userId(record.getUser() != null ? record.getUser().getId() : null)
                .userName(record.getUser() != null ? record.getUser().getName() : null)
                .userEmail(record.getUser() != null ? record.getUser().getEmail() : null)
                .build();
    }
}
