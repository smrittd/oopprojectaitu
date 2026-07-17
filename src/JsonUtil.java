import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class JsonUtil {

    private JsonUtil() {
    }

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String toJsonString(String value) {
        return "\"" + escape(value) + "\"";
    }


    public static String object(String... keyValuePairs) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(keyValuePairs[i]).append("\":").append(keyValuePairs[i + 1]);
        }
        sb.append("}");
        return sb.toString();
    }

    public static String array(List<String> jsonObjects) {
        return "[" + String.join(",", jsonObjects) + "]";
    }


    public static Map<String, String> parseFlatObject(String json) {
        Map<String, String> map = new LinkedHashMap<>();
        if (json == null) return map;
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        int i = 0;
        int n = json.length();
        while (i < n) {
            while (i < n && (Character.isWhitespace(json.charAt(i)) || json.charAt(i) == ',')) i++;
            if (i >= n || json.charAt(i) != '"') break;

            int keyStart = ++i;
            while (i < n && json.charAt(i) != '"') i++;
            String key = json.substring(keyStart, i);
            i++; // closing quote

            while (i < n && (Character.isWhitespace(json.charAt(i)) || json.charAt(i) == ':')) i++;

            String value;
            if (i < n && json.charAt(i) == '"') {
                i++;
                StringBuilder val = new StringBuilder();
                while (i < n && json.charAt(i) != '"') {
                    char c = json.charAt(i);
                    if (c == '\\' && i + 1 < n) {
                        i++;
                        char esc = json.charAt(i);
                        switch (esc) {
                            case 'n' -> val.append('\n');
                            case 't' -> val.append('\t');
                            case 'r' -> val.append('\r');
                            case '"' -> val.append('"');
                            case '\\' -> val.append('\\');
                            default -> val.append(esc);
                        }
                    } else {
                        val.append(c);
                    }
                    i++;
                }
                value = val.toString();
                i++; // closing quote
            } else {
                int valStart = i;
                while (i < n && json.charAt(i) != ',' && json.charAt(i) != '}') i++;
                value = json.substring(valStart, i).trim();
            }
            map.put(key, value);
        }
        return map;
    }
}