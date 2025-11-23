package dev.stockman.jsonshield.jackson;

import dev.stockman.jsonshield.core.JsonShieldException;
import dev.stockman.jsonshield.core.JsonShield;
import dev.stockman.jsonshield.core.JsonShieldConfiguration;
import dev.stockman.jsonshield.test.AbstractJsonShieldTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Jackson JSON provider unit tests")
public class JsonShieldJacksonTest extends AbstractJsonShieldTest {

    @Override
    protected JsonShield createJsonShield(JsonShieldConfiguration jsonShieldConfiguration) {
        return new JsonShieldJackson(JsonHelper.formattedObjectMapper(), jsonShieldConfiguration);
    }

    @Override
    protected String cleanJsonify(String json) {
        try {
            return JsonHelper.formattedObjectMapper().writeValueAsString(
                    JsonHelper.formattedObjectMapper().readTree(json)
            );
        } catch (Exception e) {
            throw new JsonShieldException("Failed to process JSON", e);
        }
    }

}

