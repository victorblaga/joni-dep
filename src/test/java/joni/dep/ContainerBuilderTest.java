package joni.dep;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import joni.dep.circular.FirstCircular;
import joni.dep.components.FirstComponent;
import joni.dep.components.FirstComponentImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContainerBuilderTest {
    @Test
    public void test() throws Exception {
        Config config = ConfigFactory.load();
        Container container = new ContainerBuilder(config, "joni.dep.components.containers").build();
        FirstComponent firstComponent = container.get(FirstComponent.class);
        assertEquals(FirstComponentImpl.class, firstComponent.getClass());
    }

    @Test
    public void circular() throws Exception {
        Config config = ConfigFactory.load();
        Container container = new ContainerBuilder(config, "joni.dep.circular.containers").build();
        try{
            container.get(FirstCircular.class);
        } catch (ContainerException e) {
            assertTrue(e.getMessage().contains("Circular dependency"));
        }
    }
}
