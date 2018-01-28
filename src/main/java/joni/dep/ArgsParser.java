package joni.dep;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class ArgsParser {
    private static Pattern KEY_PATTERN = Pattern.compile("--?.+");
    private static Pattern KEY_VAL_PATTERN = Pattern.compile("--?.+=.+");

    public Args parse(String[] argv) {
        Map<String, String> values = new HashMap<>();
        if (argv.length == 0) {
            return new Args(values);
        }

        String previousKey = null;

        for (String arg : argv) {
            String baseArg = baseName(arg);
            if (isKeyVal(arg)) {
                String[] parts = baseArg.split("=", 2);
                values.put(parts[0], parts[1]);
            } else if (isKey(arg)) {
                if (previousKey != null) {
                    values.put(previousKey, "true");
                }
                previousKey = baseArg;
            } else {
                if (previousKey == null) {
                    throw new IllegalArgumentException("Cannot have value before a key");
                }

                values.put(previousKey, arg);
                previousKey = null;
            }
        }

        if (previousKey != null) {
            values.put(previousKey, "true");
        }

        return new Args(values);
    }

    private boolean isKeyVal(String arg) {
        Matcher matcher = KEY_VAL_PATTERN.matcher(arg);
        return matcher.find();
    }

    private boolean isKey(String arg) {
        Matcher matcher = KEY_PATTERN.matcher(arg);
        return matcher.find();
    }

    private String baseName(String arg) {
        String result = arg;
        while (result.startsWith("-")) {
            result = result.substring(1);
        }

        return result;
    }
}
