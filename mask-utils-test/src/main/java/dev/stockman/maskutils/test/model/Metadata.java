package dev.stockman.maskutils.test.model;

public record Metadata(
        String lastUpdated,
        int version,
        Settings settings
) {
}
