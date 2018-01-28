package joni.dep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ContainerCollection implements Container {
    private final Collection<CustomContainer> customContainers;
    private final InstanceCache instanceCache;
    private final ContainerIndex containerIndex;
    private final List<Dependency> callChain;
    private final Logger logger = LoggerFactory.getLogger(ContainerCollection.class);

    ContainerCollection(Collection<CustomContainer> customContainers) {
        this.customContainers = customContainers;
        this.customContainers.forEach(c -> c.setContainerCollection(this));
        this.instanceCache = new InstanceCache();
        this.containerIndex = new ContainerIndex(customContainers);
        this.callChain = new ArrayList<>();
    }

    @Override
    public <T> T get(Class<T> klass) {
        return get(klass, "primary");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> klass, String qualifier) {
        callChain.clear();
        return doGet(klass, qualifier);
    }

    @SuppressWarnings("unchecked")
    <T> T doGet(Class<T> klass, String qualifier) {
        logger.info("Getting instance of {}:{}", klass.getCanonicalName(), qualifier);

        T result;
        if (instanceCache.contains(klass, qualifier)) {
            result = (T) instanceCache.get(klass, qualifier);
            logger.info("Instance found on cache {}", result);
        } else {
            Blueprint blueprint = containerIndex.getBlueprint(klass, qualifier);
            if (blueprint == null) {
                String message = String.format("Cannot find instance for class %s and qualifier %s",
                        klass.getCanonicalName(), qualifier);
                throw new ContainerException(message);
            } else {
                callChain.add(new Dependency(klass, qualifier));
                result = (T) blueprint.createInstance();
                instanceCache.put(klass, qualifier, result);
            }
        }

        return result;
    }

    boolean callChainIncludes(Class klass, String qualifier) {
        Dependency dependency = new Dependency(klass, qualifier);
        return callChain.stream().anyMatch(dependency::equals);
    }

    ContainerException circularDependencyException(Class klass, String qualifier) {
        String message = String.format("Circular dependency on %s:%s",
                klass.getCanonicalName(), qualifier);
        return new ContainerException(message);
    }
}
