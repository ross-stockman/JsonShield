package dev.stockman.jsonshield.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

final class JsonHelper {
    private JsonHelper() {}

    public static Gson formattedGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .create();
    }

    private static class InstantTypeAdapter extends TypeAdapter<Instant> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

        @Override
        public void write(JsonWriter out, Instant value) throws IOException {
            out.value(value != null ? formatter.format(value) : null);
        }

        @Override
        public Instant read(JsonReader in) throws IOException {
            String str = in.nextString();
            return str != null ? Instant.parse(str) : null;
        }
    }

}
