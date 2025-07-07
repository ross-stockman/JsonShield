package dev.stockman.maskutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Deprecated")
public class MaskUtilsTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = JsonHelper.formattedObjectMapper();
    }

    @Test
    void testNullConstructorInputs() {
        assertThrows(NullPointerException.class, () -> new MaskUtils(null, MaskingConfiguration.useWhiteListStrategy().build()));
        assertThrows(NullPointerException.class, () -> new MaskUtils(mapper, null));
    }

    @Test
    void testValidate() {
        MaskUtils maskUtils = new MaskUtils(mapper, MaskingConfiguration.useWhiteListStrategy().build());
        assertThrows(InvalidJsonException.class, () -> maskUtils.mask("{malformed json}"));
    }

    @Test
    void testNullInputs() {
        MaskUtils maskUtils = new MaskUtils(mapper, MaskingConfiguration.useWhiteListStrategy().build());
        assertThrows(NullPointerException.class, () -> maskUtils.mask((String) null));
        assertThrows(NullPointerException.class, () -> maskUtils.mask((Object) null));
    }

    @Test
    void testArrayWithMixedContent_Whitelisted() {
        // Array contains both value nodes and object nodes
        String json = """
        {
            "tags": ["active", "premium", {"type": "special", "detail": "vip"}]
        }""";

        MaskingConfiguration config = MaskingConfiguration.useWhiteListStrategy()
                .addField("tags")
                .build();

        MaskUtils maskUtils = new MaskUtils(mapper, config);
        String masked = maskUtils.mask(json);

        // Value elements should be preserved, object element should be masked
        assertJsonEquals("""
        {
            "tags": ["active", "premium", {"type": "*****", "detail": "*****"}]
        }""", masked);
    }

    @Test
    void testArrayWithMixedContent_NotWhitelisted() {
        String json = """
        {
            "tags": ["active", "premium", {"type": "special", "detail": "vip"}]
        }""";

        MaskingConfiguration config = MaskingConfiguration.useWhiteListStrategy()
                .build(); // empty whitelist

        MaskUtils maskUtils = new MaskUtils(mapper, config);
        String masked = maskUtils.mask(json);

        // Everything should be masked
        assertJsonEquals("""
        {
            "tags": ["*****", "*****", {"type": "*****", "detail": "*****"}]
        }""", masked);
    }

    @Test
    void testArrayWithMixedContent_Blacklisted() {
        String json = """
        {
            "tags": ["active", "premium", {"type": "special", "detail": "vip"}],
            "status": "enabled"
        }""";

        MaskingConfiguration config = MaskingConfiguration.useBlackListStrategy()
                .addField("tags")
                .build(); // tags not in blacklist

        MaskUtils maskUtils = new MaskUtils(mapper, config);
        String masked = maskUtils.mask(json);

        // tags array values should be preserved, object still masked
        assertJsonEquals("""
        {
            "tags": ["*****", "*****", {"type": "special", "detail": "vip"}],
            "status": "enabled"
        }""", masked);
    }

    @Test
    void testNestedArrays_Whitelisted() {
        String json = """
        {
            "matrix": [
                [1, 2, 3],
                [4, 5, 6]
            ],
            "data": {
                "values": [7, 8, 9]
            }
        }""";

        MaskingConfiguration config = MaskingConfiguration.useWhiteListStrategy()
                .addFields("matrix", "values")
                .build();

        MaskUtils maskUtils = new MaskUtils(mapper, config);
        String masked = maskUtils.mask(json);

        assertJsonEquals("""
        {
            "matrix": [[1, 2, 3], [4, 5, 6]],
            "data": {
                "values": [7, 8, 9]
            }
        }""", masked);
    }

    @Test
    void testNestedArrays_Blacklisted() {
        String json = """
        {
            "matrix": [
                [1, 2, 3],
                [4, 5, 6]
            ],
            "data": {
                "values": [7, 8, 9]
            }
        }""";

        MaskingConfiguration config = MaskingConfiguration.useBlackListStrategy()
                .addFields("matrix", "values")
                .build();

        MaskUtils maskUtils = new MaskUtils(mapper, config);
        String masked = maskUtils.mask(json);

        assertJsonEquals("""
        {
            "matrix": [[0, 0, 0], [0, 0, 0]],
            "data": {
                "values": [0, 0, 0]
            }
        }""", masked);
    }

    @Test
    void testObjectsInArray_WithNestedArrays_Whitelisted() {
        String json = """
        {
            "users": [
                {"name": "John", "scores": [10, 20]},
                {"name": "Jane", "scores": [15, 25]}
            ]
        }""";

        MaskingConfiguration config = MaskingConfiguration.useWhiteListStrategy()
                .addFields("users", "scores")
                .build();

        MaskUtils maskUtils = new MaskUtils(mapper, config);
        String masked = maskUtils.mask(json);

        assertJsonEquals("""
        {
            "users": [
                {"name": "*****", "scores": [10, 20]},
                {"name": "*****", "scores": [15, 25]}
            ]
        }""", masked);
    }

    @Test
    void testObjectsInArray_WithNestedArrays_Blacklisted() {
        String json = """
        {
            "users": [
                {"name": "John", "scores": [10, 20]},
                {"name": "Jane", "scores": [15, 25]}
            ]
        }""";

        MaskingConfiguration config = MaskingConfiguration.useBlackListStrategy()
                .addFields("users", "scores")
                .build();

        MaskUtils maskUtils = new MaskUtils(mapper, config);
        String masked = maskUtils.mask(json);

        assertJsonEquals("""
        {
            "users": [
                {"name": "John", "scores": [0, 0]},
                {"name": "Jane", "scores": [0, 0]}
            ]
        }""", masked);
    }

    @Test
    void testValueNodeOnly_Boolean_Whitelisted() {
        String json = "true";

        MaskingConfiguration config = MaskingConfiguration.useWhiteListStrategy()
                .build();

        MaskUtils maskUtils = new MaskUtils(mapper, config);
        String masked = maskUtils.mask(json);

        assertJsonEquals("""
        false""", masked);
    }

    @Test
    void testValueNodeOnly_Boolean_Blacklisted() {
        String json = "true";

        MaskingConfiguration config = MaskingConfiguration.useBlackListStrategy()
                .build();

        MaskUtils maskUtils = new MaskUtils(mapper, config);
        String masked = maskUtils.mask(json);

        assertJsonEquals("""
        true""", masked);
    }

    @Test
    void testValueNodeOnly_Text_Whitelisted() {
        String json = "\"value\"";

        MaskingConfiguration config = MaskingConfiguration.useWhiteListStrategy()
                .build();

        MaskUtils maskUtils = new MaskUtils(mapper, config);
        String masked = maskUtils.mask(json);

        assertJsonEquals("\"*****\"", masked);
    }

    @Test
    void testValueNodeOnly_Text_Blacklisted() {
        String json = "\"value\"";

        MaskingConfiguration config = MaskingConfiguration.useBlackListStrategy()
                .build();

        MaskUtils maskUtils = new MaskUtils(mapper, config);
        String masked = maskUtils.mask(json);

        assertJsonEquals("\"value\"", masked);
    }

    private void allWhitelistAssertions(JsonNode maskedNode) {
        // Test basic field types
        assertEquals("*****", maskedNode.get("id").asText());
        assertEquals("*****", maskedNode.get("timestamp").asText());
        assertFalse(maskedNode.get("active").asBoolean());
        assertEquals(0.0, maskedNode.get("score").asDouble());
        assertEquals("*****", maskedNode.get("name").asText());
        assertEquals(0, maskedNode.get("age").asInt());
        assertEquals(0, maskedNode.get("largeNumber").asLong());
        assertEquals(0.0, maskedNode.get("preciseDecimal").asDouble());

        // Test nested object (contact)
        JsonNode contact = maskedNode.get("contact");
        assertEquals("*****", contact.get("email").asText());
        assertEquals("*****", contact.get("phone").asText());
        assertFalse(contact.get("verified").asBoolean());
        assertEquals(0.0, contact.get("accountBalance").asDouble());

        // Test array of objects (addresses)
        JsonNode addresses = maskedNode.get("addresses");
        assertTrue(addresses.isArray());
        assertEquals(2, addresses.size());
        JsonNode address1 = addresses.get(0);
        assertEquals("*****", address1.get("type").asText());
        assertEquals("*****", address1.get("street").asText());
        assertEquals("*****", address1.get("city").asText());
        assertEquals(0, address1.get("zipCode").asInt());
        assertEquals(0.0, address1.get("propertyValue").asDouble());

        // Test simple arrays
        JsonNode tags = maskedNode.get("tags");
        assertTrue(tags.isArray());
        assertEquals(3, tags.size());
        assertEquals("*****", tags.get(0).asText());
        assertEquals("*****", tags.get(1).asText());
        assertEquals("*****", tags.get(2).asText());

        JsonNode scores = maskedNode.get("scores");
        assertTrue(scores.isArray());
        assertEquals(3, scores.size());
        assertEquals(0, scores.get(0).asInt());
        assertEquals(0, scores.get(1).asInt());
        assertEquals(0, scores.get(2).asInt());

        // Test deeply nested structures
        JsonNode metadata = maskedNode.get("metadata");
        assertEquals("*****", metadata.get("lastUpdated").asText());
        assertEquals(0, metadata.get("version").asInt());
        assertFalse(metadata.get("settings").get("notifications").asBoolean());
        assertEquals("*****", metadata.get("settings").get("theme").asText());

        // Test mixed array content
        JsonNode mixed = maskedNode.get("payment").get("mixed");
        assertTrue(mixed.isArray());
        assertEquals(4, mixed.size());
        assertEquals("*****", mixed.get(0).asText());
        assertEquals(0, mixed.get(1).asInt());
        assertFalse(mixed.get(2).asBoolean());
        assertEquals("*****", mixed.get(3).get("nestedKey").asText());
        assertEquals(0, mixed.get(3).get("hugeValue").asLong());

        // Test special cases
        assertTrue(maskedNode.get("nullField").isNull());
        assertTrue(maskedNode.get("emptyObject").isEmpty());
        assertTrue(maskedNode.get("emptyArray").isEmpty());

    }

    private void allBlacklistAssertions(JsonNode maskedNode) {
        // Test basic field types
        assertEquals("ABC123", maskedNode.get("id").asText());
        assertEquals("2025-07-01T12:34:56.789Z", maskedNode.get("timestamp").asText());
        assertTrue(maskedNode.get("active").asBoolean());
        assertEquals(95.5, maskedNode.get("score").asDouble());
        assertEquals("John Doe", maskedNode.get("name").asText());
        assertEquals(30, maskedNode.get("age").asInt());
        assertEquals(new BigInteger("9223372036854775808"), maskedNode.get("largeNumber").bigIntegerValue());
        assertEquals("123456.789", maskedNode.get("preciseDecimal").toString());
        assertEquals(new BigDecimal("123456.789"), maskedNode.get("preciseDecimal").decimalValue());


        // Test nested object (contact)
        JsonNode contact = maskedNode.get("contact");
        assertEquals("john@example.com", contact.get("email").asText());
        assertEquals("123-456-7890", contact.get("phone").asText());
        assertFalse(contact.get("verified").asBoolean());
        assertEquals(new BigDecimal("999999.99"), contact.get("accountBalance").decimalValue());

        // Test array of objects (addresses)
        JsonNode addresses = maskedNode.get("addresses");
        assertTrue(addresses.isArray());
        assertEquals(2, addresses.size());
        JsonNode address1 = addresses.get(0);
        assertEquals("home", address1.get("type").asText());
        assertEquals("123 Main St", address1.get("street").asText());
        assertEquals("Springfield", address1.get("city").asText());
        assertEquals(12345, address1.get("zipCode").asInt());
        assertEquals(new BigDecimal("1234567.89"), address1.get("propertyValue").decimalValue());

        // Test simple arrays
        JsonNode tags = maskedNode.get("tags");
        assertTrue(tags.isArray());
        assertEquals(3, tags.size());
        assertEquals("premium", tags.get(0).asText());
        assertEquals("verified", tags.get(1).asText());
        assertEquals("active", tags.get(2).asText());

        JsonNode scores = maskedNode.get("scores");
        assertTrue(scores.isArray());
        assertEquals(3, scores.size());
        assertEquals(88, scores.get(0).asInt());
        assertEquals(92, scores.get(1).asInt());
        assertEquals(95, scores.get(2).asInt());

        // Test deeply nested structures
        JsonNode metadata = maskedNode.get("metadata");
        assertEquals("2025-07-01", metadata.get("lastUpdated").asText());
        assertEquals(2, metadata.get("version").asInt());
        assertTrue(metadata.get("settings").get("notifications").asBoolean());
        assertEquals("dark", metadata.get("settings").get("theme").asText());

        // Test mixed array content
        JsonNode mixed = maskedNode.get("payment").get("mixed");
        assertTrue(mixed.isArray());
        assertEquals(4, mixed.size());
        assertEquals("secret1", mixed.get(0).asText());
        assertEquals(42, mixed.get(1).asInt());
        assertTrue(mixed.get(2).asBoolean());
        assertEquals("nestedValue", mixed.get(3).get("nestedKey").asText());
        assertEquals(new BigInteger("340282366920938463463374607431768211455"), mixed.get(3).get("hugeValue").bigIntegerValue());

        // Test special cases
        assertTrue(maskedNode.get("nullField").isNull());
        assertTrue(maskedNode.get("emptyObject").isEmpty());
        assertTrue(maskedNode.get("emptyArray").isEmpty());

    }

    // Helper method to handle JSON parsing in tests
    private JsonNode readTree(String json) {
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON in test", e);
        }
    }

    private void assertJsonEquals(String expected, String actual) {
        try {
            JsonNode expectedNode = mapper.readTree(expected);
            JsonNode actualNode = mapper.readTree(actual);
            assertEquals(expectedNode, actualNode,
                    "JSON strings are not equal.\nExpected: " + expectedNode + "\nActual: " + actualNode);
        } catch (JsonProcessingException e) {
            fail("Failed to parse JSON: " + e.getMessage());
        }
    }


}

