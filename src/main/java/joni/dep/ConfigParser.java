package joni.dep;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigParser {
    private final ArgsParser argsParser;
    private final Map<String, String> entries;

    public ConfigParser() {
        this.argsParser = new ArgsParser();
        this.entries = new HashMap<>();
    }

    public ConfigParser entry(String key, Object value) {
        this.entries.put(key, value.toString());
        return this;
    }

    public Config parse() {
        return parse(new String[0]);
    }

    public Config parse(String[] argv) {
        Args args = this.argsParser.parse(argv);
        Config result = getConfig(args);
        result = result.withFallback(ConfigFactory.parseMap(this.entries));

        if (args.hasResources()) {
            for (String resource : args.getResources()) {
                result = result.withFallback(ConfigFactory.load(resource));
            }
        }

        if (args.hasFiles()) {
            for (String fileName : args.getFiles()) {
                result = result.withFallback(ConfigFactory.parseFile(new File(fileName)));
            }
        }

        if (!args.hasResources() && !args.hasFiles()) {
            result = result.withFallback(ConfigFactory.load());
        }

        return result;
    }

    private Config getConfig(Args args) {
        Map<String, String> values = args.getValues();
        Map<String, String> configValues = values.entrySet().stream().collect(Collectors.toMap(
                e -> String.format("args.%s", e.getKey()),
                Map.Entry::getValue
        ));

        return ConfigFactory.parseMap(configValues, "Command line arguments");
    }
}
