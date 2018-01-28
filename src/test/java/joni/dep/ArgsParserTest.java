package joni.dep;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class ArgsParserTest {

    private ArgsParser argsParser = new ArgsParser();

    @Test
    public void parse() {
        Args args = argsParser.parse(argv("--option-one=value1 --flag -opt2 val2 --opt3 val3 --flag3"));

        Map<String, String> values = args.getValues();
        assertEquals("value1", values.get("option-one"));
        assertEquals("val2", values.get("opt2"));
        assertEquals("val3", values.get("opt3"));

        assertTrue(values.containsKey("flag"));
        assertTrue(values.containsKey("flag3"));

        assertFalse(args.hasFiles());
        assertFalse(args.hasResources());

        args = argsParser.parse(argv("--resources resource1,resource2 -files file/name/here --flag -another-flag"));
        values = args.getValues();
        assertEquals("resource1,resource2", values.get("resources"));
        assertThat(args.getResources(), contains("resource1", "resource2"));

        assertEquals("file/name/here", values.get("files"));
        assertThat(args.getFiles(), contains("file/name/here"));

        assertTrue(values.containsKey("flag"));
        assertTrue(values.containsKey("another-flag"));
    }

    private String[] argv(String string) {
        return string.split("\\s+");
    }
}