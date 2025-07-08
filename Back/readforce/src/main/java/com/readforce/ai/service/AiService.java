package com.readforce.ai.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readforce.ai.ApiException;
import com.readforce.ai.dto.AiGeneratePassageRequestDto;
import com.readforce.ai.dto.GeminiGeneratePassageResponseDto;
import com.readforce.ai.dto.GeminiGenerateQuestionResponseDto;
import com.readforce.ai.dto.GeminiGenerateTestPassageAndQuestionResponseDto;
import com.readforce.ai.dto.GeminiGenerateTestPassageResponseDto;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.ClassificationEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.enums.NameEnum;
import com.readforce.common.enums.TypeEnum;
import com.readforce.common.exception.JsonException;
import com.readforce.passage.dto.PassageResponseDto;
import com.readforce.passage.entity.Category;
import com.readforce.passage.entity.Classification;
import com.readforce.passage.entity.Language;
import com.readforce.passage.entity.Level;
import com.readforce.passage.entity.Passage;
import com.readforce.passage.service.CategoryService;
import com.readforce.passage.service.ClassificationService;
import com.readforce.passage.service.LanguageService;
import com.readforce.passage.service.LevelService;
import com.readforce.passage.service.PassageService;
import com.readforce.question.entity.Choice;
import com.readforce.question.entity.MultipleChoice;
import com.readforce.question.service.MultipleChoiceService;
import com.readforce.question.service.QuestionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {

    private final LevelService levelService;
    private final CategoryService categoryService;
    private final LanguageService languageService;
    private final RestTemplate restTemplate;
    private final PassageService passageService;
    private final ObjectMapper objectMapper;
    private final ClassificationService classificationService;
    private final QuestionService questionService;
    private final MultipleChoiceService multipleChoiceService;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Transactional
    public void generateTestVocabulary(LanguageEnum languageEnum) {

        Language language = languageService.getLangeageByLanguage(languageEnum);

        List<Level> levelList = levelService.getAllLevelList();

        Classification classification = classificationService.getClassificationByClassfication(ClassificationEnum.TEST);

        for (Level level : levelList) {

            String prompt = gernerateTestVocabularyPrompt(language, level);

            Map<String, Object> requestResult = requestGenerate(prompt);


            String content = extractContentFromResponse(requestResult);
            
            System.out.println("DEBUG Gemini raw response: " + content);  // 응답 내용 확인용

            GeminiGenerateTestPassageResponseDto parsedResult = parsingResponse(content);

            String author = NameEnum.GEMINI.name();

            LocalDate publicationDate = LocalDate.now();

            Category categoryEntity = categoryService.getCategoryByCategory(CategoryEnum.VOCABULARY);

            passageService.savePassage(parsedResult.getTitle(), parsedResult.getContent(), author, publicationDate, categoryEntity, level, language, classification);
        }
    }

    private GeminiGenerateTestPassageResponseDto parsingResponse(String requestResult) {
        try {
            return objectMapper.readValue(requestResult, GeminiGenerateTestPassageResponseDto.class);
        } catch (Exception exception) {
            throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);
        }
    }

    private Map<String, Object> requestGenerate(String prompt) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of("contents", List.of(content));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, httpHeaders);

        String url = geminiApiUrl + "?key=" + geminiApiKey;

        try {
            return restTemplate.postForObject(url, requestEntity, Map.class);
        } catch (Exception exception) {
            throw new ApiException(MessageCode.GEMINI_API_REQUEST_FAIL);
        }
    }

    private String gernerateTestVocabularyPrompt(Language language, Level level) {

        String prompt = "";

        switch(language.getLanguageName()) {
            case KOREAN:
                prompt = String.format(
                """
                당신은 한국어 어휘의 전문가입니다.
                '난이도 %d (%s)' 수준의 단어를 JSON 형식으로 생성해 주세요.
                **반드시 하나의 JSON 객체만 반환해야 합니다.**

                요청 형식:
                {
                  "title": "여기에 단어를 생성해주세요.",
                  "content": "여기에 단어의 사전적 의미를 작성해주세요.",
                  "level": "여기에 단어 생성에 사용된 난이도를 적어주세요."
                }
                """, level.getLevelNumber(), level.getVocabularyLevel());
                break;
            default:
        }

        return prompt;
    }

    @Transactional
    public void generateTestQuestion(LanguageEnum languageEnum) {

        Language language = languageService.getLangeageByLanguage(languageEnum);
        Classification classification = classificationService.getClassificationByClassfication(ClassificationEnum.TEST);

        List<CategoryEnum> testCategoryList = new ArrayList<>();
        testCategoryList.add(CategoryEnum.VOCABULARY);
        testCategoryList.add(CategoryEnum.FACTUAL);
        testCategoryList.add(CategoryEnum.INFERENTIAL);

        for (CategoryEnum testCategory : testCategoryList) {

            if (testCategory == CategoryEnum.VOCABULARY) {

                List<PassageResponseDto> unusedPassageList = questionService.getUnusedVocabularyPassageList(languageEnum, ClassificationEnum.TEST);

                if (unusedPassageList.isEmpty()) {
                    continue;
                }

                PassageResponseDto randomPassageDto = unusedPassageList.get(new Random().nextInt(unusedPassageList.size()));
                Passage passage = passageService.getPassageByPassageNo(randomPassageDto.getPassageNo());

                String prompt = gernerateTestVocabularyQuestionPrompt(language, passage.getLevel(), passage.getTitle(), passage.getContent());

                Map<String, Object> requestResult = requestGenerate(prompt);

                String content = extractContentFromResponse(requestResult);

                GeminiGenerateTestPassageAndQuestionResponseDto parsedResult = parsePassageAndQuestionResponse(content);

                saveMultipleChoiceQuestion(passage, parsedResult);

            } else {

                for (Level level : levelService.getAllLevelList()) {

                    String prompt = (testCategory == CategoryEnum.FACTUAL)
                            ? generateTestFactualPassageAndQuestionPrompt(language, level)
                            : generateTestInferentialPassageAndQuestionPrompt(language, level);

                    Map<String, Object> requestResult = requestGenerate(prompt);

                    String content = extractContentFromResponse(requestResult);

                    GeminiGenerateTestPassageAndQuestionResponseDto parsedResult = parsePassageAndQuestionResponse(content);

                    Category categoryEntity = categoryService.getCategoryByCategory(testCategory);

                    Passage newPassage = passageService.savePassage(parsedResult.getTitle(), parsedResult.getContent(), NameEnum.GEMINI.name(), LocalDate.now(), categoryEntity, level, language, classification);

                    saveMultipleChoiceQuestion(newPassage, parsedResult);
                }
            }
        }
    }
    private GeminiGenerateTestPassageAndQuestionResponseDto parsePassageAndQuestionResponse(String requestResult) {

        try {
            return objectMapper.readValue(requestResult, GeminiGenerateTestPassageAndQuestionResponseDto.class);
        } catch (Exception exception) {
            throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);
        }
    }

    private void saveMultipleChoiceQuestion(Passage passage, GeminiGenerateTestPassageAndQuestionResponseDto parsedResult) {

        List<Choice> choiceList = new ArrayList<>();
        Map<String, String> explanationMap = parsedResult.getExplanation();

        for (int i = 0; i < parsedResult.getChoiceList().size(); i++) {

            boolean isCorrect = (i == parsedResult.getCorrectAnswerIndex());

            String explanationText = isCorrect
                    ? explanationMap.getOrDefault("correct", "정답에 대한 설명이 없습니다.")
                    : explanationMap.getOrDefault("incorrect", "오답에 대한 설명이 없습니다.");

            Choice choice = Choice.builder()
                    .choiceIndex(i)
                    .content(parsedResult.getChoiceList().get(i))
                    .isCorrect(isCorrect)
                    .explanation(explanationText)
                    .build();

            choiceList.add(choice);
        }

        MultipleChoice multipleChoice = MultipleChoice.builder()
                .passage(passage)
                .question(parsedResult.getQuestion())
                .choiceList(choiceList)
                .build();

        multipleChoiceService.saveMultipleChoice(multipleChoice);
    }

    private void saveMultipleChoiceQuestion(Passage passage, GeminiGenerateQuestionResponseDto parsedResult) {

        List<Choice> choiceList = new ArrayList<>();
        Map<String, String> explanationMap = parsedResult.getExplanation();

        for (int i = 0; i < parsedResult.getChoiceList().size(); i++) {

            boolean isCorrect = (i == parsedResult.getCorrectAnswerIndex());

            String explanationText = isCorrect
                    ? explanationMap.getOrDefault("correct", "정답에 대한 설명이 없습니다.")
                    : explanationMap.getOrDefault("incorrect", "오답에 대한 설명이 없습니다.");

            Choice choice = Choice.builder()
                    .choiceIndex(i)
                    .content(parsedResult.getChoiceList().get(i))
                    .isCorrect(isCorrect)
                    .explanation(explanationText)
                    .build();

            choiceList.add(choice);
        }

        MultipleChoice multipleChoice = MultipleChoice.builder()
                .passage(passage)
                .question(parsedResult.getQuestion())
                .choiceList(choiceList)
                .build();

        multipleChoiceService.saveMultipleChoice(multipleChoice);
    }
    private String gernerateTestVocabularyQuestionPrompt(Language language, Level level, String title, String content) {

        String prompt = "";

        switch (language.getLanguageName()) {

            case KOREAN:
                prompt = String.format("""
                        당신은 한국어 어휘 문제 출제 전문가입니다.
                        '난이도 %d (%s)' 수준의 단어인 '%s'의 의미를 묻는 객관식 문제를 생성해 주세요.
                        아래 예시 문장을 문제에 반드시 포함하고, 정답 1개와 매력적인 오답 3개를 포함한 총 4개의 선택지를 만들어 주세요.
                        정답의 위치는 무작위로 설정해 주세요.

                        - 단어: %s
                        - 예시 문장: %s

                        반드시 아래의 JSON 형식으로만 응답해 주세요:
                        {
                            "question": "생성된 질문",
                            "choiceList": ["선택지 1", "선택지 2", "선택지 3", "선택지 4"],
                            "correctAnswerIndex": 정답_선택지의_인덱스 (0-3),
                            "explanation": {
                                "correct": "정답 해설...",
                                "incorrect": "오답 해설..."
                            }
                        }
                """, level.getLevelNumber(), level.getVocabularyLevel(), title, title, content);
                break;

            default:
        }

        return prompt;
    }

    private String generateTestFactualPassageAndQuestionPrompt(Language language, Level level) {

        String prompt = "";

        switch (language.getLanguageName()) {

            case KOREAN:
                prompt = String.format("""
                        당신은 한국어 '사실적 이해' 문제 출제 전문가입니다.
                        '난이도 %d (%s)' 수준의 짧은 글을 생성하고, 글의 내용과 일치하는 내용을 찾는 객관식 문제를 JSON 형식으로 생성해 주세요.
                        정답 1개와 매력적인 오답 3개를 포함하고, 정답의 위치는 무작위로 설정해 주세요.

                        요청 형식:
                        {
                            "title": "여기에 지문 제목을 생성해주세요.",
                            "content": "여기에 지문 내용을 생성해주세요.",
                            "question": "생성된 질문",
                            "choiceList": ["선택지 1", "선택지 2", "선택지 3", "선택지 4"],
                            "correctAnswerIndex": 정답_선택지의_인덱스 (0-3),
                            "explanation": {
                                "correct": "정답 해설...",
                                "incorrect": "오답 해설..."
                            }
                        }
                """, level.getLevelNumber(), level.getVocabularyLevel());
                break;

            default:
        }

        return prompt;
    }

    private String generateTestInferentialPassageAndQuestionPrompt(Language language, Level level) {

        String prompt = "";

        switch (language.getLanguageName()) {

            case KOREAN:
                prompt = String.format("""
                        당신은 한국어 '추론적 이해' 문제 출제 전문가입니다.
                        '난이도 %d (%s)' 수준의 짧은 글을 생성하고, 글의 내용을 바탕으로 추론할 수 있는 내용을 찾는 객관식 문제를 JSON 형식으로 생성해 주세요.
                        정답 1개와 매력적인 오답 3개를 포함하고, 정답의 위치는 무작위로 설정해 주세요.

                        요청 형식:
                        {
                            "title": "여기에 지문 제목을 생성해주세요.",
                            "content": "여기에 지문 내용을 생성해주세요.",
                            "question": "생성된 질문",
                            "choiceList": ["선택지 1", "선택지 2", "선택지 3", "선택지 4"],
                            "correctAnswerIndex": 정답_선택지의_인덱스 (0-3),
                            "explanation": {
                                "correct": "정답 해설...",
                                "incorrect": "오답 해설..."
                            }
                        }
                """, level.getLevelNumber(), level.getVocabularyLevel());
                break;

            default:
        }

        return prompt;
    }

    @Transactional
    public void generatePassage(AiGeneratePassageRequestDto aiGeneratePassageRequestDto) {

        Level level = levelService.getLevelByLevel(aiGeneratePassageRequestDto.getLevel());

        Language language = languageService.getLangeageByLanguage(aiGeneratePassageRequestDto.getLanguage());

        Classification classification = classificationService.getClassificationByClassfication(aiGeneratePassageRequestDto.getClassification());

        String prompt = generatePassagePrompt(aiGeneratePassageRequestDto);

        Map<String, Object> requestResult = requestGenerate(prompt);

        String content = extractContentFromResponse(requestResult);

        GeminiGeneratePassageResponseDto parsedResult = parsePassageResponse(content);

        String author = NameEnum.GEMINI.name();

        LocalDate publicationDate = LocalDate.now();

        Category category = categoryService.getCategoryByCategory(aiGeneratePassageRequestDto.getCategory());

        passageService.savePassage(
                parsedResult.getTitle(),
                parsedResult.getContent(),
                author,
                publicationDate,
                category,
                level,
                language,
                classification
        );
    }
    private GeminiGeneratePassageResponseDto parsePassageResponse(String requestResult) {

        try {
            return objectMapper.readValue(requestResult, GeminiGeneratePassageResponseDto.class);
        } catch (Exception exception) {
            throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);
        }

    }

    private String generatePassagePrompt(AiGeneratePassageRequestDto aiGeneratePassageRequestDto) {

        String prompt = "";

        String languageString = getLanguageString(aiGeneratePassageRequestDto.getLanguage());

        String categoryString = getCategoryString(
                aiGeneratePassageRequestDto.getLanguage(),
                aiGeneratePassageRequestDto.getCategory()
        );

        String typeString = getTypeString(
                aiGeneratePassageRequestDto.getLanguage(),
                aiGeneratePassageRequestDto.getCategory(),
                aiGeneratePassageRequestDto.getType()
        );

        Level level = levelService.getLevelByLevel(aiGeneratePassageRequestDto.getLevel());

        switch (aiGeneratePassageRequestDto.getCategory()) {

            case NEWS:

                switch (aiGeneratePassageRequestDto.getLanguage()) {

                    case KOREAN:
                        prompt = String.format("""
                                당신은 %s 분야를 다루는 전문 기자입니다. 지금부터 다음 조건에 맞춰 뉴스 기사를 생성해 주세요.

                                1.  **언어:** %s
                                2.  **카테고리:** %s
                                3.  **타입:** %s
                                4.  **난이도:** 전체 10의 난이도 중 %s
                                    * **어휘:** %s
                                    * **문장 구조:** %s
                                    * **내용:** 사실에 기반하되, 독자의 흥미를 유발할 수 있도록 구성해 주세요.
                                    * **분량:** 제목 포함 %d개 문단으로 구성해 주세요.

                                5.  **출력 형식:** 아래의 JSON 형식에 맞춰서 출력해 주세요. `title`과 `body` 외의 다른 설명은 추가하지 마세요.

                                {
                                  "title": "기사 제목",
                                  "content": "기사 본문 내용. 문단 구분을 위해 '\\n\\n'를 사용해 주세요."
                                }

                        """,
                                typeString,
                                languageString,
                                categoryString,
                                typeString,
                                level.getLevelNumber(),
                                level.getVocabularyLevel(),
                                level.getSentenceStructure(),
                                level.getParagraphCount()
                        );
                        break;

                    default:
                }

                break;

            default:

        }

        return prompt;
    }
    private String getCategoryString(LanguageEnum language, CategoryEnum category) {

        switch (language) {

            case KOREAN:

                switch (category) {

                    case NEWS:
                        return "뉴스";

                    case NOVEL:
                        return "소설";

                    case FAIRY_TALE:
                        return "동화";

                    default:
                }

            case ENGLISH:

            case JAPANESE:

            default:

        }
        return null;
    }

    private String getLanguageString(LanguageEnum language) {

        switch (language) {

            case KOREAN:
                return "한국어";

            case ENGLISH:
                return "English";

            case JAPANESE:
                return "日本語";

            default:

        }

        return null;
    }

    private String getTypeString(LanguageEnum language, CategoryEnum category, TypeEnum type) {

        switch (language) {

            case KOREAN:

                switch (category) {

                    case NEWS:

                        switch (type) {

                            case POLITICS:
                                return "정부 정책, 법안, 선거";
                            case ECONOMY:
                                return "경제 지표, 기업, 시장";
                            case SOCIETY:
                                return "교육, 환경, 사건";
                            case LIFE_AND_CULTURE:
                                return "문화·라이프스타일, 건강";
                            case IT_AND_SCIENCE:
                                return "AI, 우주, 기술 혁신";
                            case WORLD:
                                return "국제 사건, 글로벌 이슈";
                            case SPORTS:
                                return "경기 결과, 선수";
                            case ENTERTAINMENT:
                                return "영화, 스타 활동";
                            default:
                        }

                        break;

                    default:

                }

                break;

            case ENGLISH:

            case JAPANESE:

            default:

        }

        return null;
    }
    public void generateQuestion() {

        List<Passage> noQuestionPassageList = passageService.getNoQuestionPassage();

        for (Passage passage : noQuestionPassageList) {

            String prompt = generateQuestionPrompt(passage);

            Map<String, Object> requestResult = requestGenerate(prompt);

            String content = extractContentFromResponse(requestResult);

            GeminiGenerateQuestionResponseDto parsedResult = parseQuestionResponse(content);

            saveMultipleChoiceQuestion(passage, parsedResult);

        }

    }

    private GeminiGenerateQuestionResponseDto parseQuestionResponse(String requestResult) {

        try {

            return objectMapper.readValue(requestResult, GeminiGenerateQuestionResponseDto.class);

        } catch (Exception exception) {

            throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);

        }

    }
    private String generateQuestionPrompt(Passage passage) {

        String prompt = "";

        switch (passage.getLanguage().getLanguageName()) {

            case KOREAN:
                prompt = String.format("""
                    당신은 문해력 평가 전문가입니다. 주어진 텍스트와 그 텍스트의 난이도에 맞춰, 학생의 이해도를 정확히 측정할 수 있는 문제와 상세한 해설을 생성해 주세요.

                    1.  **주어진 텍스트:**
                        {%s}

                    2.  **텍스트의 난이도:** 전체 10의 난이도 중 {%d}

                    3.  **생성 조건:**
                        * **질문 유형:** {%s}에 해당하는 질문 유형으로 문제를 생성해야 합니다.
                        * **문제 수:** {%d}
                        * **선택지 구성:** 문제의 난이도에 맞게 오답 선택지의 매력도를 조절해 주세요. (예: 난이도가 높을수록 오답이 더 정교하고 미묘해야 함)
                        * **해설 생성:** 정답의 근거와 오답이 틀린 이유를 명확하게 설명하는 해설을 포함해 주세요.

                    4.  **출력 형식:** 아래의 JSON 배열 형식에 맞춰서, 다른 설명 없이 JSON 데이터만 출력해 주세요.

                    [
                      {
                        "question": "문제 내용",
                        "choiceList": ["선택지 1", "선택지 2", "선택지 3", "선택지 4"],
                        "correctAnswerIndex": 0,
                        "explanation": {
                          "correct": "정답 해설...",
                          "incorrect": "오답 해설..."
                        }
                      }
                    ]
                """,
                passage.getContent(),
                passage.getLevel().getLevelNumber(),
                passage.getLevel().getQuestionType(),
                3);
                break;

            default:

        }

        return prompt;
    }

    private String extractContentFromResponse(Map<String, Object> response) {

        if (response != null && response.containsKey("candidates")) {

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");

            if (candidates != null && !candidates.isEmpty()) {

                Map<String, Object> firstCandidate = candidates.get(0);

                if (firstCandidate.containsKey("content")) {

                    Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");

                    if (content.containsKey("parts")) {

                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

                        if (parts != null && !parts.isEmpty()) {

                            Map<String, Object> firstPart = parts.get(0);

                            if (firstPart.containsKey("text")) {

                                return (String) firstPart.get("text");

                            }

                        }

                    }

                }

            }

        }

        throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);

    }

}
