package joni.dep;

public class ContainerException extends RuntimeException {
    public ContainerException(Throwable e) {
        super(e);
    }

    public ContainerException(String message) {
        super(message);
    }
}
