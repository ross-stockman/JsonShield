package dev.stockman.jsonshield.test.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record TestData(
        String id,
        Instant timestamp,
        boolean active,
        double score,
        String name,
        int age,
        BigInteger largeNumber,
        BigDecimal preciseDecimal,
        Contact contact,
        List<Address> addresses,
        List<String> tags,
        List<Integer> scores,
        Metadata metadata,
        Payment payment,
        Object nullField,
        Map<String, Object> emptyObject,
        List<Object> emptyArray
) {
}
