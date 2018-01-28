package joni.dep;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import joni.dep.components.FirstComponent;
import org.junit.Test;

public class ContainerBuilderTest {
    @Test
    public void test() throws Exception {
        Config config = ConfigFactory.load();
        Container container = new ContainerBuilder(config, "joni.dep.containers").build();
        FirstComponent firstComponent = container.get(FirstComponent.class);
    }
}
