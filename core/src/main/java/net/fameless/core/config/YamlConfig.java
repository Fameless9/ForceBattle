package net.fameless.core.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record YamlConfig(Map<String, Object> data) {

    private @Nullable Object getValue(@NotNull String key) {
        String[] parts = key.split("\\.");
        Object current = data;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!(current instanceof Map<?, ?> map)) {
                if (i < parts.length - 1) {
                    return null;
                } else {
                    return current;
                }
            }
            current = map.get(part);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    public void set(@NotNull String key, Object value) {
        String[] parts = key.split("\\.");
        Map<String, Object> currentMap = data;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            Object obj = currentMap.get(part);
            if (!(obj instanceof Map)) {
                Map<String, Object> newMap = new HashMap<>();
                currentMap.put(part, newMap);
                currentMap = newMap;
            } else {
                currentMap = (Map<String, Object>) obj;
            }
        }
        String lastPart = parts[parts.length - 1];
        if (value == null) {
            currentMap.remove(lastPart);
        } else {
            currentMap.put(lastPart, value);
        }
    }

    public @NotNull String getString(String key) {
        Object val = getValue(key);
        if (val instanceof String string) {
            return string;
        }
        throw new IllegalArgumentException("Value for key '" + key + "' is not a String");
    }

    public String getString(String key, String defaultValue) {
        Object val = getValue(key);
        if (val == null) return defaultValue;
        if (val instanceof String string) {
            return string;
        }
        throw new IllegalArgumentException("Value for key '" + key + "' is not a String");
    }

    public int getInt(String key) {
        Object val = getValue(key);
        if (val instanceof Number num) {
            return num.intValue();
        }
        throw new IllegalArgumentException("Value for key '" + key + "' is not a Number");
    }

    public int getInt(String key, int defaultValue) {
        Object val = getValue(key);
        if (val == null) return defaultValue;
        if (val instanceof Number num) {
            return num.intValue();
        }
        throw new IllegalArgumentException("Value for key '" + key + "' is not a Number");
    }

    public double getDouble(String key) {
        Object val = getValue(key);
        if (val instanceof Number num) {
            return num.doubleValue();
        }
        throw new IllegalArgumentException("Value for key '" + key + "' is not a Number");
    }

    public double getDouble(String key, double defaultValue) {
        Object val = getValue(key);
        if (val == null) return defaultValue;
        if (val instanceof Number num) {
            return num.doubleValue();
        }
        throw new IllegalArgumentException("Value for key '" + key + "' is not a Number");
    }

    public boolean getBoolean(String key) {
        Object val = getValue(key);
        if (val instanceof Boolean bool) {
            return bool;
        }
        throw new IllegalArgumentException("Value for key '" + key + "' is not a Boolean");
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object val = getValue(key);
        if (val == null) return defaultValue;
        if (val instanceof Boolean bool) {
            return bool;
        }
        throw new IllegalArgumentException("Value for key '" + key + "' is not a Boolean");
    }

    @SuppressWarnings("unchecked")
    public @NotNull Map<String, Object> getSection(String key) {
        Object val = getValue(key);
        if (val instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Collections.emptyMap();
    }

    public @NotNull List<String> getStringList(String key) {
        Object val = getValue(key);
        if (val instanceof List<?>) {
            return (List<String>) val;
        }
        return new ArrayList<>();
    }

    public boolean contains(String key) {
        return getValue(key) != null;
    }

    public String dump() {
        return PluginConfig.YAML.dump(data);
    }

}
