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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final CurrentUserService currentUserService;

    public FinancialRecordService(FinancialRecordRepository recordRepository,
                                  WorkspaceMemberRepository workspaceMemberRepository,
                                  CurrentUserService currentUserService) {
        this.recordRepository = recordRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.currentUserService = currentUserService;
    }

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

        return toResponse(recordRepository.save(record));
    }

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

        return toResponse(recordRepository.save(record));
    }

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
