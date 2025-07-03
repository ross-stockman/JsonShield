package dev.stockman.maskutils;

import java.util.*;

/**
 * Configuration for field masking strategy and field sets.
 * Supports both whitelist (show only specified fields) and blacklist (mask only specified fields) strategies.
 */
public class MaskingConfiguration {
    private final Set<String> fields;
    private final Strategy strategy;

    private MaskingConfiguration(Builder builder) {
        this.fields = Set.copyOf(builder.fields);
        this.strategy = builder.strategy;
    }

    /**
     * Determines if a field's value should be masked based on the masking strategy.
     * For whitelist strategy: unmask only if field is in the whitelist
     * For blacklist strategy: mask if field is not in the blacklist
     *
     * @param fieldName the name of the field to check
     * @return true if the field's value should be masked, false if it should be unmasked
     */
    public boolean shouldMask(String fieldName) {
        if (strategy == Strategy.WHITELIST) {
            return !fields.contains(fieldName);
        }
        return fields.contains(fieldName);
    }

    /**
     * Creates a builder for whitelist strategy where only specified fields will be shown unmasked.
     * @return a new builder instance configured for whitelist strategy
     */
    public static Builder useWhiteListStrategy() {
        return new Builder(Strategy.WHITELIST);
    }

    /**
     * Creates a builder for blacklist strategy where only specified fields will be masked.
     * @return a new builder instance configured for blacklist strategy
     */
    public static Builder useBlackListStrategy() {
        return new Builder(Strategy.BLACKLIST);
    }

    /**
     * Builder for MaskingConfiguration instances.
     */
    public static class Builder {
        private final Set<String> fields = new HashSet<>();
        private final Strategy strategy;
        Builder(Strategy strategy) {
            this.strategy = strategy;
        }

        /**
         * Adds a field to the configured set.
         * @param fieldName the name of the field to be added to the configured set
         * @return the current builder instance, for method chaining
         */
        public Builder addField(String fieldName) {
            fields.add(fieldName);
            return this;
        }

        /**
         * Adds a set of fields to the configured set.
         * @param fieldNames the names of the fields to be added to the configured set
         * @return the current builder instance, for method chaining
         */
        public Builder addFields(String... fieldNames) {
            fields.addAll(Arrays.asList(fieldNames));
            return this;
        }

        /**
         * Adds a set of fields to the configured set.
         * @param fieldNames the names of the fields to be added to the configured set
         * @return the current builder instance, for method chaining
         */
        public Builder addFields(Collection<String> fieldNames) {
            fields.addAll(fieldNames);
            return this;
        }

        /**
         * Creates a MaskingConfiguration instance from the current builder state.
         * @return a new MaskingConfiguration instance with the configured strategy and fields
         */
        public MaskingConfiguration build() {
            return new MaskingConfiguration(this);
        }
    }

}
