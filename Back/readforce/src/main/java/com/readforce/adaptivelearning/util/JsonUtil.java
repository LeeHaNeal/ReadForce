package com.readforce.adaptivelearning.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Double> toMap(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of();
        }
    }
}

