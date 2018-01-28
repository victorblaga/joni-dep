package joni.dep.components.containers;

import com.typesafe.config.Config;
import joni.dep.CustomContainer;
import joni.dep.Qualifier;
import joni.dep.components.*;

public class FirstContainer extends CustomContainer {
    public FirstContainer(Config config) {
        super(config);
    }

    public FirstComponent getFirstComponent() {
        return new FirstComponentImpl(
                get(SecondComponent.class),
                "test"
        );
    }

    @Qualifier("simple")
    public FirstComponent getFirstComponentSimple() {
        return new FirstComponentImpl(
                "test"
        );
    }

    public ThirdComponent getThirdComponent() {
        return new ThirdComponentImpl(
                get(FirstComponent.class, "simple")
        );
    }
}
