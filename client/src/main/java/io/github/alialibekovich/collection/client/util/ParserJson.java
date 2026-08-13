package io.github.alialibekovich.collection.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.alialibekovich.collection.model.Organization;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Parses the collection JSON the server sends for {@code show}/{@code visualize}
 * straight into the local {@link CollectionManager} — no temp files involved.
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

    /** Replaces the local collection with the organizations parsed from {@code json}. */
    public static void loadCollection(String json) {
        CollectionManager.initializeCollection();
        try {
            List<Organization> organizations =
                    GSON.fromJson(json, new TypeToken<List<Organization>>() {
                    }.getType());
            if (organizations != null) {
                organizations.forEach(CollectionManager::addJson);
            }
        } catch (JsonSyntaxException e) {
            System.out.println("Сервер вернул некорректный JSON: " + e.getMessage());
        }
    }
}
