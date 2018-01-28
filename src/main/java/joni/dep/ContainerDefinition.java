package joni.dep;

import java.util.List;
import java.util.Optional;

public class ContainerDefinition {
    private final List<Blueprint> blueprints;

    public ContainerDefinition(List<Blueprint> blueprints) {
        this.blueprints = blueprints;
    }

    public List<Blueprint> getBlueprints() {
        return blueprints;
    }

    public Optional<Blueprint> getBlueprint(Class klass, String qualifier) {
        return this.blueprints.stream()
                .filter(blueprint -> blueprint.getKlass().equals(klass) && blueprint.getQualifier().equals(qualifier))
                .findFirst();
    }
}
