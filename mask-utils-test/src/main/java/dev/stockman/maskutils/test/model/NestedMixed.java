package dev.stockman.maskutils.test.model;

import java.math.BigInteger;

public record NestedMixed(
        String nestedKey,
        BigInteger hugeValue
) {
}
