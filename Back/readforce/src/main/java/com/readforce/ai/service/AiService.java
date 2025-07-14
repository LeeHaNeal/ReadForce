package com.readforce.ai.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readforce.ai.dto.AiGeneratePassageRequestDto;
import com.readforce.ai.dto.GeminiGeneratePassageResponseDto;
import com.readforce.ai.dto.GeminiGenerateQuestionResponseDto;
import com.readforce.ai.dto.GeminiGenerateTestPassageAndQuestionResponseDto;
import com.readforce.ai.dto.GeminiGenerateTestPassageResponseDto;
import com.readforce.ai.exception.ApiException;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.ClassificationEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.enums.NameEnum;
import com.readforce.common.exception.JsonException;
import com.readforce.passage.dto.PassageResponseDto;
import com.readforce.passage.entity.Category;
import com.readforce.passage.entity.Classification;
import com.readforce.passage.entity.Language;
import com.readforce.passage.entity.Level;
import com.readforce.passage.entity.Passage;
import com.readforce.passage.entity.Type;
import com.readforce.passage.service.CategoryService;
import com.readforce.passage.service.ClassificationService;
import com.readforce.passage.service.LanguageService;
import com.readforce.passage.service.LevelService;
import com.readforce.passage.service.PassageService;
import com.readforce.passage.service.TypeService;
import com.readforce.question.entity.Choice;
import com.readforce.question.entity.MultipleChoice;
import com.readforce.question.service.MultipleChoiceService;
import com.readforce.question.service.QuestionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    private final TypeService typeService;
    private final PromptService promptService;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private String requestGenerate(String prompt) {
        String url = geminiApiUrl + "?key=" + geminiApiKey;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        List<Map<String, String>> safetySettings = List.of(
            Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_NONE"),
            Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_NONE"),
            Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_NONE"),
            Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_NONE")
        );

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
            "safetySettings", safetySettings
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, httpHeaders);

        try {
        	
            Map<String, Object> response = restTemplate.postForObject(url, requestEntity, Map.class);
            
            return extractContentFromResponse(response);
            
        } catch (HttpClientErrorException e) {
        	
            log.error("Gemini API 호출 실패: Status Code = {}, Response Body = {}", e.getStatusCode(), e.getResponseBodyAsString());
            
            log.error("Request Body: {}", requestBody);
            
            throw new ApiException(MessageCode.GEMINI_API_REQUEST_FAIL);
            
        } catch (Exception exception) {
        	
            log.error("Gemini API 호출 중 알 수 없는 오류 발생", exception);
            
            throw new ApiException(MessageCode.GEMINI_API_REQUEST_FAIL);
            
        }
    }

    private String extractContentFromResponse(Map<String, Object> response) {
    	
        try {
        	
            JsonNode rootNode = objectMapper.valueToTree(response);
            
            JsonNode textNode = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text");
            
            if (textNode.isMissingNode()) {
            	
                log.warn("응답에서 'text' 필드를 찾을 수 없습니다. 응답: {}", response);
                
                return "{}";
                
            }
            
            return cleanJsonString(textNode.asText());
            
        } catch (Exception exception) {
        	
            log.error("Gemini API 응답 파싱 중 오류 발생", exception);
            
            return "{}";
            
        }
        
    }
    
    private String cleanJsonString(String rawText) {
    	
        if (rawText == null) return "{}";
        
        String cleanedText = rawText.replaceAll("```json", "").replaceAll("```", "").trim();
        
        int firstBracket = cleanedText.indexOf('{');
        
        int firstSquareBracket = cleanedText.indexOf('[');

        if (firstBracket == -1 && firstSquareBracket == -1) return "{}";

        int startIndex = (firstBracket != -1 && firstSquareBracket != -1) ? Math.min(firstBracket, firstSquareBracket) : Math.max(firstBracket, firstSquareBracket);
       
        char startChar = cleanedText.charAt(startIndex);
        
        char endChar = (startChar == '{') ? '}' : ']';
        
        int lastIndex = cleanedText.lastIndexOf(endChar);

        if (lastIndex > startIndex) {

        	return cleanedText.substring(startIndex, lastIndex + 1);
        
        }
        
        return "{}";
        
    }

    @Transactional
    public void generateTestVocabulary(LanguageEnum languageEnum) {
        
    	Language language = languageService.getLangeageByLanguage(languageEnum);
        
    	List<Level> levelList = levelService.getAllLevelList();
        
    	Classification classification = classificationService.getClassificationByClassfication(ClassificationEnum.TEST);

        for (Level level : levelList) {
            
        	String prompt = promptService.gernerateTestVocabularyPrompt(language, level);
           
            try {
               
            	String responseText = requestGenerate(prompt);

            	String content = cleanJsonString(responseText);
                
            	List<GeminiGenerateTestPassageResponseDto> parsedResultList = parseTestPassageResponse(content);

                for (GeminiGenerateTestPassageResponseDto parsedResult : parsedResultList) {
                    
                	passageService.savePassage(parsedResult.getTitle(), parsedResult.getContent(), NameEnum.GEMINI.name(), LocalDate.now(), categoryService.getCategoryByCategory(CategoryEnum.VOCABULARY), level, language, classification, null);
                
                }
                
                Thread.sleep(2000);
                
            } catch (InterruptedException exception) {
            	
                Thread.currentThread().interrupt();
                
                log.error("Thread sleep interrupted", exception);
                
            } catch (ApiException exception) {
            	
                log.error("Failed to call Gemini API for level {}: {}", level.getLevelNumber(), exception.getMessage());
                
            }
            
        }
        
    }
    
    private List<GeminiGenerateTestPassageResponseDto> parseTestPassageResponse(String requestResult) {
    	
        if (requestResult == null || requestResult.trim().isEmpty() || "{}".equals(requestResult.trim())) {
        	
            return Collections.emptyList();
            
        }
        
        try {
        	
            JsonNode rootNode = objectMapper.readTree(requestResult);
            
            if (rootNode.isArray()) {
            	
                return objectMapper.readValue(requestResult, new TypeReference<List<GeminiGenerateTestPassageResponseDto>>() {});
           
            } else {
            	
                return Collections.singletonList(objectMapper.readValue(requestResult, GeminiGenerateTestPassageResponseDto.class));
           
            }
            
        } catch (Exception exception) {
        	
            throw new JsonException(MessageCode.JSON_PROCESSING_FAIL + " 내용: " + requestResult);
            
        }
        
    }

    @Transactional
    public void generatePassage(AiGeneratePassageRequestDto requestDto) {
    	
        Level level = levelService.getLevelByLevel(requestDto.getLevel());
        
        Language language = languageService.getLangeageByLanguage(requestDto.getLanguage());
        
        Classification classification = classificationService.getClassificationByClassfication(requestDto.getClassification());
        
        Category category = categoryService.getCategoryByCategory(requestDto.getCategory());
        Type type = typeService.getTypeByType(requestDto.getType());
        int count = (requestDto.getCount() != null) ? requestDto.getCount() : 1;

        for (int i = 0; i < count; i++) {
            String prompt = promptService.generatePassagePrompt(requestDto);
            String responseText = requestGenerate(prompt);
            String content = cleanJsonString(responseText);
            
            GeminiGeneratePassageResponseDto parsedResult = parsePassageResponse(content);

            if (parsedResult != null) {
                passageService.savePassage(parsedResult.getTitle(), parsedResult.getContent(), NameEnum.GEMINI.name(), LocalDate.now(), category, level, language, classification, type);
            }
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private GeminiGeneratePassageResponseDto parsePassageResponse(String requestResult) {
        if (requestResult == null || requestResult.trim().isEmpty() || "{}".equals(requestResult.trim())) {
            return null;
        }
        try {
            JsonNode rootNode = objectMapper.readTree(requestResult);
            if (rootNode.isArray() && rootNode.size() > 0) {
                return objectMapper.treeToValue(rootNode.get(0), GeminiGeneratePassageResponseDto.class);
            } else if (rootNode.isObject()){
                return objectMapper.treeToValue(rootNode, GeminiGeneratePassageResponseDto.class);
            }
            return null;
        } catch (Exception exception) {
            throw new JsonException(MessageCode.JSON_PROCESSING_FAIL + " 내용: " + requestResult);
        }
    }

    @Transactional
    public void generateQuestion() {
        List<Passage> noQuestionPassageList = passageService.getNoQuestionPassage();
        for (Passage passage : noQuestionPassageList) {
            String prompt = promptService.generateQuestionPrompt(passage);
            String responseText = requestGenerate(prompt);
            String content = cleanJsonString(responseText);
            List<GeminiGenerateQuestionResponseDto> parsedResultList = parseQuestionResponse(content);
            for (GeminiGenerateQuestionResponseDto parsedResult : parsedResultList) {
                saveMultipleChoiceQuestion(passage, parsedResult);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread sleep interrupted", e);
            }
        }
    }

    private List<GeminiGenerateQuestionResponseDto> parseQuestionResponse(String requestResult) {
        if (requestResult == null || requestResult.trim().isEmpty() || "{}".equals(requestResult.trim())) {
            return Collections.emptyList();
        }
        try {
            JsonNode rootNode = objectMapper.readTree(requestResult);
            if (rootNode.isArray()) {
                return objectMapper.readValue(requestResult, new TypeReference<List<GeminiGenerateQuestionResponseDto>>() {});
            } else {
                return Collections.singletonList(objectMapper.readValue(requestResult, GeminiGenerateQuestionResponseDto.class));
            }
        } catch (Exception exception) {
            throw new JsonException(MessageCode.JSON_PROCESSING_FAIL + " 내용: " + requestResult);
        }
    }

    @Transactional
    public void generateTestQuestion(LanguageEnum languageEnum) {
        Language language = languageService.getLangeageByLanguage(languageEnum);
        Classification classification = classificationService.getClassificationByClassfication(ClassificationEnum.TEST);
        List<CategoryEnum> testCategoryList = List.of(CategoryEnum.VOCABULARY, CategoryEnum.FACTUAL, CategoryEnum.INFERENTIAL);

        for (CategoryEnum testCategory : testCategoryList) {
            if (testCategory == CategoryEnum.VOCABULARY) {
                List<PassageResponseDto> unusedPassageList = questionService.getUnusedVocabularyPassageList(languageEnum, ClassificationEnum.TEST);
                if (unusedPassageList.isEmpty()) continue;
                
                PassageResponseDto randomPassageDto = unusedPassageList.get(new Random().nextInt(unusedPassageList.size()));
                Passage passage = passageService.getPassageByPassageNo(randomPassageDto.getPassageNo());
                
                String prompt = promptService.gernerateTestVocabularyQuestionPrompt(language, passage.getLevel(), passage.getTitle(), passage.getContent());
                String responseText = requestGenerate(prompt);
                String content = cleanJsonString(responseText);
                List<GeminiGenerateTestPassageAndQuestionResponseDto> parsedResultList = parsePassageAndQuestionResponse(content);
                
                for (GeminiGenerateTestPassageAndQuestionResponseDto parsedResult : parsedResultList) {
                    saveMultipleChoiceQuestion(passage, parsedResult);
                }
            } else {
                for (Level level : levelService.getAllLevelList()) {
                    String prompt = (testCategory == CategoryEnum.FACTUAL)
                            ? promptService.generateTestFactualPassageAndQuestionPrompt(language, level)
                            : promptService.generateTestInferentialPassageAndQuestionPrompt(language, level);
                    
                    String responseText = requestGenerate(prompt);
                    String content = cleanJsonString(responseText);
                    List<GeminiGenerateTestPassageAndQuestionResponseDto> parsedResultList = parsePassageAndQuestionResponse(content);
                    
                    for (GeminiGenerateTestPassageAndQuestionResponseDto parsedResult : parsedResultList) {
                        Category categoryEntity = categoryService.getCategoryByCategory(testCategory);
                        Passage newPassage = passageService.savePassage(parsedResult.getTitle(), parsedResult.getContent(), NameEnum.GEMINI.name(), LocalDate.now(), categoryEntity, level, language, classification, null);
                        saveMultipleChoiceQuestion(newPassage, parsedResult);
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    private List<GeminiGenerateTestPassageAndQuestionResponseDto> parsePassageAndQuestionResponse(String requestResult) {
		try {
			JsonNode rootNode = objectMapper.readTree(requestResult);
			if(rootNode.isArray()) {
				return objectMapper.readValue(requestResult, new TypeReference<List<GeminiGenerateTestPassageAndQuestionResponseDto>>() {});
			} else {
				return Collections.singletonList(objectMapper.readValue(requestResult, GeminiGenerateTestPassageAndQuestionResponseDto.class));
			}
		} catch(Exception exception) {
			return Collections.emptyList();
		}
	}
    
    private void saveMultipleChoiceQuestion(Passage passage, GeminiGenerateTestPassageAndQuestionResponseDto parsedResult) {
		List<Choice> choiceList = new ArrayList<>();
		Map<String, String> explanationMap = parsedResult.getExplanation();

		for(int i = 0; i < parsedResult.getChoiceList().size(); i++) {
			boolean isCorrect = (i == Integer.parseInt(parsedResult.getCorrectAnswerIndex()));
			String explanationText = isCorrect ? explanationMap.getOrDefault("correct", "정답에 대한 설명이 없습니다.") : explanationMap.getOrDefault("incorrect", "오답에 대한 설명이 없습니다.");
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
		Map<String, Object> explanationMap = parsedResult.getExplanation();

		for(int i = 0; i < parsedResult.getChoiceList().size(); i++) {
			boolean isCorrect = (i == Integer.parseInt(parsedResult.getCorrectAnswerIndex()));
			
            String explanationText;
            if (isCorrect) {
                explanationText = explanationMap.getOrDefault("correct", "정답에 대한 설명이 없습니다.").toString();
            } else {
                explanationText = explanationMap.getOrDefault("incorrect", "오답에 대한 설명이 없습니다.").toString();
            }

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

    @Transactional
	public void generateQuestionByPassageNo(Long passageNo) {

    	Passage passage = passageService.getPassageByPassageNo(passageNo);
    	
    	String prompt = promptService.generateQuestionPrompt(passage);
    	
    	String responseText = requestGenerate(prompt);
    	
    	String content = cleanJsonString(responseText);
    	
    	List<GeminiGenerateQuestionResponseDto> parsedResultList = parseQuestionResponse(content);

    	for(GeminiGenerateQuestionResponseDto parsedResult : parsedResultList) {
    		
    		saveMultipleChoiceQuestion(passage, parsedResult);
    		
    	}
    	
	}
    
}