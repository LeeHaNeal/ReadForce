package com.readforce.ai.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readforce.ai.dto.*;
import com.readforce.ai.exception.GeminiException;
import com.readforce.common.enums.Category;
import com.readforce.common.enums.Language;
import com.readforce.common.enums.Type;
import com.readforce.passage.entity.Passage;
import com.readforce.passage.service.*;
import com.readforce.question.entity.Choice;
import com.readforce.question.entity.MultipleChoice;
import com.readforce.question.service.QuestionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PassageService passageService;
    private final CategoryService categoryService;
    private final TypeService typeService;
    private final LevelService levelService;
    private final LanguageService languageService;
    private final ClassificationService classificationService;
    private final QuestionService questionService;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // Gemini API 호출 메서드
    private GeminiApiResponse callGeminiApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GeminiApiRequest request = new GeminiApiRequest(
                List.of(new GeminiApiRequest.Content("user", List.of(new GeminiApiRequest.Part(prompt)))),
                new GeminiApiRequest.GenerationConfig("application/json", 1024, 0.3)
        );
        String urlWithKey = geminiApiUrl + "?key=" + geminiApiKey;

        try {
            ResponseEntity<GeminiApiResponse> response = restTemplate.exchange(
                    urlWithKey, HttpMethod.POST, new HttpEntity<>(request, headers), GeminiApiResponse.class
            );
            GeminiApiResponse body = response.getBody();
            log.info("🔽 Gemini API Raw Response: {}", body);
            if (body == null || body.getCandidates().isEmpty()) {
                throw new GeminiException("Gemini API 응답 없음");
            }
            return body;
        } catch (HttpClientErrorException e) {
            log.error("Gemini API 호출 실패: HTTP {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new GeminiException("Gemini API 호출 실패: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("Gemini API 호출 중 예외 발생", e);
            throw new GeminiException("Gemini API 호출 중 예외 발생");
        }
    }
    // 불완전 JSON 보완
    private String fixIncompleteJson(String json) {
        String fixed = json.trim();

        if (!fixed.startsWith("{")) {
            fixed = "{" + fixed;
        }

        int openBraces = fixed.length() - fixed.replace("{", "").length();
        int closeBraces = fixed.length() - fixed.replace("}", "").length();
        if (closeBraces < openBraces) {
            fixed += "}".repeat(openBraces - closeBraces);
        }

        long quoteCount = fixed.chars().filter(ch -> ch == '"').count();
        if (quoteCount % 2 != 0) {
            fixed += "\"";
        }

        return fixed;
    }

    // 본문 파싱
    private GeminiResponseDto parsePassageContent(String content) {
        try {
            String fixedContent = fixIncompleteJson(content);
            JsonNode node = objectMapper.readTree(fixedContent);
            String body = node.get("body").asText();

            if (body.length() > 600) {
                log.error("❌ 본문 길이 초과: {}자", body.length());
                throw new GeminiException("본문 길이 초과 (600자 이하로 작성 필요)");
            }

            return GeminiResponseDto.builder()
                    .title(node.get("title").asText())
                    .passageText(body)
                    .build();
        } catch (Exception e) {
            log.error("🚨 Passage JSON 파싱 실패. content:\n{}", content, e);
            throw new GeminiException("Passage JSON 파싱 실패");
        }
    }

    // 문제 파싱
    private GeminiResponseDto parseQuestionContent(String content) {
        try {
            String fixedContent = fixIncompleteJson(content);
            JsonNode root = objectMapper.readTree(fixedContent);
            JsonNode questionsArray = root.get("questions");
            if (questionsArray == null || !questionsArray.isArray() || questionsArray.isEmpty()) {
                throw new GeminiException("'questions' 배열이 없거나 비어 있음");
            }
            JsonNode firstQuestion = questionsArray.get(0);
            return GeminiResponseDto.builder()
                    .questionText(firstQuestion.get("question").asText(""))
                    .choices(objectMapper.convertValue(
                            firstQuestion.get("choices"),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
                    ))
                    .correctAnswer(firstQuestion.get("answer").asText(""))
                    .explanation(firstQuestion.get("explanation").asText(""))
                    .build();
        } catch (Exception e) {
            log.error("🚨 Question JSON 파싱 실패. content:\n{}", content, e);
            throw new GeminiException("Question JSON 파싱 실패");
        }
    }

    // 본문 생성 + 저장 재시도 3회
    public GeminiResponseDto generatePassage(GeminiRequestDto dto) {
        if (isNoPassageCategory(dto.getCategory())) {
            return GeminiResponseDto.builder()
                    .title(dto.getTitle() != null ? dto.getTitle() : "제목 없음")
                    .passageText(dto.getPassageText())
                    .build();
        }

        for (int attempt = 1; attempt <= 3; attempt++) {
            GeminiApiResponse response = callGeminiApi(buildPassagePrompt(dto));
            String json = response.getCandidates().get(0).getContent().getParts().get(0).getText();

            try {
                return parsePassageContent(json);
            } catch (GeminiException e) {
                log.warn("❗ 본문 길이 초과로 Gemini API 재요청 (시도 {}회차)", attempt);
                if (attempt == 3) {
                    throw new GeminiException("본문 길이 초과로 3회 시도 실패");
                }
            }
        }

        throw new GeminiException("지문 생성 실패");
    }

    // 문제 생성 + 저장 재시도 3회
    public MultipleChoice generateAndSaveQuestion(GeminiRequestDto dto, Passage passage) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            GeminiApiResponse response = callGeminiApi(buildQuestionPrompt(dto));
            String json = response.getCandidates().get(0).getContent().getParts().get(0).getText();

            try {
                GeminiResponseDto questionDto = parseQuestionContent(json);
                MultipleChoice multipleChoice = MultipleChoice.builder()
                        .passage(passage)
                        .question(questionDto.getQuestionText())
                        .build();

                IntStream.range(0, questionDto.getChoices().size())
                        .forEach(i -> {
                            Choice choice = Choice.builder()
                                    .choiceIndex(i + 1)
                                    .content(questionDto.getChoices().get(i))
                                    .isCorrect(questionDto.getChoices().get(i).equals(questionDto.getCorrectAnswer()))
                                    .build();
                            multipleChoice.addChoice(choice);
                        });

                return questionService.saveMultipleChoice(multipleChoice);

            } catch (GeminiException e) {
                log.warn("❗ 문제 JSON 파싱 실패로 Gemini API 재요청 (시도 {}회차)", attempt);
                if (attempt == 3) throw e;
            }
        }
        throw new GeminiException("문제 생성 3회 실패");
    }
    @Transactional
    public MultipleChoice generateFullProcess(GeminiRequestDto dto) {
        Passage passage = isNoPassageCategory(dto.getCategory())
                ? passageService.getPassageByPassageNo(dto.getPassageNo())
                : generateAndSavePassage(dto);

        dto.setPassageText(passage.getContent());
        return generateAndSaveQuestion(dto, passage);
    }

    public Passage generateAndSavePassageIfNeeded(GeminiRequestDto dto) {
        return isNoPassageCategory(dto.getCategory())
                ? passageService.getPassageByPassageNo(dto.getPassageNo())
                : generateAndSavePassage(dto);
    }
    
    public Passage getPassageById(Long passageNo) {
        return passageService.getPassageByPassageNo(passageNo);
    }

    
    public Passage generateAndSavePassage(GeminiRequestDto dto) {
        GeminiResponseDto passageDto = generatePassage(dto);
        dto.setPassageText(passageDto.getPassageText());
        return savePassageEntity(dto, passageDto);
    }

    private Passage savePassageEntity(GeminiRequestDto dto, GeminiResponseDto passageDto) {
        return passageService.savePassage(
                Passage.builder()
                        .title(passageDto.getTitle())
                        .content(passageDto.getPassageText())
                        .author("Gemini AI")
                        .publicationDate(LocalDate.now())
                        .category(categoryService.getCategoryByCategory(dto.getCategory().name()))
                        .type(typeService.getTypeByType(dto.getType().name()))
                        .level(levelService.getLevelByLevel(dto.getLevel()))
                        .language(languageService.getLangeageByLanguage(dto.getLanguage().name()))
                        .classification(classificationService.getClassificationByEnum(dto.getClassification().name()))
                        .build());
    }

    private boolean isNoPassageCategory(Category category) {
        return List.of(Category.NOVEL, Category.FAIRY_TALE).contains(category);
    }

    private String buildPassagePrompt(GeminiRequestDto dto) {
        String lang = getLangName(dto.getLanguage());
        String lvl = getLevelDetail(dto.getLevel());
        String tp = getTypePrompt(dto.getCategory(), dto.getType(), lang, lvl);

        return String.format("""
                다음 조건으로 JSON을 출력하세요.

                조건:
                - body는 600자(공백 포함) 이내로 작성하세요.
                - 초과 시 안 됩니다.

                형식:
                {
                  "title": "제목",
                  "body": "본문"
                }

                예시:
                {
                  "title": "환경 보호의 중요성",
                  "body": "환경 오염이 심각합니다. 우리는 일회용품을 줄이고, 대중교통을 이용하며, 재활용을 해야 합니다. 모두의 노력이 필요합니다."
                }

                지문 주제:
                %s
                """, tp);
    }

    private String buildQuestionPrompt(GeminiRequestDto dto) {
        String lang = getLangName(dto.getLanguage());
        String levelDetail = getLevelDetail(dto.getLevel());
        String category = dto.getCategory().name();
        String type = dto.getType().name();

        String passageText = dto.getPassageText();

        return String.format("""
            아래 지문을 읽고 JSON 형식으로만 **세 문제**를 생성하세요.

            ### 문제 유형
            - 주제 파악
            - 세부 내용 이해
            - 어휘 추론

            ⚠️ 반드시 아래 JSON 형식으로 응답하세요. 다른 텍스트, 주석, 안내문, 공백 포함 시 오류 발생합니다.

            예시 형식:
            {
              "questions": [
                {
                  "question": "문제 내용",
                  "choices": ["보기1", "보기2", "보기3", "보기4"],
                  "answer": "정답 보기",
                  "explanation": "정답 해설"
                }
              ]
            }

            조건:
            - 문제 수: 3개
            - 각 문제 보기 수: 4개 (고정)
            - 언어: %s
            - 레벨: %d (%s)
            - 카테고리: %s
            - 질문 유형: %s

            📄 지문:
            %s
            """, lang, dto.getLevel(), levelDetail, category, type, passageText);
    }

    private String getLangName(Language language) {
        return switch (language) {
            case KOREAN -> "한국어";
            case ENGLISH -> "영어";
            case JAPANESE -> "日本語";
        };
    }

    private String getLevelDetail(int level) {
        return switch (level) {
            case 1 -> "1 문단, 초등 저학년, 단순 사실";
            case 2 -> "1 문단, 초등 필수 어휘";
            case 3 -> "1‒2 문단, 내용 일치/불일치";
            case 4 -> "2 문단, 육하원칙";
            case 5 -> "2‒3 문단, 중심 생각";
            case 6 -> "3 문단, 전체 주제";
            case 7 -> "3‒4 문단, 문맥 추론";
            case 8 -> "4 문단, 관점/생략 추론";
            case 9 -> "4‒5 문단, 논리 평가";
            case 10 -> "5 문단 이상, 전문/비평";
            default -> "기본 수준";
        };
    }

    private String getTypePrompt(Category cat, Type type, String lang, String lvl) {
        return cat == Category.NEWS ? newsPrompt(type, lang, lvl) : genericPrompt(cat, type, lang, lvl);
    }

    private String newsPrompt(Type type, String lang, String lvl) {
        String topics = getTopicByTypeAndLevel(type, 5);
        return String.format("당신은 %s 기자입니다. 대한민국 %s 관련 기사입니다.\n난이도: %s\n주제: %s", type.name(), type.name(), lvl, topics);
    }

    private String genericPrompt(Category cat, Type type, String lang, String lvl) {
        return String.format("당신은 [%s/%s] 작가입니다. 지문을 작성하세요.\n난이도: %s", cat.name(), type.name(), lvl);
    }

    private String getTopicByTypeAndLevel(Type type, int level) {
        return switch (type) {
            case POLITICS -> switch (level) {
                case 1 -> "쉬운 정부 정책 이야기";
                case 2 -> "법안 뜻 알고 이해하기";
                case 3 -> "선거란 무엇인가요?";
                case 4 -> "청소년의 정치 경험";
                case 5 -> "최근 선거 결과 분석";
                case 6 -> "여야 정책 비교";
                case 7 -> "법안 통과의 쟁점";
                case 8 -> "정치와 경제의 연결";
                case 9 -> "국제정치가 우리에 미치는 영향";
                case 10 -> "정치적 이해관계 심층 분석";
                default -> "정치 관련 주제";
            };
            case ECONOMY -> switch (level) {
                case 1 -> "장난감으로 배우는 경제";
                case 2 -> "쉬운 경제 개념 설명";
                case 3 -> "기업과 소비자 관계 이해";
                case 4 -> "중학생의 경제생활";
                case 5 -> "최신 경제 지표 소개";
                case 6 -> "금융 정책 비교";
                case 7 -> "물가·환율 분석";
                case 8 -> "경제와 사회 영향 연결";
                case 9 -> "국제 경제 동향 분석";
                case 10 -> "경제 정책 심층비평";
                default -> "경제 관련 주제";
            };
            case IT_AND_SCIENCE -> switch (level) {
                case 1 -> "로봇 친구 이야기";
                case 2 -> "우주 여행의 꿈";
                case 3 -> "AI란 무엇?";
                case 4 -> "중학생을 위한 과학기술";
                case 5 -> "최신 우주기술 분석";
                case 6 -> "AI 활용 사례 비교";
                case 7 -> "과학의 사회적 영향";
                case 8 -> "AI 윤리와 미래";
                case 9 -> "첨단기술 국제 경쟁";
                case 10 -> "기술 정책 심층 분석";
                default -> "과학/기술 주제";
            };
            case SOCIETY -> switch (level) {
                case 1 -> "학교 규칙 이야기";
                case 2 -> "환경 보호의 중요성";
                case 3 -> "사회 질서 이해하기";
                case 4 -> "청소년 봉사 활동";
                case 5 -> "사회 문제 탐구";
                case 6 -> "지역 사회 변화 분석";
                case 7 -> "사회적 갈등의 원인";
                case 8 -> "환경과 사회의 상관관계";
                case 9 -> "현대 사회의 도전 과제";
                case 10 -> "사회적 불평등 심층 분석";
                default -> "사회 관련 주제";
            };
            case LIFE_AND_CULTURE -> switch (level) {
                case 1 -> "우리 동네 맛집 탐방";
                case 2 -> "한국의 명절 이야기";
                case 3 -> "세계의 전통 의상";
                case 4 -> "청소년 문화 활동";
                case 5 -> "최근 문화 트렌드";
                case 6 -> "세계 문화 비교";
                case 7 -> "생활 습관과 건강";
                case 8 -> "문화 산업의 사회적 역할";
                case 9 -> "문화 다양성과 사회";
                case 10 -> "문화 정책 심층 분석";
                default -> "문화/라이프스타일 주제";
            };
            case WORLD -> switch (level) {
                case 1 -> "세계 여러 나라 소개";
                case 2 -> "외국 친구들과의 교류";
                case 3 -> "세계 문화 탐방";
                case 4 -> "국제 환경 문제";
                case 5 -> "세계 경제 동향";
                case 6 -> "국제 협력 사례";
                case 7 -> "세계 갈등과 분쟁";
                case 8 -> "국제 기구의 역할";
                case 9 -> "글로벌 정치·경제 분석";
                case 10 -> "세계 질서와 국제 관계 심층 분석";
                default -> "국제 관련 주제";
            };
            case SPORTS -> switch (level) {
                case 1 -> "재미있는 운동 이야기";
                case 2 -> "올림픽 이야기";
                case 3 -> "유명한 스포츠 스타";
                case 4 -> "운동과 건강의 관계";
                case 5 -> "최근 스포츠 경기 결과";
                case 6 -> "스포츠 전략 비교";
                case 7 -> "스포츠와 미디어";
                case 8 -> "스포츠 산업과 경제";
                case 9 -> "국제 스포츠 경쟁";
                case 10 -> "스포츠 정책 심층 분석";
                default -> "스포츠 관련 주제";
            };
            case ENTERTAINMENT -> switch (level) {
                case 1 -> "즐거운 영화 이야기";
                case 2 -> "가수와 배우 이야기";
                case 3 -> "유명한 애니메이션";
                case 4 -> "청소년 인기 문화";
                case 5 -> "최근 대중문화 트렌드";
                case 6 -> "국내외 스타 비교";
                case 7 -> "대중문화의 사회적 영향";
                case 8 -> "연예 산업과 사회";
                case 9 -> "대중문화 글로벌 확산";
                case 10 -> "문화 산업 정책 분석";
                default -> "대중문화 관련 주제";
            };
            default -> "일반 주제";
        };
    }
}
