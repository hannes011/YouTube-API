package org.bgf.youtube.service;

public final class DurationParser {
    private DurationParser() {}

    public static int parseDurationSeconds(String iso8601) {
        if (iso8601 == null || iso8601.isBlank()) return 0;
        int h = 0, m = 0, s = 0;
        if (!iso8601.startsWith("P")) return 0;
        int tIdx = iso8601.indexOf('T');
        String time = tIdx >= 0 ? iso8601.substring(tIdx + 1) : "";
        StringBuilder num = new StringBuilder();
        for (char c : time.toCharArray()) {
            if (Character.isDigit(c)) {
                num.append(c);
            } else {
                int val = (num.isEmpty()) ? 0 : Integer.parseInt(num.toString());
                switch (c) {
                    case 'H' -> h = val;
                    case 'M' -> m = val;
                    case 'S' -> s = val;
                    default -> {}
                }
                num = new StringBuilder();
            }
        }
        return h * 3600 + m * 60 + s;
    }
}

