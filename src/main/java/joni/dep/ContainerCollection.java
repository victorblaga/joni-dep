package joni.dep;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

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
        try {
            return doGet(klass, qualifier);
        } catch (ContainerException e) {
            throw underlyingException(e);
        }
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
                String message = String.format("Cannot find instance for [%s:%s]",
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
        List<String> callChainDescriptions = callChain.stream().map(Dependency::description).collect(Collectors.toList());
        String dependencyChain = Joiner.on(",").join(callChainDescriptions);
        String message = String.format("Circular dependency [%s:%s]. Previous dependencies: [%s]",
                klass.getCanonicalName(), qualifier, dependencyChain);
        return new ContainerException(message);
    }

    private ContainerException underlyingException(ContainerException e) {
        Throwable result = e;
        while (result.getCause() != null) {
            result = result.getCause();
        }

        if (result instanceof ContainerException) {
            return (ContainerException) result;
        } else {
            throw new RuntimeException(result);
        }
    }
}
