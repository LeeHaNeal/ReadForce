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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readforce.ai.ApiException;
import com.readforce.ai.dto.GeminiGenerateTestPassageAndQuestionResponseDto;
import com.readforce.ai.dto.GeminiGenerateTestPassageResponseDto;
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

			
		for(Level level : levelList) {
			
			String prompt = gernerateTestVocabularyPrompt(language, level);
			 
			String requestResult = requestGenerate(prompt);
			 
			GeminiGenerateTestPassageResponseDto parsedResult = parsingResponse(requestResult);
			 
			String author = NameEnum.GEMINI.name();
			 
			LocalDate publicationDate = LocalDate.now();
			 
			Category categoryEntity = categoryService.getCategoryByCategory(CategoryEnum.VOCABULARY);

			passageService.savePassage(parsedResult.getTitle(), parsedResult.getContent(), author, publicationDate, categoryEntity, level, language, classification);
			
		}
			
		
		
	}
	
	
//	private GeminiGenerateTestPassageResponseDto parsingResponse(String requestResult) {
//
//		try {
//			
//			String jsonContent = extractJsonFromResponse(requestResult);
//	        System.out.println("🔍 파싱 전 JSON 내용: " + jsonContent);
//
//			
//			GeminiGenerateTestPassageResponseDto parsedResponse = objectMapper.readValue(jsonContent, GeminiGenerateTestPassageResponseDto.class); 
//			
//			return parsedResponse;
//			
//		} catch(Exception exception) {
//	        System.err.println("❌ JSON 파싱 오류 발생: " + exception.getMessage());
//	        System.err.println("⚠️ 문제의 원본 응답 내용: " + requestResult);
//			throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);
//			
//		}
//
//	}
	private GeminiGenerateTestPassageResponseDto parsingResponse(String requestResult) {
	    try {
	        String jsonContent = extractJsonFromResponse(requestResult);

	        // 🔥 JSON 문자열이 한번 더 이스케이프된 경우 unescape 처리
	        jsonContent = objectMapper.readValue(jsonContent, String.class); // unescape

	        System.out.println("✅ 언이스케이프 후 파싱 대상 JSON: " + jsonContent);

	        return objectMapper.readValue(jsonContent, GeminiGenerateTestPassageResponseDto.class);

	    } catch (Exception exception) {
	        System.err.println("❌ JSON 파싱 오류: " + exception.getMessage());
	        System.err.println("⚠️ 문제 응답: " + requestResult);
	        throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);
	    }
	}

	private String extractJsonFromResponse(String requestResult) {

		int startIndex = requestResult.indexOf("{");
		int endIndex = requestResult.indexOf("}");
		
		if(startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
			
			return requestResult.substring(startIndex, endIndex +1);
			
		}
		
		return "{}";
	}
	
	private String requestGenerate(String prompt) {

		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		Map<String, Object> part = Map.of("text", prompt);
		Map<String, Object> content = Map.of("parts", List.of(part));
		Map<String, Object> body = Map.of("contents", List.of(content));
		
		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, httpHeaders);
		
		String url = geminiApiUrl + "?key=" + geminiApiKey;
		
		try {
			
			String response = restTemplate.postForObject(url, requestEntity, String.class);
			
			return response;
					
		} catch(Exception exception){
			
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
				
				String prompt = gernerateTestVocabularyQuestionPrompt(language, passage.getLevel(), passage.getTitle(), passage.getContent());

				String requestResult = requestGenerate(prompt);
				
				GeminiGenerateTestPassageAndQuestionResponseDto parsedResult = parsePassageAndQuestionResponse(requestResult);
				
				saveMultipleChoiceQuestion(passage, parsedResult);

			} else {
				
				for(Level level : levelService.getAllLevelList()) {
					
					String prompt = (testCategory == CategoryEnum.FACTUAL)
							? generateTestFactualPassageAndQuestionPrompt(language, level)
							: generateTestInferentialPassageAndQuestionPrompt(language, level);
					
					String requestResult = requestGenerate(prompt);
					
					GeminiGenerateTestPassageAndQuestionResponseDto parsedResult = parsePassageAndQuestionResponse(requestResult);
					
					Category categoryEntity = categoryService.getCategoryByCategory(testCategory);
					
					Passage newPassage = passageService.savePassage(parsedResult.getTitle(), parsedResult.getContent(), NameEnum.GEMINI.name(), LocalDate.now(), categoryEntity, level, language, classification);
							
					saveMultipleChoiceQuestion(newPassage, parsedResult);

				}

			}

		}
		
	}
	
	private GeminiGenerateTestPassageAndQuestionResponseDto parsePassageAndQuestionResponse(String requestResult) {

		try {
			
			String jsonContent = extractJsonFromResponse(requestResult);
			
			return objectMapper.readValue(jsonContent, GeminiGenerateTestPassageAndQuestionResponseDto.class);
			
		} catch(Exception exception) {
			
			throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);
			
		}
		
	}


	private void saveMultipleChoiceQuestion(Passage passage, GeminiGenerateTestPassageAndQuestionResponseDto parsedResult) {
		
		List<Choice> choiceList = new ArrayList<>();
		
		for(int i = 0 ; i < parsedResult.getChoiceList().size() ; i++) {
			
			Choice choice = Choice.builder()
					.choiceIndex(i)
					.content(parsedResult.getChoiceList().get(i))
					.isCorrect(i == parsedResult.getCorrectAnswerIndex())
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
		
		switch(language.getLanguageName()) {
		
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
						  "correctAnswerIndex": 정답_선택지의_인덱스 (0-3)
						}
				""", level.getLevelNumber(), level.getVocabularyLevel(), title, title, content);
				
			default:
		
		}
		
		return prompt;
		
	}
	
	private String generateTestFactualPassageAndQuestionPrompt(Language language, Level level) {
		
		String prompt = "";
		
		switch(language.getLanguageName()) {
		
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
		                  "correctAnswerIndex": 정답_선택지의_인덱스 (0-3)
		                }
				""", level.getLevelNumber(), level.getVocabularyLevel());
				break;

			default:
				
		}
		
		return prompt;
		
	}
	
	private String generateTestInferentialPassageAndQuestionPrompt(Language language, Level level) {
		
		String prompt = "";
		
		switch(language.getLanguageName()) {
		
			case KOREAN:
				prompt = String.format(
			            """
			                당신은 한국어 '추론적 이해' 문제 출제 전문가입니다.
			                '난이도 %d (%s)' 수준의 짧은 글을 생성하고, 글의 내용을 바탕으로 추론할 수 있는 내용을 찾는 객관식 문제를 JSON 형식으로 생성해 주세요.
			                정답 1개와 매력적인 오답 3개를 포함하고, 정답의 위치는 무작위로 설정해 주세요.

			                요청 형식:
			                {
			                  "title": "여기에 지문 제목을 생성해주세요.",
			                  "content": "여기에 지문 내용을 생성해주세요.",
			                  "question": "생성된 질문",
			                  "choiceList": ["선택지 1", "선택지 2", "선택지 3", "선택지 4"],
			                  "correctAnswerIndex": 정답_선택지의_인덱스 (0-3)
			                }
			    """, level.getLevelNumber(), level.getVocabularyLevel());
				break;
				
			default:
		
		}
		
		return prompt;
		
	}


	




	
	
}
