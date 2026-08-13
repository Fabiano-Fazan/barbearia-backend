package com.barbearia.financial.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record FinancialSummaryDTO(

        @Schema(description = "Total incomes for the period")
        BigDecimal totalIncomes,

        @Schema(description = "Total expenses for the period")
        BigDecimal totalExpenses,

        @Schema(description = "Net balance for the period")
        BigDecimal netBalance,

        @Schema(description = "Pending commissions for the period")
        BigDecimal pendingCommissions,

        @Schema(description = "Total services rendered for the period")
        long totalServiceRendered

) {
}
