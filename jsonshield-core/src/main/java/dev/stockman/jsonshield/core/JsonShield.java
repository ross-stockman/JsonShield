package dev.stockman.jsonshield.core;

public interface JsonShield {
    /**
     * Masks sensitive data in a JSON string by replacing values with predefined masks.
     * The following masking rules are applied:
     * <ul>
     *     <li>String values are replaced with "*****"</li>
     *     <li>Numbers are replaced with 0 (integers) or 0.0 (floating-point)</li>
     *     <li>Boolean values are replaced with false</li>
     *     <li>Null values remain null</li>
     *     <li>Arrays and objects are traversed recursively</li>
     * </ul>
     *
     * @param json the JSON string to be masked
     * @return a new JSON string with masked values
     * @throws NullPointerException if the input JSON string is null
     * @throws InvalidJsonException if the input is not valid JSON
     * @throws JsonShieldException if an error occurs during the masking process
     */
    String mask(String json);

    /**
     * Masks sensitive data in a Java object by converting it to JSON and applying masking rules.
     * The following masking rules are applied:
     * <ul>
     *     <li>String values are replaced with "*****"</li>
     *     <li>Numbers are replaced with 0 (integers) or 0.0 (floating-point)</li>
     *     <li>Boolean values are replaced with false</li>
     *     <li>Null values remain null</li>
     *     <li>Arrays and objects are traversed recursively</li>
     * </ul>
     *
     * @param obj the object to be masked
     * @return a JSON string with masked values
     * @throws NullPointerException if the input object is null
     * @throws JsonShieldException if an error occurs during the masking process
     */
    String mask(Object obj);
}
