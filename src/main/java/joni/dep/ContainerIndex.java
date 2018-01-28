package joni.dep;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContainerIndex {
    private final ContainerDescriber containerDescriber;
    private final Collection<CustomContainer> customContainers;
    private final Map<String, Blueprint> blueprintIndex;

    public ContainerIndex(Collection<CustomContainer> customContainers) {
        this.containerDescriber = new ContainerDescriber();
        this.customContainers = customContainers;
        this.blueprintIndex = new HashMap<>();
        List<ContainerDefinition> definitions = getContainerDefinitions(customContainers);
        for (ContainerDefinition containerDefinition : definitions) {
            for (Blueprint blueprint : containerDefinition.getBlueprints()) {
                blueprintIndex.put(key(blueprint.getKlass(), blueprint.getQualifier()), blueprint);
            }
        }
    }

    private List<ContainerDefinition> getContainerDefinitions(Collection<CustomContainer> customContainers) {
        return customContainers.stream().map(this.containerDescriber::describe).collect(Collectors.toList());
    }

    public Blueprint getBlueprint(Class klass, String qualifier) {
        return this.blueprintIndex.get(key(klass, qualifier));
    }

    private String key(Class klass, String qualifier) {
        return String.format("%s:%s", klass.getCanonicalName(), qualifier);
    }
}
