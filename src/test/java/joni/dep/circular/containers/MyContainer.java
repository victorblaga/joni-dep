package joni.dep.circular.containers;

import com.typesafe.config.Config;
import joni.dep.CustomContainer;
import joni.dep.circular.FirstCircular;
import joni.dep.circular.SecondCircular;

public class MyContainer extends CustomContainer {
    public MyContainer(Config config) {
        super(config);
    }

    public FirstCircular getFirst() {
        return new FirstCircular(get(SecondCircular.class));
    }

    public SecondCircular getSecond() {
        return new SecondCircular(get(FirstCircular.class));
    }
}
