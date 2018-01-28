package joni.dep.unavailable.containers;

import com.typesafe.config.Config;
import joni.dep.CustomContainer;
import joni.dep.unavailable.Dependency;
import joni.dep.unavailable.Service;

public class ApplicationContainer extends CustomContainer {
    public ApplicationContainer(Config config) {
        super(config);
    }

    public Service getService() {
        return new Service(get(Dependency.class));
    }
}
