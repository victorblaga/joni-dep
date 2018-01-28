package joni.dep;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

class Utils {
    public static List<String> split(String resources) {
        if (resources == null || resources.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> result = Lists.newArrayList(Splitter.on(",").split(resources));
        return result.stream().map(String::trim).collect(toList());
    }
}
