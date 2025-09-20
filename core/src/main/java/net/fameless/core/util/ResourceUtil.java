package net.fameless.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

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

    public static void extractResourceIfMissing(String resourcePath, @NotNull File targetFile) {
        if (targetFile.exists()) return;

        targetFile.getParentFile().mkdirs();

        try (InputStream in = ResourceUtil.class.getClassLoader().getResourceAsStream(resourcePath);
             OutputStream out = new FileOutputStream(targetFile)) {

            if (in == null) {
                throw new FileNotFoundException("Resource not found in JAR: " + resourcePath);
            }

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to extract resource: " + resourcePath, e);
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
