package com.readforce.adaptivelearning.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;

public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Double> toMap(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        try {
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}
