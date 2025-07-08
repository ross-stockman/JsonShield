package dev.stockman.maskutils.test;

import dev.stockman.maskutils.core.InvalidJsonException;
import dev.stockman.maskutils.core.MaskUtils;
import dev.stockman.maskutils.core.MaskingConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractMaskUtilsTest {
    
    protected abstract MaskUtils createMaskUtils(MaskingConfiguration maskingConfiguration);
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
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
            assertThrows(InvalidJsonException.class, () -> maskUtils.mask("{malformed json}"));
        }

        @Test
        @DisplayName("Null inputs")
        void testNullInputs() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
            assertThrows(NullPointerException.class, () -> maskUtils.mask((String) null));
            assertThrows(NullPointerException.class, () -> maskUtils.mask((Object) null));
        }

        @Test
        @DisplayName("String scalar value")
        void testStringScalarValue() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("\"test\"");
            jsonAssertEquals("\"*****\"", maskedJson);
        }

        @Test
        @DisplayName("Boolean scalar value")
        void testBooleanScalarValue() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("true");
            jsonAssertEquals("false", maskedJson);
        }

        @Test
        @DisplayName("Integer scalar value")
        void testIntegerScalarValue() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("1");
            jsonAssertEquals("0", maskedJson);
        }

        @Test
        @DisplayName("Float scalar value")
        void testFloatScalarValue() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("1.1");
            jsonAssertEquals("0.0", maskedJson);
        }

        @Test
        @DisplayName("Null scalar value")
        void testNullScalarValue() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("null");
            jsonAssertEquals("null", maskedJson);
        }

        @Test
        @DisplayName("Empty object")
        void testEmptyObject() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("{ }");
            jsonAssertEquals("{ }", maskedJson);
        }

        @Test
        @DisplayName("Empty array")
        void testEmptyArray() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
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

            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
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

            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
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

            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useWhiteListStrategy().build());
            jsonAssertEquals(output, maskUtils.mask(input));
        }
    }

    @Nested
    @DisplayName("Blacklist tests")
    class BlacklistTests {

        @Test
        @DisplayName("Malformed JSON")
        void testValidate() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
            assertThrows(InvalidJsonException.class, () -> maskUtils.mask("{malformed json}"));
        }

        @Test
        @DisplayName("Null inputs")
        void testNullInputs() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
            assertThrows(NullPointerException.class, () -> maskUtils.mask((String) null));
            assertThrows(NullPointerException.class, () -> maskUtils.mask((Object) null));
        }

        @Test
        @DisplayName("String scalar value")
        void testStringScalarValue() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("\"test\"");
            jsonAssertEquals("\"test\"", maskedJson);
        }

        @Test
        @DisplayName("Boolean scalar value")
        void testBooleanScalarValue() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("true");
            jsonAssertEquals("true", maskedJson);
        }

        @Test
        @DisplayName("Integer scalar value")
        void testIntegerScalarValue() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("1");
            jsonAssertEquals("1", maskedJson);
        }

        @Test
        @DisplayName("Float scalar value")
        void testFloatScalarValue() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("1.1");
            jsonAssertEquals("1.1", maskedJson);
        }

        @Test
        @DisplayName("Null scalar value")
        void testNullScalarValue() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("null");
            jsonAssertEquals("null", maskedJson);
        }

        @Test
        @DisplayName("Empty object")
        void testEmptyObject() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("{ }");
            jsonAssertEquals("{ }", maskedJson);
        }

        @Test
        @DisplayName("Empty array")
        void testEmptyArray() {
            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
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

            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
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

            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
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

            MaskUtils maskUtils = createMaskUtils(MaskingConfiguration.useBlackListStrategy().build());
            jsonAssertEquals(input, maskUtils.mask(input));
        }

    }


}

