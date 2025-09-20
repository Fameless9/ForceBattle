package net.fameless.core.util;

import javax.annotation.Nullable;

public record Coords(int x, @Nullable Integer y, int z) {

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }

    public static Coords fromString(String input) {
        String[] parts = input.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid coords: " + input);
        }
        return new Coords(
                Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[2].trim())
        );
    }

}
