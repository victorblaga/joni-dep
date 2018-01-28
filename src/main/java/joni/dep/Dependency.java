package joni.dep;

import java.util.Objects;

class Dependency {
    private final Class klass;
    private final String qualifier;
    Dependency(Class klass, String qualifier) {
        this.klass = klass;
        this.qualifier = qualifier;
    }

    public boolean equals(Class klass, String qualifier) {
        return this.klass.equals(klass) && this.qualifier.equals(qualifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return Objects.equals(klass, that.klass) &&
                Objects.equals(qualifier, that.qualifier);
    }

    @Override
    public int hashCode() {

        return Objects.hash(klass, qualifier);
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "klass=" + klass.getCanonicalName() +
                ", qualifier='" + qualifier + '\'' +
                '}';
    }

    public String description() {
        return String.format("%s:%s", klass.getCanonicalName(), qualifier);
    }
}
