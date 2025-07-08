package dev.stockman.jsonshield.test;

import dev.stockman.jsonshield.core.InvalidJsonException;
import dev.stockman.jsonshield.core.JsonShield;
import dev.stockman.jsonshield.core.JsonShieldConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractJsonShieldTest {
    
    protected abstract JsonShield createJsonShield(JsonShieldConfiguration maskingConfiguration);
    protected abstract String cleanJsonify(String json);

    private void jsonAssertEquals(String expected, String actual) {
        String expectedClean = cleanJsonify(expected);
        String actualClean = cleanJsonify(actual);
        assertEquals(expectedClean, actualClean);
    }


    @Nested
    @DisplayName("Whitelist tests")
    class WhitelistTests {

        @Test
        @DisplayName("Malformed JSON")
        void testValidate() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            assertThrows(InvalidJsonException.class, () -> maskUtils.mask("{malformed json}"));
        }

        @Test
        @DisplayName("Null inputs")
        void testNullInputs() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            assertThrows(NullPointerException.class, () -> maskUtils.mask((String) null));
            assertThrows(NullPointerException.class, () -> maskUtils.mask((Object) null));
        }

        @Test
        @DisplayName("String scalar value")
        void testStringScalarValue() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("\"test\"");
            jsonAssertEquals("\"*****\"", maskedJson);
        }

        @Test
        @DisplayName("Boolean scalar value")
        void testBooleanScalarValue() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("true");
            jsonAssertEquals("false", maskedJson);
        }

        @Test
        @DisplayName("Integer scalar value")
        void testIntegerScalarValue() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("1");
            jsonAssertEquals("0", maskedJson);
        }

        @Test
        @DisplayName("Float scalar value")
        void testFloatScalarValue() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("1.1");
            jsonAssertEquals("0.0", maskedJson);
        }

        @Test
        @DisplayName("Null scalar value")
        void testNullScalarValue() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("null");
            jsonAssertEquals("null", maskedJson);
        }

        @Test
        @DisplayName("Empty object")
        void testEmptyObject() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("{ }");
            jsonAssertEquals("{ }", maskedJson);
        }

        @Test
        @DisplayName("Empty array")
        void testEmptyArray() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("[ ]");
            jsonAssertEquals("[ ]", maskedJson);
        }

        @Test
        @DisplayName("Simple object value")
        void testSimpleObjectValue() {
            //language=json
            String input = """
                    {
                      "string" : "test",
                      "boolean" : true,
                      "integer" : 1,
                      "float" : 1.1,
                      "null" : null,
                      "object" : {
                        "string" : "test",
                        "boolean" : true,
                        "integer" : 1,
                        "float" : 1.1,
                        "null" : null,
                        "object" : { },
                        "array" : [ ]
                      },
                      "array" : [
                        "test",
                        true,
                        1,
                        1.1,
                        null,
                        { },
                        [ ]
                      ]
                    }""";
            //language=json
            String output = """
                    {
                      "string" : "*****",
                      "boolean" : false,
                      "integer" : 0,
                      "float" : 0.0,
                      "null" : null,
                      "object" : {
                        "string" : "*****",
                        "boolean" : false,
                        "integer" : 0,
                        "float" : 0.0,
                        "null" : null,
                        "object" : { },
                        "array" : [ ]
                      },
                      "array" : [
                        "*****",
                        false,
                        0,
                        0.0,
                        null,
                        { },
                        [ ]
                      ]
                    }""";

            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            jsonAssertEquals(output, maskUtils.mask(input));
        }

        @Test
        @DisplayName("Array of object values")
        void testArrayOfObjectValues() {
            //language=json
            String input = """
                    [
                      {
                        "string" : "test",
                        "boolean" : true,
                        "integer" : 1,
                        "float" : 1.1,
                        "null" : null,
                        "object" : {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : { },
                          "array" : [ ]
                        },
                        "array" : [
                          "test",
                          true,
                          1,
                          1.1,
                          null,
                          { },
                          [ ]
                        ]
                      },
                      {
                        "string" : "test",
                        "boolean" : true,
                        "integer" : 1,
                        "float" : 1.1,
                        "null" : null,
                        "object" : {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : { },
                          "array" : [ ]
                        },
                        "array" : [
                          "test",
                          true,
                          1,
                          1.1,
                          null,
                          { },
                          [ ]
                        ]
                      }
                    ]""";
            //language=json
            String output = """
                    [
                      {
                        "string" : "*****",
                        "boolean" : false,
                        "integer" : 0,
                        "float" : 0.0,
                        "null" : null,
                        "object" : {
                          "string" : "*****",
                          "boolean" : false,
                          "integer" : 0,
                          "float" : 0.0,
                          "null" : null,
                          "object" : { },
                          "array" : [ ]
                        },
                        "array" : [
                          "*****",
                          false,
                          0,
                          0.0,
                          null,
                          { },
                          [ ]
                        ]
                      },
                      {
                        "string" : "*****",
                        "boolean" : false,
                        "integer" : 0,
                        "float" : 0.0,
                        "null" : null,
                        "object" : {
                          "string" : "*****",
                          "boolean" : false,
                          "integer" : 0,
                          "float" : 0.0,
                          "null" : null,
                          "object" : { },
                          "array" : [ ]
                        },
                        "array" : [
                          "*****",
                          false,
                          0,
                          0.0,
                          null,
                          { },
                          [ ]
                        ]
                      }
                    ]""";

            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            jsonAssertEquals(output, maskUtils.mask(input));
        }

        @Test
        @DisplayName("Matrix of object values")
        void testMatrixOfObjectValues() {
            //language=json
            String input = """
                    [
                      [
                        {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : {
                            "string" : "test",
                            "boolean" : true,
                            "integer" : 1,
                            "float" : 1.1,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "test",
                            true,
                            1,
                            1.1,
                            null,
                            { },
                            [ ]
                          ]
                        },
                        {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : {
                            "string" : "test",
                            "boolean" : true,
                            "integer" : 1,
                            "float" : 1.1,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "test",
                            true,
                            1,
                            1.1,
                            null,
                            { },
                            [ ]
                          ]
                        }
                      ],
                      [
                        {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : {
                            "string" : "test",
                            "boolean" : true,
                            "integer" : 1,
                            "float" : 1.1,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "test",
                            true,
                            1,
                            1.1,
                            null,
                            { },
                            [ ]
                          ]
                        },
                        {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : {
                            "string" : "test",
                            "boolean" : true,
                            "integer" : 1,
                            "float" : 1.1,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "test",
                            true,
                            1,
                            1.1,
                            null,
                            { },
                            [ ]
                          ]
                        }
                      ]
                    ]""";
            //language=json
            String output = """
                    [
                      [
                        {
                          "string" : "*****",
                          "boolean" : false,
                          "integer" : 0,
                          "float" : 0.0,
                          "null" : null,
                          "object" : {
                            "string" : "*****",
                            "boolean" : false,
                            "integer" : 0,
                            "float" : 0.0,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "*****",
                            false,
                            0,
                            0.0,
                            null,
                            { },
                            [ ]
                          ]
                        },
                        {
                          "string" : "*****",
                          "boolean" : false,
                          "integer" : 0,
                          "float" : 0.0,
                          "null" : null,
                          "object" : {
                            "string" : "*****",
                            "boolean" : false,
                            "integer" : 0,
                            "float" : 0.0,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "*****",
                            false,
                            0,
                            0.0,
                            null,
                            { },
                            [ ]
                          ]
                        }
                      ],
                      [
                        {
                          "string" : "*****",
                          "boolean" : false,
                          "integer" : 0,
                          "float" : 0.0,
                          "null" : null,
                          "object" : {
                            "string" : "*****",
                            "boolean" : false,
                            "integer" : 0,
                            "float" : 0.0,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "*****",
                            false,
                            0,
                            0.0,
                            null,
                            { },
                            [ ]
                          ]
                        },
                        {
                          "string" : "*****",
                          "boolean" : false,
                          "integer" : 0,
                          "float" : 0.0,
                          "null" : null,
                          "object" : {
                            "string" : "*****",
                            "boolean" : false,
                            "integer" : 0,
                            "float" : 0.0,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "*****",
                            false,
                            0,
                            0.0,
                            null,
                            { },
                            [ ]
                          ]
                        }
                      ]
                    ]""";

            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useWhiteListStrategy().build());
            jsonAssertEquals(output, maskUtils.mask(input));
        }
    }

    @Nested
    @DisplayName("Blacklist tests")
    class BlacklistTests {

        @Test
        @DisplayName("Malformed JSON")
        void testValidate() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            assertThrows(InvalidJsonException.class, () -> maskUtils.mask("{malformed json}"));
        }

        @Test
        @DisplayName("Null inputs")
        void testNullInputs() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            assertThrows(NullPointerException.class, () -> maskUtils.mask((String) null));
            assertThrows(NullPointerException.class, () -> maskUtils.mask((Object) null));
        }

        @Test
        @DisplayName("String scalar value")
        void testStringScalarValue() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("\"test\"");
            jsonAssertEquals("\"test\"", maskedJson);
        }

        @Test
        @DisplayName("Boolean scalar value")
        void testBooleanScalarValue() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("true");
            jsonAssertEquals("true", maskedJson);
        }

        @Test
        @DisplayName("Integer scalar value")
        void testIntegerScalarValue() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("1");
            jsonAssertEquals("1", maskedJson);
        }

        @Test
        @DisplayName("Float scalar value")
        void testFloatScalarValue() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("1.1");
            jsonAssertEquals("1.1", maskedJson);
        }

        @Test
        @DisplayName("Null scalar value")
        void testNullScalarValue() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("null");
            jsonAssertEquals("null", maskedJson);
        }

        @Test
        @DisplayName("Empty object")
        void testEmptyObject() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("{ }");
            jsonAssertEquals("{ }", maskedJson);
        }

        @Test
        @DisplayName("Empty array")
        void testEmptyArray() {
            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("[ ]");
            jsonAssertEquals("[ ]", maskedJson);
        }

        @Test
        @DisplayName("Simple object value")
        void testSimpleObjectValue() {
            //language=json
            String input = """
                    {
                      "string" : "test",
                      "boolean" : true,
                      "integer" : 1,
                      "float" : 1.1,
                      "null" : null,
                      "object" : {
                        "string" : "test",
                        "boolean" : true,
                        "integer" : 1,
                        "float" : 1.1,
                        "null" : null,
                        "object" : { },
                        "array" : [ ]
                      },
                      "array" : [
                        "test",
                        true,
                        1,
                        1.1,
                        null,
                        { },
                        [ ]
                      ]
                    }""";

            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            jsonAssertEquals(input, maskUtils.mask(input));
        }

        @Test
        @DisplayName("Array of object values")
        void testArrayOfObjectValues() {
            //language=json
            String input = """
                    [
                      {
                        "string" : "test",
                        "boolean" : true,
                        "integer" : 1,
                        "float" : 1.1,
                        "null" : null,
                        "object" : {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : { },
                          "array" : [ ]
                        },
                        "array" : [
                          "test",
                          true,
                          1,
                          1.1,
                          null,
                          { },
                          [ ]
                        ]
                      },
                      {
                        "string" : "test",
                        "boolean" : true,
                        "integer" : 1,
                        "float" : 1.1,
                        "null" : null,
                        "object" : {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : { },
                          "array" : [ ]
                        },
                        "array" : [
                          "test",
                          true,
                          1,
                          1.1,
                          null,
                          { },
                          [ ]
                        ]
                      }
                    ]""";

            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            jsonAssertEquals(input, maskUtils.mask(input));
        }

        @Test
        @DisplayName("Matrix of object values")
        void testMatrixOfObjectValues() {
            //language=json
            String input = """
                    [
                      [
                        {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : {
                            "string" : "test",
                            "boolean" : true,
                            "integer" : 1,
                            "float" : 1.1,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "test",
                            true,
                            1,
                            1.1,
                            null,
                            { },
                            [ ]
                          ]
                        },
                        {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : {
                            "string" : "test",
                            "boolean" : true,
                            "integer" : 1,
                            "float" : 1.1,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "test",
                            true,
                            1,
                            1.1,
                            null,
                            { },
                            [ ]
                          ]
                        }
                      ],
                      [
                        {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : {
                            "string" : "test",
                            "boolean" : true,
                            "integer" : 1,
                            "float" : 1.1,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "test",
                            true,
                            1,
                            1.1,
                            null,
                            { },
                            [ ]
                          ]
                        },
                        {
                          "string" : "test",
                          "boolean" : true,
                          "integer" : 1,
                          "float" : 1.1,
                          "null" : null,
                          "object" : {
                            "string" : "test",
                            "boolean" : true,
                            "integer" : 1,
                            "float" : 1.1,
                            "null" : null,
                            "object" : { },
                            "array" : [ ]
                          },
                          "array" : [
                            "test",
                            true,
                            1,
                            1.1,
                            null,
                            { },
                            [ ]
                          ]
                        }
                      ]
                    ]""";

            JsonShield maskUtils = createJsonShield(JsonShieldConfiguration.useBlackListStrategy().build());
            jsonAssertEquals(input, maskUtils.mask(input));
        }

    }


}

