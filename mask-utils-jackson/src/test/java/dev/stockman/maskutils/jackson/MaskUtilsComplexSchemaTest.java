package dev.stockman.maskutils.jackson;

import dev.stockman.maskutils.core.MaskUtils;
import dev.stockman.maskutils.core.MaskingConfiguration;
import dev.stockman.maskutils.test.AbstractMaskUtilsComplexSchemaTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Jackson JSON provider complex schema tests")
public class MaskUtilsComplexSchemaTest extends AbstractMaskUtilsComplexSchemaTest {

    @Override
    protected MaskUtils createMaskUtils(MaskingConfiguration maskingConfiguration) {
        return new MaskUtilsJackson(JsonHelper.formattedObjectMapper(), maskingConfiguration);
    }

}

