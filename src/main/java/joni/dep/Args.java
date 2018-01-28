package joni.dep;

import java.util.List;
import java.util.Map;

class Args {
    private final Map<String, String> values;
    private final List<String> resources;
    private final List<String> files;

    public Args(Map<String, String> values) {
        this.values = values;
        this.resources = Utils.split(values.getOrDefault("resources", ""));
        this.files = Utils.split(values.getOrDefault("files", ""));
    }

    public boolean hasResources() {
        return !resources.isEmpty();
    }

    public List<String> getResources() {
        return resources;
    }

    public boolean hasFiles() {
        return !files.isEmpty();
    }

    public List<String> getFiles() {
        return files;
    }

    public Map<String, String> getValues() {
        return this.values;
    }
}
