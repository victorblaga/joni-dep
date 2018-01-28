package joni.dep;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Reflection {
    public static List<Class> getSupertypes(Class klass) {
        Set<Class> result = new HashSet<>();
        Class index = klass.getSuperclass();
        while (index != null && !index.equals(Object.class)) {
            result.add(index);
            index = index.getSuperclass();
        }

        for (Class interf : klass.getInterfaces()) {
            result.add(interf);
            result.addAll(getSupertypes(interf));
        }

        return new ArrayList<>(result);
    }
}
