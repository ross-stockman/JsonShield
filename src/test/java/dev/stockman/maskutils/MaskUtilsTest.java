package dev.stockman.maskutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MaskUtilsTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = JsonHelper.formattedObjectMapper();
    }

    @Nested
    @DisplayName("Whitelist tests")
    class WhitelistTests {

        @Test
        @DisplayName("Null constructor inputs")
        void testNullConstructorInputs() {
            assertThrows(NullPointerException.class, () -> new MaskUtilsJackson(null, MaskingConfiguration.useWhiteListStrategy().build()));
            assertThrows(NullPointerException.class, () -> new MaskUtilsJackson(mapper, null));
        }

        @Test
        @DisplayName("Malformed JSON")
        void testValidate() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            assertThrows(InvalidJsonException.class, () -> maskUtils.mask("{malformed json}"));
        }

        @Test
        @DisplayName("Null inputs")
        void testNullInputs() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            assertThrows(NullPointerException.class, () -> maskUtils.mask((String) null));
            assertThrows(NullPointerException.class, () -> maskUtils.mask((Object) null));
        }

        @Test
        @DisplayName("String scalar value")
        void testStringScalarValue() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("\"test\"");
            assertEquals("\"*****\"", maskedJson);
        }

        @Test
        @DisplayName("Boolean scalar value")
        void testBooleanScalarValue() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("true");
            assertEquals("false", maskedJson);
        }

        @Test
        @DisplayName("Integer scalar value")
        void testIntegerScalarValue() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("1");
            assertEquals("0", maskedJson);
        }

        @Test
        @DisplayName("Float scalar value")
        void testFloatScalarValue() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("1.1");
            assertEquals("0.0", maskedJson);
        }

        @Test
        @DisplayName("Null scalar value")
        void testNullScalarValue() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("null");
            assertEquals("null", maskedJson);
        }

        @Test
        @DisplayName("Empty object")
        void testEmptyObject() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("{ }");
            assertEquals("{ }", maskedJson);
        }

        @Test
        @DisplayName("Empty array")
        void testEmptyArray() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            String maskedJson = maskUtils.mask("[ ]");
            assertEquals("[ ]", maskedJson);
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

            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            assertEquals(output, maskUtils.mask(input));
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

            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            assertEquals(output, maskUtils.mask(input));
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

            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useWhiteListStrategy().build());
            assertEquals(output, maskUtils.mask(input));
        }
    }

    @Nested
    @DisplayName("Blacklist tests")
    class BlacklistTests {

        @Test
        @DisplayName("Null constructor inputs")
        void testNullConstructorInputs() {
            assertThrows(NullPointerException.class, () -> new MaskUtilsJackson(null, MaskingConfiguration.useBlackListStrategy().build()));
            assertThrows(NullPointerException.class, () -> new MaskUtilsJackson(mapper, null));
        }

        @Test
        @DisplayName("Malformed JSON")
        void testValidate() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            assertThrows(InvalidJsonException.class, () -> maskUtils.mask("{malformed json}"));
        }

        @Test
        @DisplayName("Null inputs")
        void testNullInputs() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            assertThrows(NullPointerException.class, () -> maskUtils.mask((String) null));
            assertThrows(NullPointerException.class, () -> maskUtils.mask((Object) null));
        }

        @Test
        @DisplayName("String scalar value")
        void testStringScalarValue() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("\"test\"");
            assertEquals("\"test\"", maskedJson);
        }

        @Test
        @DisplayName("Boolean scalar value")
        void testBooleanScalarValue() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("true");
            assertEquals("true", maskedJson);
        }

        @Test
        @DisplayName("Integer scalar value")
        void testIntegerScalarValue() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("1");
            assertEquals("1", maskedJson);
        }

        @Test
        @DisplayName("Float scalar value")
        void testFloatScalarValue() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("1.1");
            assertEquals("1.1", maskedJson);
        }

        @Test
        @DisplayName("Null scalar value")
        void testNullScalarValue() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("null");
            assertEquals("null", maskedJson);
        }

        @Test
        @DisplayName("Empty object")
        void testEmptyObject() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("{ }");
            assertEquals("{ }", maskedJson);
        }

        @Test
        @DisplayName("Empty array")
        void testEmptyArray() {
            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            String maskedJson = maskUtils.mask("[ ]");
            assertEquals("[ ]", maskedJson);
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

            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            assertEquals(input, maskUtils.mask(input));
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

            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            assertEquals(input, maskUtils.mask(input));
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

            MaskUtilsJackson maskUtils = new MaskUtilsJackson(mapper, MaskingConfiguration.useBlackListStrategy().build());
            assertEquals(input, maskUtils.mask(input));
        }

    }


}

