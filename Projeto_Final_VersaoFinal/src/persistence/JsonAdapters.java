package persistence;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class JsonAdapters {
    public static final GsonBuilder registerAll(GsonBuilder gb) {
        gb.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) { return new JsonPrimitive(src.toString()); }
        });
        gb.registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) { return LocalDate.parse(json.getAsString()); }
        });

        gb.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) { return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)); }
        });
        gb.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) { return LocalDateTime.parse(json.getAsString()); }
        });

        gb.registerTypeAdapter(Duration.class, new JsonSerializer<Duration>() {
            public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) { return new JsonPrimitive(src.toMinutes()); }
        });
        gb.registerTypeAdapter(Duration.class, new JsonDeserializer<Duration>() {
            public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) { return Duration.ofMinutes(json.getAsLong()); }
        });

        return gb;
    }

    public static com.google.gson.Gson createGson() {
        com.google.gson.GsonBuilder gb = new com.google.gson.GsonBuilder().setPrettyPrinting();
        registerAll(gb);
        return gb.create();
    }
}

