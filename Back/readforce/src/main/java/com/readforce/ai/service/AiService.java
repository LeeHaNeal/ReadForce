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
                         '난이도 %d (%s)' 수준의 단어 데이터를 생성해야 합니다.
   
                         ## 중요 규칙
                         - 반드시 **하나의 JSON 객체** 형식으로만 응답해야 합니다.
                         - 절대로 JSON 배열(리스트) 형식인 `[ ]` 로 감싸서 반환하면 안 됩니다.
   
                         ## 올바른 응답 형식 (JSON 객체):
                         {
                           "title": "단어",
                           "content": "단어의 뜻",
                           "level": "난이도 1 (초등 저학년)"
                         }
   
                         ## 잘못된 응답 형식 (JSON 배열):
                         [
                           {
                             "title": "단어",
                             "content": "단어의 뜻",
                             "level": "난이도 1 (초등 저학년)"
                           }
                         ]
   
                         이제 규칙에 맞춰 '난이도 %d (%s)' 수준의 단어를 생성해 주세요.
                      """, level.getLevelNumber(), level.getVocabularyLevel(), level.getLevelNumber(), level.getVocabularyLevel());
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

            Map<String, Object> requestResult = requestGenerate(prompt);
            
            String content = extractContentFromResponse(requestResult);
            
            List<GeminiGenerateTestPassageAndQuestionResponseDto> parsedResultList = parsePassageAndQuestionResponse(content);
            
            for(GeminiGenerateTestPassageAndQuestionResponseDto parsedResult : parsedResultList) {
               
               saveMultipleChoiceQuestion(passage, parsedResult);
               
            }

         } else {
            
            for(Level level : levelService.getAllLevelList()) {
               
               String prompt = (testCategory == CategoryEnum.FACTUAL)
                     ? generateTestFactualPassageAndQuestionPrompt(language, level)
                     : generateTestInferentialPassageAndQuestionPrompt(language, level);
               
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
                        "correctAnswerIndex": 정답_선택지의_인덱스 (0-3),
                        "explanation": {
                             "correct": "정답 해설...",
                           "incorrect": "오답 해설..."
                          }
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
   public void generatePassage(AiGeneratePassageRequestDto requestDto) {

       Level level = levelService.getLevelByLevel(requestDto.getLevel());
       Language language = languageService.getLangeageByLanguage(requestDto.getLanguage());
       Classification classification = classificationService.getClassificationByClassfication(requestDto.getClassification());
       Category category = categoryService.getCategoryByCategory(requestDto.getCategory());
       Type type = typeService.getTypeByType(requestDto.getType());

       int count = (requestDto.getCount() != null) ? requestDto.getCount() : 1;

       for (int i = 0; i < count; i++) {

           String prompt = generatePassagePrompt(requestDto);
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
               Thread.sleep(3000);  // 3초 간격
           } catch (InterruptedException e) {
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


   private String generatePassagePrompt(AiGeneratePassageRequestDto aiGeneratePassageRequestDto) {

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

	    String prompt = "";

	    switch (aiGeneratePassageRequestDto.getCategory()) {

	        case NEWS:
	            switch (aiGeneratePassageRequestDto.getLanguage()) {

	                case KOREAN:
	                    prompt = String.format("""
	                            당신은 %s 분야를 다루는 전문 기자입니다. 이번 기사 주제는 '%s'입니다.
	                            지금부터 다음 조건에 맞춰 뉴스 기사를 작성해 주세요.

	                            1. **언어:** %s
	                            2. **카테고리:** %s
	                            3. **유형:** %s
	                            4. **난이도:** 전체 10의 난이도 중 %d
	                               - **어휘:** %s 수준
	                               - **문장 구조:** %s
	                               - **내용:** '%s' 주제에 맞는 실제 사례나 최근 이슈를 포함해 주세요.
	                               - **분량:** 제목 포함 %d개 문단으로 구성해 주세요.

	                            5. **출력 형식:** 아래 JSON 형식으로만 응답해 주세요. 다른 설명은 포함하지 마세요.

	                            {
	                              "title": "기사 제목",
	                              "content": "기사 본문 내용. 문단 구분을 위해 '\\n\\n'를 사용하세요."
	                            }
	                            """,
	                            typeString, // 기자 분야
	                            typeString, // 주제 명시
	                            languageString,
	                            categoryString,
	                            typeString,
	                            level.getLevelNumber(),
	                            level.getVocabularyLevel(),
	                            level.getSentenceStructure(),
	                            typeString, // 내용에 주제 명시
	                            level.getParagraphCount()
	                    );
	                    break;

	                default:
	                    prompt = "지원하지 않는 언어입니다.";
	                    break;
	            }
	            break;

	        // [추가 예정] 소설이나 동화는 이곳에 별도 프롬프트 작성
	        case NOVEL:
	            prompt = "소설 카테고리는 AI가 직접 생성하지 않습니다.";
	            break;

	        case FAIRY_TALE:
	            prompt = "동화 카테고리는 AI가 직접 생성하지 않습니다.";
	            break;

	        default:
	            prompt = "지원하지 않는 카테고리입니다.";
	            break;
	    }

	    return prompt;
	}



   private String getCategoryString(LanguageEnum language, CategoryEnum category) {

      switch(language) {
      
         case KOREAN:
            
            switch(category) {
            
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

      switch(language) {
      
         case KOREAN:
            return "한국어";
      
         case ENGLISH:
            return "Englisgh";
            
         case JAPANESE:
            return "日本語";
            
         default:
            
      }
      
      return null;
   }


   private String getTypeString(LanguageEnum language, CategoryEnum category, TypeEnum type) {

      switch(language) {
      
         case KOREAN:
            
            switch(category) {
            
               case NEWS:
                  
                  switch(type) {
                  
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
      
      for(Passage passage : noQuestionPassageList) {
         
         String prompt = generateQuestionPrompt(passage);
         
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


   private String generateQuestionPrompt(Passage passage) {
      
      String prompt = "";
      
      switch(passage.getLanguage().getLanguageName()) {
      
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

	@Transactional
	public void generateChallengeQuestions() {
		
		
	}





   
   
}
