package dev.stockman.maskutils.model;

import java.math.BigDecimal;

public record Contact(
        String email,
        String phone,
        boolean verified,
        BigDecimal accountBalance
) {
}
