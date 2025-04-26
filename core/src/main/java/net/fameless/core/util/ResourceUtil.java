package net.fameless.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStreamReader;

public class ResourceUtil {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public static @NotNull String readResource(String path) {
        try (InputStreamReader reader = new InputStreamReader(ResourceUtil.class.getClassLoader().getResourceAsStream(path))) {
            StringBuilder content = new StringBuilder();
            char[] buffer = new char[1024];
            int numRead;
            while ((numRead = reader.read(buffer)) != -1) {
                content.append(buffer, 0, numRead);
            }
            return content.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Contract("_ -> new")
    public static @NotNull File getFile(String path) {
        return new File(ResourceUtil.class.getClassLoader().getResource(path).getFile());
    }

    public static JsonObject readJsonResource(String path) {
        try (InputStreamReader reader = new InputStreamReader(ResourceUtil.class.getClassLoader().getResourceAsStream(path))) {
            return GSON.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
