package dev.stockman.maskutils.gson;

import com.google.gson.*;
import dev.stockman.maskutils.core.InvalidJsonException;
import dev.stockman.maskutils.core.JsonMaskingException;
import dev.stockman.maskutils.core.MaskUtils;
import dev.stockman.maskutils.core.MaskingConfiguration;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Utility class for masking sensitive data in JSON content using GSON dependencies.
 * Provides methods to mask both JSON strings and JsonElement objects,
 * replacing values with predefined masks while preserving the JSON structure.
 */
public class MaskUtilsGson implements MaskUtils {

    private final Gson gson;
    private final MaskingConfiguration maskingConfiguration;
    private static final String MASK = "*****";

    /**
     * Constructs a new MaskUtils instance.
     *
     * @param gson the Gson instance to be used for JSON processing
     * @param maskingConfiguration the MaskingConfiguration to be used for masking rules
     * @throws NullPointerException if gson or maskingConfiguration is null
     */
    public MaskUtilsGson(Gson gson, MaskingConfiguration maskingConfiguration) {
        this.gson = Objects.requireNonNull(gson, "Gson cannot be null");
        this.maskingConfiguration = Objects.requireNonNull(maskingConfiguration, "MaskingConfiguration cannot be null");
    }

    @Override
    public String mask(String json) {
        Objects.requireNonNull(json, "Input JSON string cannot be null");
        JsonElement rootElement = validate(json);
        JsonElement maskedElement = mask(rootElement);
        return gson.toJson(maskedElement);
    }

    @Override
    public String mask(Object obj) {
        Objects.requireNonNull(obj, "Input Object cannot be null");
        JsonElement rootElement = gson.toJsonTree(obj);
        JsonElement maskedElement = !rootElement.isJsonPrimitive() ?
                mask(rootElement) :
                maskingConfiguration.shouldMaskScalarRoot() ? mask(rootElement) : rootElement;
        return gson.toJson(maskedElement);
    }

    private JsonElement validate(String json) {
        try {
            return JsonParser.parseString(json);
        } catch (Exception e) {
            throw new InvalidJsonException("Invalid JSON", e);
        }
    }

    private JsonElement mask(JsonElement element) {
        Objects.requireNonNull(element, "Input JsonElement cannot be null");
        try {
            return maskNode(element, "");
        } catch (Exception e) {
            throw new JsonMaskingException("Error masking JsonElement", e);
        }
    }

    private JsonElement maskNode(JsonElement element, String parentNodeName) {
        if (element.isJsonNull()) {
            return JsonNull.INSTANCE;
        } else if (element.isJsonObject()) {
            return maskObject(element.getAsJsonObject());
        } else if (element.isJsonArray()) {
            return maskArray(element.getAsJsonArray(), parentNodeName);
        } else if (maskingConfiguration.shouldMaskScalarRoot()) {
            return maskValueNode(element);
        }
        return element;
    }

    private JsonElement maskValueNode(JsonElement element) {
        if (element.isJsonNull()) {
            return JsonNull.INSTANCE;
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return new JsonPrimitive(false);
            } else if (primitive.isNumber()) {
                return maskNumeric(primitive);
            } else if (primitive.isString()) {
                return new JsonPrimitive(MASK);
            }
        }
        return element;
    }

    private JsonElement maskNumeric(JsonPrimitive element) {
        String numStr = element.getAsString();
        return numStr.contains(".") ?
                new JsonPrimitive(BigDecimal.valueOf(0.0)) :
                new JsonPrimitive(0);
    }

    private JsonElement maskObject(JsonObject object) {
        JsonObject maskedObject = new JsonObject();
        object.entrySet().forEach(entry -> {
            String fieldName = entry.getKey();
            JsonElement fieldElement = entry.getValue();
            maskedObject.add(fieldName, determineNodeMask(fieldElement, fieldName));
        });
        return maskedObject;
    }

    private JsonElement maskArray(JsonArray array, String fieldName) {
        JsonArray maskedArray = new JsonArray(array.size());
        array.forEach(element ->
                maskedArray.add(determineNodeMask(element, fieldName))
        );
        return maskedArray;
    }

    private JsonElement determineNodeMask(JsonElement element, String fieldName) {
        if (element.isJsonPrimitive()) {
            return maskingConfiguration.shouldMask(fieldName) ?
                    maskValueNode(element) :
                    element;
        }

        if (element.isJsonArray()) {
            return maskNode(element, fieldName);
        }

        // For objects, we don't pass the parent field name down
        return maskNode(element, "");
    }
}
