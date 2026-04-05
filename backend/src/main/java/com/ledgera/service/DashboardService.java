package com.ledgera.service;

import com.ledgera.dto.*;
import com.ledgera.entity.FinancialRecord;
import com.ledgera.entity.User;
import com.ledgera.enums.Role;
import com.ledgera.enums.TransactionType;
import com.ledgera.exception.ForbiddenException;
import com.ledgera.repository.FinancialRecordRepository;
import com.ledgera.repository.UserRepository;
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
    private final CurrentUserService currentUserService;

    public DashboardService(FinancialRecordRepository recordRepository, UserRepository userRepository, CurrentUserService currentUserService) {
        this.recordRepository = recordRepository;
        this.currentUserService = currentUserService;
    }

    public DashboardResponse getDashboardData() {
        User currentUser = currentUserService.requireCurrentUser();
        // viewers only see their own aggregates
        boolean restrictToUser = currentUser.getRole() == Role.VIEWER;

        BigDecimal totalIncome = restrictToUser
                ? recordRepository.sumByTypeAndUser(TransactionType.INCOME, currentUser.getId())
                : recordRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = restrictToUser
                ? recordRepository.sumByTypeAndUser(TransactionType.EXPENSE, currentUser.getId())
                : recordRepository.sumByType(TransactionType.EXPENSE);
        if (totalIncome == null) {
            // default null aggregates to zero for math
            totalIncome = BigDecimal.ZERO;
        }
        if (totalExpenses == null) {
            // default null aggregates to zero for math
            totalExpenses = BigDecimal.ZERO;
        }
        // calculate net balance for the dashboard
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        List<CategoryTotal> categoryTotals = buildCategoryTotals(restrictToUser ? currentUser.getId() : null);
        List<MonthlyTrend> monthlyTrends = buildMonthlyTrends(restrictToUser ? currentUser.getId() : null);
        List<FinancialRecordResponse> recentTransactions = buildRecentTransactions(restrictToUser ? currentUser.getId() : null);

        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .monthlyTrends(monthlyTrends)
                .recentTransactions(recentTransactions)
                .build();
    }

    private List<CategoryTotal> buildCategoryTotals(Long userId) {
        List<Object[]> results = userId == null
                ? recordRepository.getCategoryTotalsByType()
                : recordRepository.getCategoryTotalsByTypeAndUser(userId);
        Map<String, CategoryTotal> categoryMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            // skip malformed rows from native aggregation
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
                // ignore rows with missing transaction type
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

    private List<MonthlyTrend> buildMonthlyTrends(Long userId) {
        List<Object[]> results = userId == null
                ? recordRepository.getMonthlyTrends()
                : recordRepository.getMonthlyTrendsByUser(userId);
        Map<String, MonthlyTrend> trendMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            // skip malformed rows from native aggregation
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
                // ignore rows missing a valid time bucket
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

    private List<FinancialRecordResponse> buildRecentTransactions(Long userId) {
        List<FinancialRecord> records = userId == null
                ? recordRepository.findTop10ByOrderByDateDescIdDesc()
                : recordRepository.findTop10ByUserIdOrderByDateDescIdDesc(userId);
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
