package dev.stockman.jsonshield.test.model;

public record Metadata(
        String lastUpdated,
        int version,
        Settings settings
) {
}
