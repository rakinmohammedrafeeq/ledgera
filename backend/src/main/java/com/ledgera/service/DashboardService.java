package com.ledgera.service;

import com.ledgera.dto.*;
import com.ledgera.entity.FinancialRecord;
import com.ledgera.entity.User;
import com.ledgera.entity.Workspace;
import com.ledgera.enums.Role;
import com.ledgera.enums.TransactionType;
import com.ledgera.enums.WorkspacePermission;
import com.ledgera.exception.ForbiddenException;
import com.ledgera.repository.FinancialRecordRepository;
import com.ledgera.repository.UserRepository;
import com.ledgera.repository.WorkspaceMemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final FinancialRecordRepository recordRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final CurrentUserService currentUserService;

    public DashboardService(FinancialRecordRepository recordRepository, 
                           UserRepository userRepository,
                           WorkspaceMemberRepository workspaceMemberRepository,
                           CurrentUserService currentUserService) {
        this.recordRepository = recordRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.currentUserService = currentUserService;
    }

    public DashboardResponse getDashboardData() {
        User currentUser = currentUserService.requireCurrentUser();
        
        // Get current workspace
        Workspace workspace = currentUser.getCurrentWorkspace();
        if (workspace == null) {
            throw new ForbiddenException("No workspace selected");
        }
        
        // Check workspace access
        workspaceMemberRepository
                .findPermissionByWorkspaceAndUser(workspace.getId(), currentUser.getId())
                .orElseThrow(() -> new ForbiddenException("You don't have access to this workspace"));
        
        // All data is scoped to current workspace
        Long workspaceId = workspace.getId();

        BigDecimal totalIncome = recordRepository.sumByTypeAndWorkspace(TransactionType.INCOME, workspaceId);
        BigDecimal totalExpenses = recordRepository.sumByTypeAndWorkspace(TransactionType.EXPENSE, workspaceId);
        
        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }
        if (totalExpenses == null) {
            totalExpenses = BigDecimal.ZERO;
        }
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        List<CategoryTotal> categoryTotals = buildCategoryTotals(workspaceId);
        List<MonthlyTrend> monthlyTrends = buildMonthlyTrends(workspaceId);
        List<FinancialRecordResponse> recentTransactions = buildRecentTransactions(workspaceId);

        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .monthlyTrends(monthlyTrends)
                .recentTransactions(recentTransactions)
                .build();
    }

    private List<CategoryTotal> buildCategoryTotals(Long workspaceId) {
        List<Object[]> results = recordRepository.getCategoryTotalsByTypeAndWorkspace(workspaceId);
        Map<String, CategoryTotal> categoryMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            if (row == null || row.length < 3) {
                continue;
            }
            String category = row[0] != null ? row[0].toString() : "Uncategorized";
            TransactionType type = row[1] instanceof TransactionType
                    ? (TransactionType) row[1]
                    : null;
            BigDecimal amount = row[2] instanceof BigDecimal
                    ? (BigDecimal) row[2]
                    : BigDecimal.ZERO;
            if (type == null) {
                continue;
            }

            CategoryTotal ct = categoryMap.computeIfAbsent(category,
                    k -> CategoryTotal.builder()
                            .category(k)
                            .total(BigDecimal.ZERO)
                            .income(BigDecimal.ZERO)
                            .expense(BigDecimal.ZERO)
                            .build());

            if (type == TransactionType.INCOME) {
                ct.setIncome(amount);
            } else {
                ct.setExpense(amount);
            }
            ct.setTotal(ct.getIncome().subtract(ct.getExpense()));
        }

        return new ArrayList<>(categoryMap.values());
    }

    private List<MonthlyTrend> buildMonthlyTrends(Long workspaceId) {
        List<Object[]> results = recordRepository.getMonthlyTrendsByWorkspace(workspaceId);
        Map<String, MonthlyTrend> trendMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            if (row == null || row.length < 4) {
                continue;
            }
            int year = row[0] instanceof Number ? ((Number) row[0]).intValue() : 0;
            int month = row[1] instanceof Number ? ((Number) row[1]).intValue() : 0;
            TransactionType type = row[2] instanceof TransactionType
                    ? (TransactionType) row[2]
                    : null;
            BigDecimal amount = row[3] instanceof BigDecimal
                    ? (BigDecimal) row[3]
                    : BigDecimal.ZERO;
            if (type == null || year == 0 || month == 0) {
                continue;
            }

            String key = year + "-" + month;
            String monthName = month >= 1 && month <= 12
                    ? Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    : "N/A";
            MonthlyTrend trend = trendMap.computeIfAbsent(key,
                    k -> MonthlyTrend.builder()
                            .year(year)
                            .month(month)
                            .monthName(monthName)
                            .income(BigDecimal.ZERO)
                            .expense(BigDecimal.ZERO)
                            .build());

            if (type == TransactionType.INCOME) {
                trend.setIncome(amount);
            } else {
                trend.setExpense(amount);
            }
        }

        return new ArrayList<>(trendMap.values());
    }

    private List<FinancialRecordResponse> buildRecentTransactions(Long workspaceId) {
        List<FinancialRecord> records = recordRepository.findTop10ByWorkspaceIdOrderByDateDescIdDesc(workspaceId);
        return records.stream().map(record -> FinancialRecordResponse.builder()
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
                .build()).collect(Collectors.toList());
    }
}
