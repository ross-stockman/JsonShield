package dev.stockman.maskutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.stockman.maskutils.model.*;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static dev.stockman.maskutils.JsonHelper.readTree;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("Complex schema tests")
public class MaskUtilsComplexSchemaTest {

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
          "preciseDecimal": 123456.789,
          "contact": {
            "email": "john@example.com",
            "phone": "123-456-7890",
            "verified": false,
            "accountBalance": 999999.99
          },
          "addresses": [
            {
              "type": "home",
              "street": "123 Main St",
              "city": "Springfield",
              "zipCode": 12345,
              "propertyValue": 1234567.89
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
            new BigDecimal("123456.789"),
            new Contact(
                    "john@example.com",
                    "123-456-7890",
                    false,
                    new BigDecimal("999999.99")
            ),
            List.of(
                    new Address(
                            "home",
                            "123 Main St",
                            "Springfield",
                            12345,
                            new BigDecimal("1234567.89"),
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
        mapper = JsonHelper.formattedObjectMapper();
    }

    @Nested
    @DisplayName("Whitelist tests")
    class WhitelistTests {

        @Test
        @DisplayName("Using JSON string")
        void testMaskByStringUsing() {
            MaskUtils maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask(sampleUnmaskedJson);
            JsonNode maskedNode = readTree(mapper, maskedJson);
            allWhitelistAssertions(maskedNode);
        }

        @Test
        @DisplayName("Using Java objects")
        void testMaskByObjectUsing() {
            MaskUtils maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask(sampleUnmaskedObject);
            JsonNode maskedNode = readTree(mapper, maskedJson);
            allWhitelistAssertions(maskedNode);
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
    }

    @Nested
    @DisplayName("Blacklist Tests")
    class BlacklistTests {

        @Test
        @DisplayName("Using JSON string")
        void testMaskByStringUsing() {
            MaskUtils maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask(sampleUnmaskedJson);
            JsonNode maskedNode = readTree(mapper, maskedJson);
            allBlacklistAssertions(maskedNode);
        }

        @Test
        @DisplayName("Using Java objects")
        void testMaskByObjectUsing() {
            MaskUtils maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask(sampleUnmaskedObject);
            JsonNode maskedNode = readTree(mapper, maskedJson);
            allBlacklistAssertions(maskedNode);
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
    }

}

