package dev.stockman.maskutils.model;

import java.math.BigInteger;

public record NestedMixed(
        String nestedKey,
        BigInteger hugeValue
) {
}
