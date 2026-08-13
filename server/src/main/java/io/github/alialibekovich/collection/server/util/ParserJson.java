package io.github.alialibekovich.collection.server.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.alialibekovich.collection.model.Organization;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serializes the organization collection to JSON — the format the client
 * consumes for its table and visualization views.
 */
public final class ParserJson {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                @Override
                public void write(JsonWriter out, LocalDateTime value) throws IOException {
                    out.value(value.toString());
                }

                @Override
                public LocalDateTime read(JsonReader in) throws IOException {
                    return LocalDateTime.parse(in.nextString());
                }
            })
            .serializeNulls()
            .enableComplexMapKeySerialization()
            .create();

    private ParserJson() {
    }

    public static String toJson(List<Organization> organizations) {
        return GSON.toJson(organizations);
    }
}
