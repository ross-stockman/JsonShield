package dev.stockman.maskutils.gson;

import com.google.gson.JsonParser;
import dev.stockman.maskutils.core.MaskUtils;
import dev.stockman.maskutils.core.MaskingConfiguration;
import dev.stockman.maskutils.test.AbstractMaskUtilsComplexSchemaTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("GSON JSON provider complex schema tests")
public class MaskUtilsComplexSchemaTest extends AbstractMaskUtilsComplexSchemaTest {

    @Override
    protected MaskUtils createMaskUtils(MaskingConfiguration maskingConfiguration) {
        return new MaskUtilsGson(JsonHelper.formattedGson(), maskingConfiguration);
    }

    @Override
    protected String cleanJsonify(String json) {
        return JsonHelper.formattedGson().toJson(JsonParser.parseString(json));
    }
}
