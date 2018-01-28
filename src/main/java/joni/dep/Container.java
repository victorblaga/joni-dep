package joni.dep;

public interface Container {
    <T> T get(Class<T> klass);
    <T> T get(Class<T> klass, String qualifier);
}
