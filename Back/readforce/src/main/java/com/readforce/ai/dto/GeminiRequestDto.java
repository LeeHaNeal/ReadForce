package com.readforce.ai.dto;

import com.readforce.common.enums.Category;
import com.readforce.common.enums.Classification;
import com.readforce.common.enums.Language;
import com.readforce.common.enums.Type;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeminiRequestDto {
    private Category category;         // enum으로 변경
    private Type type;                 // enum으로 변경
    private Language language;         // enum으로 변경
    private int level;
    private Classification classification; // enum으로 변경
    private Long passageNo;
    private String passageText;
    private String title;
}
