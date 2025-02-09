package net.fameless.forceBattle.configuration;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fameless.forceBattle.ForceBattle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PluginUpdater {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/fameless9/ForceBattle/releases/latest";
    private static final String JSON_TAG_NAME = "tag_name";
    private static final String JSON_ASSETS = "assets";
    private static final String JSON_BROWSER_DOWNLOAD_URL = "browser_download_url";

    private static final String PLUGIN_DIRECTORY = ForceBattle.platform().getPluginFile().getParent();
    private static final String CURRENT_PLUGIN_FILE = ForceBattle.platform().getPluginFile().getAbsolutePath();
    private static final String TEMP_PLUGIN_FILE = PLUGIN_DIRECTORY + "/plugin_temp.jar";

    public static void checkForUpdate() {
        try {
            JsonObject releaseData = fetchLatestRelease();
            String latestVersion = releaseData.get(JSON_TAG_NAME).getAsString();
            String currentVersion = ForceBattle.platform().getPluginVersion();

            if (latestVersion.equals(currentVersion)) {
                ForceBattle.logger().info("Plugin is already up-to-date.");
                return;
            }

            String downloadUrl = findPluginDownloadUrl(releaseData.getAsJsonArray(JSON_ASSETS));
            if (downloadUrl == null) {
                ForceBattle.logger().warning("No plugin asset found in the latest release.");
                return;
            }

            ForceBattle.logger().info("New version found: " + latestVersion + ". Updating plugin... | Restart the server afterwards.");
            downloadAndReplacePlugin(downloadUrl, latestVersion);
        } catch (IOException e) {
            ForceBattle.logger().severe("Failed to fetch or update the plugin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static JsonObject fetchLatestRelease() throws IOException {
        URL url = URI.create(GITHUB_API_URL).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        try (InputStream inputStream = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return new Gson().fromJson(response.toString(), JsonObject.class);
        }
    }

    private static @Nullable String findPluginDownloadUrl(@NotNull JsonArray assets) {
        for (int i = 0; i < assets.size(); i++) {
            JsonObject asset = assets.get(i).getAsJsonObject();
            if (asset.get("name").getAsString().endsWith(".jar")) {
                return asset.get(JSON_BROWSER_DOWNLOAD_URL).getAsString();
            }
        }
        return null;
    }

    private static void downloadAndReplacePlugin(String downloadUrl, String latestVersion) throws IOException {
        URL url = URI.create(downloadUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        File tempFile = new File(TEMP_PLUGIN_FILE);
        try (InputStream inputStream = connection.getInputStream()) {
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        File currentFile = new File(CURRENT_PLUGIN_FILE);
        ForceBattle.platform().shutDown();
        Files.move(tempFile.toPath(), currentFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ForceBattle.logger().info("Plugin updated successfully to version " + latestVersion + ".");
    }

}
