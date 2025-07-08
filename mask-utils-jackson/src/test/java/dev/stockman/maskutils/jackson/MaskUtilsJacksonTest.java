package dev.stockman.maskutils.jackson;

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

}

