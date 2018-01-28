package joni.dep;

import com.typesafe.config.Config;

import java.util.Optional;

public abstract class CustomContainer implements Container {
    protected final Config config;
    private ContainerCollection containerCollection;

    public CustomContainer(Config config) {
        this.config = config;
    }

    public void setContainerCollection(ContainerCollection containerCollection) {
        this.containerCollection = containerCollection;
    }

    @Override
    public <T> T get(Class<T> klass) {
        return get(klass, "primary");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> klass, String qualifier) {
        if (this.containerCollection == null) {
            // this custom container is used in a stand-alone fashion
            ContainerDefinition containerDefinition = new ContainerDescriber().describe(this);
            Optional<Blueprint> blueprint = containerDefinition.getBlueprint(klass, qualifier);
            if (blueprint.isPresent()) {
                return (T) blueprint.get().createInstance();
            } else {
                throw new ContainerException(String.format("Cannot create instance of %s with qualifier %s",
                        klass.getCanonicalName(), qualifier));
            }
        } else {
            // this custom container is used as part of a container collection
            if (containerCollection.callChainIncludes(klass, qualifier)) {
                throw containerCollection.circularDependencyException(klass, qualifier);
            }

            return containerCollection.doGet(klass, qualifier);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName();
    }
}
