package joni.dep;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerDescriber {
    public ContainerDefinition describe(CustomContainer container) {
        Method[] methods = container.getClass().getDeclaredMethods();
        List<Method> instanceCreationMethods = Arrays.stream(methods)
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> m.getParameterCount() == 0)
                .collect(Collectors.toList());
        if (instanceCreationMethods.isEmpty()) {
            throw new ContainerException(String.format("Container %s doesn't have any valid instance creation method",
                    container.getClass().getCanonicalName()));
        }

        List<Blueprint> blueprints = new ArrayList<>();

        for (Method method : instanceCreationMethods) {
            Class returnType = method.getReturnType();
            String qualifier = getQualifier(method);
            blueprints.add(new Blueprint(returnType, qualifier, method, container));

            List<Class> supertypes = Reflection.getSupertypes(returnType);
            for (Class superklass : supertypes) {
                blueprints.add(new Blueprint(superklass, qualifier, method, container));
            }
        }

        return new ContainerDefinition(blueprints);
    }

    private String getQualifier(Method method) {
        try {
            Qualifier qualifier = method.getAnnotation(Qualifier.class);
            return qualifier.value();
        } catch (NullPointerException e) {
            return "primary";
        }
    }
}
