package dev.stockman.maskutils.gson;

import com.google.gson.JsonParser;
import dev.stockman.maskutils.core.MaskUtils;
import dev.stockman.maskutils.core.MaskingConfiguration;
import dev.stockman.maskutils.test.AbstractMaskUtilsTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("GSON JSON provider unit tests")
public class MaskUtilsGsonTest extends AbstractMaskUtilsTest {

    @Override
    protected MaskUtils createMaskUtils(MaskingConfiguration maskingConfiguration) {
        return new MaskUtilsGson(JsonHelper.formattedGson(), maskingConfiguration);
    }

    @Override
    protected String cleanJsonify(String json) {
        return JsonHelper.formattedGson().toJson(JsonParser.parseString(json));
    }
}
