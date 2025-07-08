package dev.stockman.maskutils.test.model;

import java.math.BigDecimal;

public record Contact(
        String email,
        String phone,
        boolean verified,
        BigDecimal accountBalance
) {
}
