package com.readforce.administrator.controller.passage;

import com.readforce.administrator.dto.AdministratorUploadPassageRequestDto;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.ClassificationEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.enums.TypeEnum;
import com.readforce.passage.service.PassageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/administrator/passage")
@RequiredArgsConstructor
public class AdministratorPassageController {

    private final PassageService passageService;

    @PostMapping("/upload-passage")
    public ResponseEntity<Map<String, String>> uploadPassage(
            @RequestPart("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("language") LanguageEnum language,
            @RequestParam("classification") ClassificationEnum classification,
            @RequestParam("category") CategoryEnum category,
            @RequestParam("type") TypeEnum type,
            @RequestParam("level") Integer level
    ) {
        // DTO 생성
        AdministratorUploadPassageRequestDto requestDto = AdministratorUploadPassageRequestDto.builder()
                .file(file)
                .title(title)
                .author(author)
                .language(language)
                .classification(classification)
                .category(category)
                .type(type)
                .level(level)
                .build();

        // 서비스 호출
        passageService.uploadPassage(requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                MessageCode.MESSAGE_CODE, MessageCode.UPLOAD_PASSAGE_SUCCESS
        ));
    }
}
