package dev.stockman.jsonshield.gson;

import com.google.gson.JsonParser;
import dev.stockman.jsonshield.core.JsonShield;
import dev.stockman.jsonshield.core.JsonShieldConfiguration;
import dev.stockman.jsonshield.test.AbstractJsonShieldTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("GSON JSON provider unit tests")
public class JsonShieldGsonTest extends AbstractJsonShieldTest {

    @Override
    protected JsonShield createJsonShield(JsonShieldConfiguration jsonShieldConfiguration) {
        return new JsonShieldGson(JsonHelper.formattedGson(), jsonShieldConfiguration);
    }

    @Override
    protected String cleanJsonify(String json) {
        return JsonHelper.formattedGson().toJson(JsonParser.parseString(json));
    }
}
