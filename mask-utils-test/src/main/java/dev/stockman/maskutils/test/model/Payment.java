package dev.stockman.maskutils.test.model;

import java.math.BigDecimal;
import java.util.List;

public record Payment(
        String cardNumber,
        String cvv,
        BigDecimal amount,
        List<Object> mixed
) {
}
