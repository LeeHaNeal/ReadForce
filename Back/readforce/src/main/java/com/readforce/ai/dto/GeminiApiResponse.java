package com.readforce.ai.dto;

import lombok.Data;
import java.util.List;

@Data
public class GeminiApiResponse {
    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private Content content;
    }

    @Data
    public static class Content {
        private String role;
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text;
    }
}
