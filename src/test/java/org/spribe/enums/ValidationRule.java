package org.spribe.enums;

import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.BiConsumer;

import static org.testng.Assert.*;

@AllArgsConstructor
@Getter
public enum ValidationRule {

    UNIQUE((response, field) -> {
        List<?> list = JsonPath.read(response, "$.." + field);
        List<?> distinctList = list.stream().distinct().collect(java.util.stream.Collectors.toList());
        assertEquals(distinctList.size(), list.size());
    });

    private final BiConsumer<String, String> function;
}
