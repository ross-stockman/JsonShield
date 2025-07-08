package dev.stockman.maskutils.jackson;

import dev.stockman.maskutils.core.JsonMaskingException;
import dev.stockman.maskutils.core.MaskUtils;
import dev.stockman.maskutils.core.MaskingConfiguration;
import dev.stockman.maskutils.test.AbstractMaskUtilsTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Jackson JSON provider unit tests")
public class MaskUtilsJacksonTest extends AbstractMaskUtilsTest {

    @Override
    protected MaskUtils createMaskUtils(MaskingConfiguration maskingConfiguration) {
        return new MaskUtilsJackson(JsonHelper.formattedObjectMapper(), maskingConfiguration);
    }

    @Override
    protected String cleanJsonify(String json) {
        try {
            return JsonHelper.formattedObjectMapper().writeValueAsString(
                    JsonHelper.formattedObjectMapper().readTree(json)
            );
        } catch (Exception e) {
            throw new JsonMaskingException("Failed to process JSON", e);
        }
    }

}

