package dev.stockman.maskutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;


public class MaskUtilsTest {

    // language=JSON
    private static final String sampleUnmaskedJson = """
        {
          "id": "ABC123",
          "timestamp": "2025-07-01T12:34:56.789Z",
          "active": true,
          "score": 95.5,
          "name": "John Doe",
          "age": 30,
          "largeNumber": 9223372036854775808,
          "preciseDecimal": 123456789.123456789,
          "contact": {
            "email": "john@example.com",
            "phone": "123-456-7890",
            "verified": false,
            "accountBalance": 9999999999999999999.99
          },
          "addresses": [
            {
              "type": "home",
              "street": "123 Main St",
              "city": "Springfield",
              "zipCode": 12345,
              "propertyValue": 1234567890123456789.12345
            },
            {
              "type": "work",
              "street": "456 Corp Ave",
              "city": "Business City",
              "zipCode": 67890,
              "buildingNumber": 18446744073709551615
            }
          ],
          "tags": ["premium", "verified", "active"],
          "scores": [88, 92, 95],
          "metadata": {
            "lastUpdated": "2025-07-01",
            "version": 2,
            "settings": {
              "notifications": true,
              "theme": "dark"
            }
          },
          "payment": {
            "cardNumber": "4111111111111111",
            "cvv": "123",
            "amount": 999999999999999.999999,
            "mixed": [
              "secret1",
              42,
              true,
              {
                "nestedKey": "nestedValue",
                "hugeValue": 340282366920938463463374607431768211455
              }
            ]
          },
          "nullField": null,
          "emptyObject": {},
          "emptyArray": []
        }
        """;

    private final TestData sampleUnmaskedObject = new TestData(
            "ABC123",
            Instant.parse("2025-07-01T12:34:56.789Z"),
            true,
            95.5,
            "John Doe",
            30,
            new BigInteger("9223372036854775808"),
            new BigDecimal("123456789.123456789"),
            new Contact(
                    "john@example.com",
                    "123-456-7890",
                    false,
                    new BigDecimal("9999999999999999999.99")
            ),
            List.of(
                    new Address(
                            "home",
                            "123 Main St",
                            "Springfield",
                            12345,
                            new BigDecimal("1234567890123456789.12345"),
                            null
                    ),
                    new Address(
                            "work",
                            "456 Corp Ave",
                            "Business City",
                            67890,
                            null,
                            new BigInteger("18446744073709551615")
                    )
            ),
            List.of("premium", "verified", "active"),
            List.of(88, 92, 95),
            new Metadata(
                    "2025-07-01",
                    2,
                    new Settings(true, "dark")
            ),
            new Payment(
                    "4111111111111111",
                    "123",
                    new BigDecimal("999999999999999.999999"),
                    List.of(
                            "secret1",
                            42,
                            true,
                            new NestedMixed(
                                    "nestedValue",
                                    new BigInteger("340282366920938463463374607431768211455")
                            )
                    )
            ),
            null,
            new HashMap<>(),
            new ArrayList<>()
    );


    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = JsonConfiguration.formattedObjectMapper();
    }

    @Test
    void testValidate() {
        MaskUtils maskUtils = new MaskUtils(mapper);
        assertThrows(InvalidJsonException.class, () -> maskUtils.mask("{malformed json}"));
    }

    @Test
    void testNullInputs() {
        MaskUtils maskUtils = new MaskUtils(mapper);
        assertThrows(NullPointerException.class, () -> maskUtils.mask((String) null));
        assertThrows(NullPointerException.class, () -> maskUtils.mask((Object) null));
    }

    @Test
    void testMaskByString() {
        MaskUtils maskUtils = new MaskUtils(mapper);
        String maskedJson = maskUtils.mask(sampleUnmaskedJson);
        JsonNode maskedNode = readTree(maskedJson);
        allAssertions(maskedNode);
    }

    @Test
    void testMaskByObject() {
        MaskUtils maskUtils = new MaskUtils(mapper);
        String maskedJson = maskUtils.mask(sampleUnmaskedObject);
        JsonNode maskedNode = readTree(maskedJson);
        allAssertions(maskedNode);
    }

    private void allAssertions(JsonNode maskedNode) {
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

    // Helper method to handle JSON parsing in tests
    private JsonNode readTree(String json) {
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON in test", e);
        }
    }

}

record TestData(
        String id,
        Instant timestamp,
        boolean active,
        double score,
        String name,
        int age,
        BigInteger largeNumber,
        BigDecimal preciseDecimal,
        Contact contact,
        List<Address> addresses,
        List<String> tags,
        List<Integer> scores,
        Metadata metadata,
        Payment payment,
        Object nullField,
        Map<String, Object> emptyObject,
        List<Object> emptyArray
) {}

record Contact(
        String email,
        String phone,
        boolean verified,
        BigDecimal accountBalance
) {}

record Address(
        String type,
        String street,
        String city,
        int zipCode,
        BigDecimal propertyValue,
        BigInteger buildingNumber
) {}

record Metadata(
        String lastUpdated,
        int version,
        Settings settings
) {}

record Settings(
        boolean notifications,
        String theme
) {}

record Payment(
        String cardNumber,
        String cvv,
        BigDecimal amount,
        List<Object> mixed
) {}

record NestedMixed(
        String nestedKey,
        BigInteger hugeValue
) {}

