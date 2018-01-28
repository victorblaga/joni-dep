package joni.dep;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import joni.dep.components.FirstComponent;
import org.junit.Test;

public class ContainerScannerTest {
    @Test
    public void test() throws Exception {
        Config config = ConfigFactory.load();
        Container container = new ContainerScanner(config, "joni.dep.containers").scan();
        FirstComponent firstComponent = container.get(FirstComponent.class);
    }
}
