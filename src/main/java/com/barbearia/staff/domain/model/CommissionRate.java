package com.barbearia.staff.domain.model;

import java.math.BigDecimal;

public record CommissionRate(BigDecimal value) {
    public CommissionRate {
        if (value == null || value.signum() < 0 || value.compareTo(new BigDecimal("100")) > 0)
            throw new IllegalArgumentException("Commission rate must be between 0 and 100");
    }
}
