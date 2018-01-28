package joni.dep;

import com.google.common.io.Resources;
import com.typesafe.config.Config;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigParserTest {
    private ConfigParser configParser = new ConfigParser();

    @Test
    public void parse() {
        Config config = configParser.parse(argv("--option1=val1 -option2 val2 --flag"));
        assertTrue(config.getBoolean("args.flag"));
        assertEquals("val1", config.getString("args.option1"));
        assertEquals("val2", config.getString("args.option2"));
        assertEquals("val1", config.getString("app.module1.module2.key1"));
        assertEquals("val2", config.getString("app.module1.module2.key2"));
        assertEquals("value1", config.getString("ref.module1.key1"));
        assertTrue(config.getBoolean("ref.module1.key2"));
        assertEquals(13, config.getInt("ref.module1.key3"));
        assertEquals("app", config.getString("overwrite"));
    }

    @Test
    public void parse_from_resources_and_files() throws IOException {
        String tempPath = copyTemplateToTempFile("file1.conf.template");
        Config config = configParser.parse(argv("-arg1=val1 --resources=res1.conf,res2.conf -files " + tempPath));
        assertEquals("val1", config.getString("args.arg1"));
        assertEquals("value1", config.getString("res1.key1"));
        assertEquals("value2", config.getString("res1.key2"));
        assertEquals("value3", config.getString("res1.key3"));
        assertEquals("value1", config.getString("res2.key1"));
        assertEquals("value2", config.getString("res2.key2"));
        assertEquals("value3", config.getString("res2.key3"));
        assertEquals("value1", config.getString("file1.key1"));
        assertEquals("value2", config.getString("file1.key2"));
        assertEquals("value3", config.getString("file1.key3"));
        assertEquals("res1", config.getString("overwrite"));
    }

    private String[] argv(String str) {
        return str.split("\\s+");
    }

    private String copyTemplateToTempFile(String resourceName) throws IOException {
        URL url = Resources.getResource(resourceName);
        byte[] contents = Resources.toByteArray(url);
        File tempFile = File.createTempFile("test", ".conf");
        tempFile.deleteOnExit();
        OutputStream outputStream = new FileOutputStream(tempFile);
        outputStream.write(contents);
        outputStream.close();

        return tempFile.getAbsolutePath();
    }
}