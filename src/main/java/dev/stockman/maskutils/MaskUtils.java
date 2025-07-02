package dev.stockman.maskutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Utility class for masking sensitive data in JSON content.
 * Provides methods to mask both JSON strings and JsonNode objects,
 * replacing values with predefined masks while preserving the JSON structure.
 */
public class MaskUtils {

    private final ObjectMapper mapper;
    private static final String MASK = "*****";

    /**
     * Constructs a new MaskUtils instance.
     *
     * @param mapper the ObjectMapper to be used for JSON processing
     * @throws NullPointerException if mapper is null
     */
    public MaskUtils(ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper, "ObjectMapper cannot be null");
    }

    private JsonNode validate(String json) {
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            throw new InvalidJsonException("Invalid JSON", e);
        }
    }

    private String writeValueAsString(JsonNode node) {
        try {
            return mapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new JsonMaskingException("Error masking JsonNode", e);
        }
    }

    private JsonNode mask(JsonNode node) {
        Objects.requireNonNull(node, "Input JsonNode cannot be null");
        try {
            return maskNode(node);
        } catch (Exception e) {
            throw new JsonMaskingException("Error masking JsonNode", e);
        }
    }

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
     * @throws JsonMaskingException if an error occurs during the masking process
     */
    public String mask(String json) {
        Objects.requireNonNull(json, "Input JSON string cannot be null");
        JsonNode rootNode = validate(json);
        JsonNode maskedNode = mask(rootNode);
        return writeValueAsString(maskedNode);
    }

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
     * @throws JsonMaskingException if an error occurs during the masking process
     */
    public String mask(Object obj) {
        Objects.requireNonNull(obj, "Input Object cannot be null");
        JsonNode rootNode = mapper.valueToTree(obj);
        JsonNode maskedNode = mask(rootNode);
        return writeValueAsString(maskedNode);
    }


    private JsonNode maskNode(JsonNode node) {
        if (node.isNull()) {
            return NullNode.getInstance();
        } else if (node.isObject()) {
            return maskObject(node);
        } else if (node.isArray()) {
            return maskArray(node);
        } else if (node.isBoolean()) {
            return BooleanNode.valueOf(false);
        } else if (node.isNumber()) {
            return maskNumeric(node);
        } else if (node.isTextual()) {
            return TextNode.valueOf(MASK);
        }
        // For any other type, return as is
        return node;
    }

    private JsonNode maskNumeric(JsonNode node) {
        return node.isBigDecimal() || node.isFloatingPointNumber()
                ? DecimalNode.valueOf(BigDecimal.valueOf(0.0))
                : IntNode.valueOf(0);
    }

    private JsonNode maskObject(JsonNode node) {
        ObjectNode maskedObject = mapper.createObjectNode();
        node.fieldNames().forEachRemaining(fieldName ->
                maskedObject.set(fieldName, maskNode(node.get(fieldName)))
        );
        return maskedObject;
    }

    private JsonNode maskArray(JsonNode node) {
        ArrayNode maskedArray = mapper.createArrayNode();
        node.elements().forEachRemaining(element ->
                maskedArray.add(maskNode(element))
        );
        return maskedArray;
    }
}
