package com.readforce.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Gemini API 요청 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiApiRequest {

    /**
     * 대화 내용 목록
     */
    private List<Content> contents;

    /**
     * 생성 설정
     */
    @JsonProperty("generation_config")
    private GenerationConfig generationConfig;

    /**
     * 역할 + 메시지 내용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {

        /**
         * 역할 (예: "user", "model")
         */
        private String role;

        /**
         * 내용 파트 리스트
         */
        private List<Part> parts;
    }

    /**
     * 실제 메시지 부분
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Part {

        /**
         * 텍스트 내용
         */
        private String text;
    }

    /**
     * 응답 형식 및 토큰 제어
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GenerationConfig {

        /**
         * 응답 MIME 타입 (예: application/json)
         */
        @JsonProperty("response_mime_type")
        private String responseMimeType;

        /**
         * 최대 출력 토큰 수
         */
        @JsonProperty("max_output_tokens")
        private Integer maxOutputTokens;

        /**
         * 창의성 제어 (온도값)
         */
        private Double temperature;

        // 필요시 topK, topP 추가 가능
    }
}
