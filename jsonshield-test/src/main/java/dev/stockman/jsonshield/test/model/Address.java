package dev.stockman.jsonshield.test.model;

import java.math.BigDecimal;
import java.math.BigInteger;

public record Address(
        String type,
        String street,
        String city,
        int zipCode,
        BigDecimal propertyValue,
        BigInteger buildingNumber
) {
}
