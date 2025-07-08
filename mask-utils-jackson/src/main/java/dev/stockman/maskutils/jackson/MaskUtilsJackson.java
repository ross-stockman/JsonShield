package dev.stockman.maskutils.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import dev.stockman.maskutils.core.InvalidJsonException;
import dev.stockman.maskutils.core.JsonMaskingException;
import dev.stockman.maskutils.core.MaskUtils;
import dev.stockman.maskutils.core.MaskingConfiguration;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Utility class for masking sensitive data in JSON content using Jackson dependencies.
 * Provides methods to mask both JSON strings and JsonNode objects,
 * replacing values with predefined masks while preserving the JSON structure.
 */
public class MaskUtilsJackson implements MaskUtils {

    private final ObjectMapper mapper;
    private final MaskingConfiguration maskingConfiguration;
    private static final String MASK = "*****";

    /**
     * Constructs a new MaskUtils instance.
     *
     * @param mapper the ObjectMapper to be used for JSON processing
     * @param maskingConfiguration the MaskingConfiguration to be used for masking rules
     * @throws NullPointerException if mapper or maskingConfiguration is null
     */
    public MaskUtilsJackson(ObjectMapper mapper, MaskingConfiguration maskingConfiguration) {
        this.mapper = Objects.requireNonNull(mapper, "ObjectMapper cannot be null");
        this.maskingConfiguration = Objects.requireNonNull(maskingConfiguration, "MaskingConfiguration cannot be null");
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
            return maskNode(node, "");
        } catch (Exception e) {
            throw new JsonMaskingException("Error masking JsonNode", e);
        }
    }

    @Override
    public String mask(String json) {
        Objects.requireNonNull(json, "Input JSON string cannot be null");
        JsonNode rootNode = validate(json);
        JsonNode maskedNode = mask(rootNode);
        return writeValueAsString(maskedNode);
    }

    @Override
    public String mask(Object obj) {
        Objects.requireNonNull(obj, "Input Object cannot be null");
        JsonNode rootNode = mapper.valueToTree(obj);
        JsonNode maskedNode = !rootNode.isValueNode() ? mask(rootNode) : maskingConfiguration.shouldMaskScalarRoot() ? mask(rootNode) : rootNode;
        return writeValueAsString(maskedNode);
    }


    private JsonNode maskNode(JsonNode node, String parentNodeName) {
        if (node.isNull()) {
            return NullNode.getInstance();
        } else if (node.isObject()) {
            return maskObject(node);
        } else if (node.isArray()) {
            return maskArray(node, parentNodeName);
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

    private JsonNode maskValueNode(JsonNode node) {
        if (node.isNull()) {
            return NullNode.getInstance();
        } else if (node.isBoolean()) {
            return BooleanNode.valueOf(false);
        } else if (node.isNumber()) {
            return maskNumeric(node);
        } else if (node.isTextual()) {
            return TextNode.valueOf(MASK);
        }
        return node;
    }

    private JsonNode maskNumeric(JsonNode node) {
        return node.isBigDecimal() || node.isFloatingPointNumber()
                ? DecimalNode.valueOf(BigDecimal.valueOf(0.0))
                : IntNode.valueOf(0);
    }

    private JsonNode maskObject(JsonNode node) {
        ObjectNode maskedObject = mapper.createObjectNode();
        node.fieldNames().forEachRemaining(fieldName -> {
            JsonNode fieldNode = node.get(fieldName);
            maskedObject.set(fieldName, determineNodeMask(fieldNode, fieldName));
        });
        return maskedObject;
    }


    private JsonNode maskArray(JsonNode node, String fieldName) {
        ArrayNode maskedArray = mapper.createArrayNode();
        node.elements().forEachRemaining(element ->
                maskedArray.add(determineNodeMask(element, fieldName))
        );
        return maskedArray;
    }

    private JsonNode determineNodeMask(JsonNode node, String fieldName) {
        if (node.isValueNode()) {
            return maskingConfiguration.shouldMask(fieldName) ?
                    maskValueNode(node) :
                    node;
        }

        if (node.isArray()) {
            return maskNode(node, fieldName);
        }

        // For objects, we don't pass the parent field name down
        return maskNode(node, "");
    }

}
