package dev.stockman.jsonshield.core;

import java.util.*;

/**
 * Configuration for field masking strategy and field sets.
 * Supports both whitelist (show only specified fields) and blacklist (mask only specified fields) strategies.
 */
public class JsonShieldConfiguration {
    private final Set<String> fields;
    private final Strategy strategy;
    private final String stringMask;
    private final Double decimalMask;
    private final Boolean booleanMask;
    private final Integer numberMask;

    private JsonShieldConfiguration(Builder builder) {
        this.fields = Set.copyOf(builder.fields);
        this.strategy = builder.strategy;
        this.stringMask = builder.stringMask;
        this.decimalMask = builder.decimalMask;
        this.booleanMask = builder.booleanMask;
        this.numberMask = builder.numberMask;
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
        return (strategy == Strategy.WHITELIST) != fields.contains(fieldName);
    }

    /**
     * Determines if the root of a JSON object should be masked based on the masking strategy.
     * @return true if the root should be masked, false if it should be unmasked
     */
    public boolean shouldMaskScalarRoot() {
        return strategy == Strategy.WHITELIST;
    }

    /**
     * Retrieves the string value used for masking textual fields.
     *
     * @return the string mask applied to string fields during masking
     */
    public String getStringMask() {
        return stringMask;
    }

    /**
     * Retrieves the decimal value used for masking fields with decimal values.
     * This value is applied to fields of type Double during the masking process.
     *
     * @return the decimal mask applied to decimal fields
     */
    public Double getDecimalMask() {
        return decimalMask;
    }

    /**
     * Retrieves the boolean value used for masking Boolean fields.
     *
     * @return the boolean mask applied to Boolean fields during masking
     */
    public Boolean getBooleanMask() {
        return booleanMask;
    }

    /**
     * Retrieves the integer value used for masking number fields.
     *
     * @return the number mask applied to integer fields during masking
     */
    public Integer getNumberMask() {
        return numberMask;
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
        private String stringMask = MaskConstants.DEFAULT_STRING_MASK;
        private Integer numberMask = MaskConstants.DEFAULT_NUMBER_MASK;
        private Boolean booleanMask = MaskConstants.DEFAULT_BOOLEAN_MASK;
        private Double decimalMask = MaskConstants.DEFAULT_DECIMAL_MASK;

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
         * Sets the String to be used when masking a textual field
         * @param stringMask the string to use
         * @return the current builder instance, for method chaining
         */
        public Builder withStringMask(String stringMask) {
            this.stringMask = stringMask;
            return this;
        }

        /**
         * Sets the number to be used when masking an integer field
         * @param numberMask the number to use
         * @return the current builder instance, for method chaining
         */
        public Builder withNumberMask(Integer numberMask) {
            this.numberMask = numberMask;
            return this;
        }

        /**
         * Sets the boolean to be used when masking a boolean field
         * @param booleanMask the boolean to use
         * @return the current builder instance, for method chaining
         */
        public Builder withBooleanMask(Boolean booleanMask) {
            this.booleanMask = booleanMask;
            return this;
        }

        /**
         * Sets the double to be used when masking a decimal field
         * @param decimalMask the decimal to use
         * @return the current builder instance, for method chaining
         */
        public Builder withDecimalMask(Double decimalMask) {
            this.decimalMask = decimalMask;
            return this;
        }

        /**
         * Creates a MaskingConfiguration instance from the current builder state.
         * @return a new MaskingConfiguration instance with the configured strategy and fields
         */
        public JsonShieldConfiguration build() {
            return new JsonShieldConfiguration(this);
        }
    }

}
