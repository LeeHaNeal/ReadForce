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
   
	@Transactional
	public void generateTestVocabulary(LanguageEnum languageEnum) {
      
		Language language = languageService.getLangeageByLanguage(languageEnum);
      
		List<Level> levelList = levelService.getAllLevelList();
      
		Classification classification = classificationService.getClassificationByClassfication(ClassificationEnum.TEST);

         
		for(Level level : levelList) {
			
			String prompt = promptService.gernerateTestVocabularyPrompt(language, level);

			Map<String, Object> requestResult = requestGenerate(prompt);

			String content = extractContentFromResponse(requestResult);
			
			List<GeminiGenerateTestPassageResponseDto> parsedResultList = parsingResponse(content);

			for(GeminiGenerateTestPassageResponseDto parsedResult : parsedResultList) {

				String author = NameEnum.GEMINI.name();

				LocalDate publicationDate = LocalDate.now();
	             
	            Category categoryEntity = categoryService.getCategoryByCategory(CategoryEnum.VOCABULARY);

	            passageService.savePassage(parsedResult.getTitle(), parsedResult.getContent(), author, publicationDate, categoryEntity, level, language, classification, null);

			}

			try {

				Thread.sleep(1000);

			} catch(InterruptedException exception){

				Thread.currentThread().interrupt();

			}

		}
      
   }
   
   
	private List<GeminiGenerateTestPassageResponseDto> parsingResponse(String requestResult) {

		try {

			JsonNode rootNode = objectMapper.readTree(requestResult);

			if(rootNode.isArray()) {

				return objectMapper.readValue(requestResult, new TypeReference<List<GeminiGenerateTestPassageResponseDto>>() {});

			} else {

				GeminiGenerateTestPassageResponseDto singleDto = objectMapper.readValue(requestResult, GeminiGenerateTestPassageResponseDto.class);

				return Collections.singletonList(singleDto);

			}

		} catch(Exception exception) {

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
	    	
	        log.error("Gemini API 호출 실패 url={}, prompt={}, message={}", url, prompt, exception.getMessage(), exception);
	        
	        throw new ApiException(MessageCode.GEMINI_API_REQUEST_FAIL);
	        
	    }

	}



   

	@Transactional
	public void generateTestQuestion(LanguageEnum languageEnum) {

		Language language = languageService.getLangeageByLanguage(languageEnum);

		Classification classification = classificationService.getClassificationByClassfication(ClassificationEnum.TEST);

		List<CategoryEnum> testCategoryList = new ArrayList<CategoryEnum>();

		testCategoryList.add(CategoryEnum.VOCABULARY);

		testCategoryList.add(CategoryEnum.FACTUAL);

		testCategoryList.add(CategoryEnum.INFERENTIAL);

		for(CategoryEnum testCategory : testCategoryList) {

			if(testCategory == CategoryEnum.VOCABULARY) {

				List<PassageResponseDto> unusedPassageList = questionService.getUnusedVocabularyPassageList(languageEnum, ClassificationEnum.TEST);

				if(unusedPassageList.isEmpty()) {

					continue;

				}

				PassageResponseDto randomPassagDto = unusedPassageList.get(new Random().nextInt(unusedPassageList.size()));

				Passage passage = passageService.getPassageByPassageNo(randomPassagDto.getPassageNo());

				String prompt = promptService.gernerateTestVocabularyQuestionPrompt(language, passage.getLevel(), passage.getTitle(), passage.getContent());

				Map<String, Object> requestResult = requestGenerate(prompt);

				String content = extractContentFromResponse(requestResult);

				List<GeminiGenerateTestPassageAndQuestionResponseDto> parsedResultList = parsePassageAndQuestionResponse(content);

				for(GeminiGenerateTestPassageAndQuestionResponseDto parsedResult : parsedResultList) {

					saveMultipleChoiceQuestion(passage, parsedResult);

				}

			} else {

				for(Level level : levelService.getAllLevelList()) {

					String prompt = (testCategory == CategoryEnum.FACTUAL)
							? promptService.generateTestFactualPassageAndQuestionPrompt(language, level)
									: promptService.generateTestInferentialPassageAndQuestionPrompt(language, level);

					Map<String, Object> requestResult = requestGenerate(prompt);

					String content = extractContentFromResponse(requestResult);

					List<GeminiGenerateTestPassageAndQuestionResponseDto> parsedResultList = parsePassageAndQuestionResponse(content);

					for(GeminiGenerateTestPassageAndQuestionResponseDto parsedResult : parsedResultList) {

						Category categoryEntity = categoryService.getCategoryByCategory(testCategory);

						Passage newPassage = passageService.savePassage(parsedResult.getTitle(), parsedResult.getContent(), NameEnum.GEMINI.name(), LocalDate.now(), categoryEntity, level, language, classification, null);

						saveMultipleChoiceQuestion(newPassage, parsedResult);

					}

					try {

						Thread.sleep(1000);

					} catch(InterruptedException exception){

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

				GeminiGenerateTestPassageAndQuestionResponseDto singleDto = objectMapper.readValue(requestResult, GeminiGenerateTestPassageAndQuestionResponseDto.class);

				return Collections.singletonList(singleDto);

			}

		} catch(Exception exception) {

			return Collections.emptyList();

		}

	}

	private void saveMultipleChoiceQuestion(Passage passage, GeminiGenerateTestPassageAndQuestionResponseDto parsedResult) {

		List<Choice> choiceList = new ArrayList<>();

		Map<String, String> explanationMap = parsedResult.getExplanation();

		for(int i = 0 ; i < parsedResult.getChoiceList().size() ; i++) {

			boolean isCorrect = (i == Integer.parseInt(parsedResult.getCorrectAnswerIndex()));

			String explanationText = isCorrect
					? explanationMap.getOrDefault("correct", "정답에 대한 설명이 없습니다.")
							: explanationMap.getOrDefault("incorrect", "오답에 대한 설명이 없습니다.");

			Choice choice = Choice.builder()
					.choiceIndex(i)
					.content(parsedResult.getChoiceList().get(i))
					.isCorrect(i == Integer.parseInt(parsedResult.getCorrectAnswerIndex()))
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

		for(int i = 0 ; i < parsedResult.getChoiceList().size() ; i++) {

			boolean isCorrect = (i == Integer.parseInt(parsedResult.getCorrectAnswerIndex()));

			String explanationText = isCorrect
					? explanationMap.getOrDefault("correct", "정답에 대한 설명이 없습니다.")
							: explanationMap.getOrDefault("incorrect", "오답에 대한 설명이 없습니다.");
         
			Choice choice = Choice.builder()
					.choiceIndex(i)
					.content(parsedResult.getChoiceList().get(i))
					.isCorrect(i == Integer.parseInt(parsedResult.getCorrectAnswerIndex()))
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
	public void generatePassage(AiGeneratePassageRequestDto requestDto) {

		Level level = levelService.getLevelByLevel(requestDto.getLevel());
       
		Language language = languageService.getLangeageByLanguage(requestDto.getLanguage());
       
		Classification classification = classificationService.getClassificationByClassfication(requestDto.getClassification());
       
		Category category = categoryService.getCategoryByCategory(requestDto.getCategory());
       
		Type type = typeService.getTypeByType(requestDto.getType());

		int count = (requestDto.getCount() != null) ? requestDto.getCount() : 1;

		for (int i = 0; i < count; i++) {

			String prompt = promptService.generatePassagePrompt(requestDto);
           
			Map<String, Object> requestResult = requestGenerate(prompt);
           
			String content = extractContentFromResponse(requestResult);
           
			GeminiGeneratePassageResponseDto parsedResult = parsePassageResponse(content);

			passageService.savePassage(
					parsedResult.getTitle(),
                    parsedResult.getContent(),
                    NameEnum.GEMINI.name(),
                    LocalDate.now(),
                    category,
                    level,
                    language,
                    classification,
                    type
			);

			try {
				
				Thread.sleep(3000);

			} catch (InterruptedException exception) {
				
				Thread.currentThread().interrupt();

			}

		}
   
	}

	private GeminiGeneratePassageResponseDto parsePassageResponse(String requestResult) {

		try {

			JsonNode rootNode = objectMapper.readTree(requestResult);

			if(rootNode.isArray()) {

				List<GeminiGeneratePassageResponseDto> list = objectMapper.readValue(requestResult, new TypeReference<List<GeminiGeneratePassageResponseDto>>() {});

				if(!list.isEmpty()) {

					return list.get(0);

				}

				throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);

			} else {

				return objectMapper.readValue(requestResult, GeminiGeneratePassageResponseDto.class);

			}

		} catch(Exception exception) {

			throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);

		}

	}

	public void generateQuestion() {

		List<Passage> noQuestionPassageList = passageService.getNoQuestionPassage();

		for(Passage passage : noQuestionPassageList) {

			String prompt = promptService.generateQuestionPrompt(passage);

			Map<String, Object> requestResult = requestGenerate(prompt);
	         
			String content = extractContentFromResponse(requestResult);

			List<GeminiGenerateQuestionResponseDto> parsedResultList = parseQuestionResponse(content);

			for(GeminiGenerateQuestionResponseDto parsedResult : parsedResultList) {

				saveMultipleChoiceQuestion(passage, parsedResult);

			}         

		}

	}

	private List<GeminiGenerateQuestionResponseDto> parseQuestionResponse(String requestResult) {

		try {

			JsonNode rootNode = objectMapper.readTree(requestResult);

			if(rootNode.isArray()) {

				return objectMapper.readValue(requestResult, new TypeReference<List<GeminiGenerateQuestionResponseDto>>() {});

			} else {

				GeminiGenerateQuestionResponseDto singleDto = objectMapper.readValue(requestResult, GeminiGenerateQuestionResponseDto.class);

				return Collections.singletonList(singleDto);

			}

		} catch(Exception exception) {

			throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);

		}

	}

	private String extractContentFromResponse(Map<String, Object> response) {

		if(response != null && response.containsKey("candidates")) {

			List<Map<String, Object>> candidates = (List<Map<String, Object>>)response.get("candidates");

			if(candidates != null && !candidates.isEmpty()) {

				Map<String, Object> firstCandidate = candidates.get(0);

				if(firstCandidate.containsKey("content")) {

					Map<String, Object> content = (Map<String, Object>)firstCandidate.get("content");

					if(content.containsKey("parts")) {

						List<Map<String, Object>> parts = (List<Map<String, Object>>)content.get("parts");

						if(parts != null && !parts.isEmpty()) {

							Map<String, Object> firstPart = parts.get(0);

							if(firstPart.containsKey("text")) {

								String text = (String)firstPart.get("text");

								int firstBracket = text.indexOf('{');
								
								int firstSquareBracket = text.indexOf('[');

								if(firstBracket == -1 && firstSquareBracket == -1) {

									return "{}";

								}

								int startIndex = -1;

								if(firstBracket != -1 && firstSquareBracket != -1) {

									startIndex = Math.min(firstBracket, firstSquareBracket);

								} else if(firstBracket != -1) {

									startIndex = firstBracket;

								} else {

									startIndex = firstSquareBracket;

								}

								int lastBracket = text.lastIndexOf('}');
                        
								int lastSquareBracket = text.lastIndexOf(']');
								
								int endIndex = Math.max(lastBracket, lastSquareBracket);

								if(endIndex == -1) {

									return "{}";

								}
	                        
								return text.substring(startIndex, endIndex + 1);

							}

						}

					}

				}

			}

		}

		return "{}";

	}
  
}