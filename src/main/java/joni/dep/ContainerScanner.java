package joni.dep;

import com.typesafe.config.Config;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ContainerScanner {
    private final Config config;
    private final List<String> packageNames;
    private final Logger log = LoggerFactory.getLogger(ContainerScanner.class);

    public ContainerScanner(Config config, String... packageNames) {
        this.config = config;
        this.packageNames = Arrays.asList(packageNames);
    }

    public Container scan() {
        Collection<CustomContainer> customContainers = getAllContainers(this.packageNames);
        return new ContainerCollection(customContainers);
    }

    private Collection<CustomContainer> getAllContainers(List<String> packageNames) {
        Map<Class, CustomContainer> containers = new HashMap<>();
        for (String packageName : packageNames) {
            log.info("Scanning package {} for containers", packageName);
            Set<CustomContainer> packageCustomContainers = getContainersFromPackage(packageName);
            log.info("Found {} containers in {}: {}", packageCustomContainers.size(), packageName, packageCustomContainers);
            for (CustomContainer customContainer : packageCustomContainers) {
                containers.put(customContainer.getClass(), customContainer);
            }
        }

        return containers.values();
    }

    private Set<CustomContainer> getContainersFromPackage(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends CustomContainer>> containerClasses = reflections.getSubTypesOf(CustomContainer.class);
        return containerClasses.stream().map(k -> {
            try {
                return k.getConstructor(Config.class);
            } catch (NoSuchMethodException e) {
                throw new ContainerException(e);
            }
        }).map(c -> {
            try {
                return (CustomContainer) c.newInstance(config);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new ContainerException(e);
            }
        }).collect(Collectors.toSet());
    }
}
