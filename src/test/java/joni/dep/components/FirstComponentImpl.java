package joni.dep.components;

public class FirstComponentImpl implements FirstComponent {
    private final SecondComponent secondComponent;
    private final String param;

    public FirstComponentImpl(SecondComponent secondComponent, String param) {
        this.secondComponent = secondComponent;
        this.param = param;
    }

    public FirstComponentImpl(String param) {
        this(null, param);
    }
}
