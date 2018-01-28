package joni.dep.components.containers;

import com.typesafe.config.Config;
import joni.dep.CustomContainer;
import joni.dep.components.SecondComponent;
import joni.dep.components.SecondComponentImpl;
import joni.dep.components.ThirdComponent;

public class SecondContainer extends CustomContainer {
    public SecondContainer(Config config) {
        super(config);
    }

    public SecondComponent getSecondComponent() {
        return new SecondComponentImpl(get(ThirdComponent.class));
    }
}
