package dev.stockman.maskutils.model;

public record Metadata(
        String lastUpdated,
        int version,
        Settings settings
) {
}
