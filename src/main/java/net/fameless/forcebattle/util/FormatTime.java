package net.fameless.forcebattle.util;

public class FormatTime {

    public static String toFormatted(int time) {
        int days = time / 86400;
        int hours = time / 3600 % 24;
        int minutes = time / 60 % 60;
        int seconds = time % 60;

        StringBuilder message = new StringBuilder();

        if (days >= 1) {
            message.append(days).append("d ");
        }
        if (hours >= 1) {
            message.append(hours).append("h ");
        }
        if (minutes >= 1) {
            message.append(minutes).append("m ");
        }
        if (seconds >= 1) {
            message.append(seconds).append("s ");
        }
        if (time == 0) {
            message.append("0s");
        }
        return String.valueOf(message);
    }
}