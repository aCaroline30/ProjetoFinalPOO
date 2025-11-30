package persistence;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class JsonAdapters {
    public static final GsonBuilder registerAll(GsonBuilder gb) {
        gb.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }
        });
        gb.registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                return LocalDate.parse(json.getAsString());
            }
        });

        gb.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        });
        gb.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                return LocalDateTime.parse(json.getAsString());
            }
        });

        gb.registerTypeAdapter(java.time.Duration.class, new JsonSerializer<java.time.Duration>() {
            public JsonElement serialize(java.time.Duration src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toMinutes());
            }
        });
        gb.registerTypeAdapter(java.time.Duration.class, new JsonDeserializer<java.time.Duration>() {
            public java.time.Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                long minutes = json.getAsLong();
                return java.time.Duration.ofMinutes(minutes);
            }
        });

        return gb;
    }

    public static Gson createGson() {
        GsonBuilder gb = new GsonBuilder().setPrettyPrinting();
        registerAll(gb);
        return gb.create();
    }
}
