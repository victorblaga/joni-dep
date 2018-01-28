package joni.dep;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Blueprint {
    private final Class klass;
    private final String qualifier;
    private final Method method;
    private final CustomContainer container;

    public Blueprint(Class returnType, String qualifier, Method method, CustomContainer container) {
        this.klass = returnType;
        this.qualifier = qualifier;
        this.method = method;
        this.container = container;
    }

    public Object createInstance() {
        try {
            return method.invoke(this.container);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ContainerException(e);
        }
    }

    public Class getKlass() {
        return klass;
    }

    public String getQualifier() {
        return qualifier;
    }
}
