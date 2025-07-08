package dev.stockman.jsonshield.test;

import dev.stockman.jsonshield.core.JsonShield;
import dev.stockman.jsonshield.core.JsonShieldConfiguration;
import dev.stockman.jsonshield.test.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractJsonShieldComplexSchemaTest {

    protected abstract JsonShield createJsonShield(JsonShieldConfiguration maskingConfiguration);
    protected abstract String cleanJsonify(String json);

    private void jsonAssertEquals(String expected, String actual) {
        String expectedClean = cleanJsonify(expected);
        String actualClean = cleanJsonify(actual);
        assertEquals(expectedClean, actualClean);
    }
    // language=JSON
    private static final String unmaskedJsonString = """
            {
              "id" : "ABC123",
              "timestamp" : "2025-07-01T12:34:56.789Z",
              "active" : true,
              "score" : 95.5,
              "name" : "John Doe",
              "age" : 30,
              "largeNumber" : 9223372036854775808,
              "preciseDecimal" : 123456.789,
              "contact" : {
                "email" : "john@example.com",
                "phone" : "123-456-7890",
                "verified" : false,
                "accountBalance" : 999999.99
              },
              "addresses" : [
                {
                  "type" : "home",
                  "street" : "123 Main St",
                  "city" : "Springfield",
                  "zipCode" : 12345,
                  "propertyValue" : 1234567.89,
                  "buildingNumber" : 18446744073709551615
                },
                {
                  "type" : "work",
                  "street" : "456 Corp Ave",
                  "city" : "Business City",
                  "zipCode" : 67890,
                  "propertyValue" : 1234567.89,
                  "buildingNumber" : 18446744073709551615
                }
              ],
              "tags" : [
                "premium",
                "verified",
                "active"
              ],
              "scores" : [
                88,
                92,
                95
              ],
              "metadata" : {
                "lastUpdated" : "2025-07-01",
                "version" : 2,
                "settings" : {
                  "notifications" : true,
                  "theme" : "dark"
                }
              },
              "payment" : {
                "cardNumber" : "4111111111111111",
                "cvv" : "123",
                "amount" : 999999.999999,
                "mixed" : [
                  "secret1",
                  42,
                  true,
                  {
                    "nestedKey" : "nestedValue",
                    "hugeValue" : 340282366920938463463374607431768211455
                  }
                ]
              },
              "nullField" : null,
              "emptyObject" : { },
              "emptyArray" : [ ]
            }""";

    //language=json
    private static final String maskedJsonString = """
            {
              "id" : "*****",
              "timestamp" : "*****",
              "active" : false,
              "score" : 0.0,
              "name" : "*****",
              "age" : 0,
              "largeNumber" : 0,
              "preciseDecimal" : 0.0,
              "contact" : {
                "email" : "*****",
                "phone" : "*****",
                "verified" : false,
                "accountBalance" : 0.0
              },
              "addresses" : [
                {
                  "type" : "*****",
                  "street" : "*****",
                  "city" : "*****",
                  "zipCode" : 0,
                  "propertyValue" : 0.0,
                  "buildingNumber" : 0
                },
                {
                  "type" : "*****",
                  "street" : "*****",
                  "city" : "*****",
                  "zipCode" : 0,
                  "propertyValue" : 0.0,
                  "buildingNumber" : 0
                }
              ],
              "tags" : [
                "*****",
                "*****",
                "*****"
              ],
              "scores" : [
                0,
                0,
                0
              ],
              "metadata" : {
                "lastUpdated" : "*****",
                "version" : 0,
                "settings" : {
                  "notifications" : false,
                  "theme" : "*****"
                }
              },
              "payment" : {
                "cardNumber" : "*****",
                "cvv" : "*****",
                "amount" : 0.0,
                "mixed" : [
                  "*****",
                  0,
                  false,
                  {
                    "nestedKey" : "*****",
                    "hugeValue" : 0
                  }
                ]
              },
              "nullField" : null,
              "emptyObject" : { },
              "emptyArray" : [ ]
            }""";

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
                            new BigInteger("18446744073709551615")
                    ),
                    new Address(
                            "work",
                            "456 Corp Ave",
                            "Business City",
                            67890,
                            new BigDecimal("1234567.89"),
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
                    new BigDecimal("999999.999999"),
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

    @Nested
    @DisplayName("Whitelist tests")
    class WhitelistTests {

        @Test
        @DisplayName("Using JSON string")
        void testMaskByStringUsing() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            jsonAssertEquals(maskedJsonString, maskUtils.mask(unmaskedJsonString));
        }

        @Test
        @DisplayName("Using Java objects")
        void testMaskByObjectUsing() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            jsonAssertEquals(maskedJsonString, maskUtils.mask(sampleUnmaskedObject));
        }

    }

    @Nested
    @DisplayName("Blacklist Tests")
    class BlacklistTests {

        @Test
        @DisplayName("Using JSON string")
        void testMaskByStringUsing() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            jsonAssertEquals(unmaskedJsonString, maskUtils.mask(unmaskedJsonString));
        }

        @Test
        @DisplayName("Using Java objects")
        void testMaskByObjectUsing() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            jsonAssertEquals(unmaskedJsonString, maskUtils.mask(sampleUnmaskedObject));
        }

    }

}

