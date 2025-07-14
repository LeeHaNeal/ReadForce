package com.readforce.ai.service;

import org.springframework.stereotype.Service;

import com.readforce.ai.dto.AiGeneratePassageRequestDto;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.enums.TypeEnum;
import com.readforce.passage.entity.Language;
import com.readforce.passage.entity.Level;
import com.readforce.passage.entity.Passage;
import com.readforce.passage.service.LevelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromptService {

	private LevelService levelService;

	public String gernerateTestVocabularyPrompt(Language language, Level level) {

		String prompt = "";
  
		switch(language.getLanguageName()) {
  
		case KOREAN:
		prompt = String.format("""
				당신은 초등학생부터 성인까지 다양한 연령대를 위한 **어휘 학습 애플리케이션 콘텐츠 제작 전문가**입니다.
				'난이도 %d (%s)' 수준의 단어 데이터를 생성해야 합니다. 생성된 데이터는 어휘 학습 애플리케이션에서 사용될 예정입니다.

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
	
	public String generateTestFactualPassageAndQuestionPrompt(Language language, Level level) {
	      
		String prompt = "";
  
		switch(language.getLanguageName()) {
  
			case KOREAN:
				prompt = String.format("""
						당신은 **한국어 문해력 평가 문제 출제 전문가**입니다. 특히, 주어진 글의 내용을 정확하게 이해하고 사실 정보를 파악하는 능력을 평가하는 '사실적 이해' 문제 제작을 담당합니다.
						'난이도 %d (%s)' 수준에 맞는, **세 문단으로 구성된 약 200자 내외의 설명문**을 생성하고, 해당 글의 내용과 일치하는 사실을 찾는 객관식 문제를 1개 생성해주세요.
						
						## 중요 규칙
						- **정답 선택지:** 1개
						- **오답 선택지:** 3개 (정답과 유사하지만 미세하게 다르거나, 본문의 일부 내용만 포함하여 혼동을 유발하는 매력적인 오답으로 구성)
						- **출력 형식:** 다른 설명 없이, 반드시 아래의 **JSON 객체** 형식으로만 응답해야 합니다.
						
						## 응답 형식:
						{
						    "title": "여기에 지문 제목을 생성해주세요.",
						    "content": "여기에 세 문단으로 구성된, 약 200자 내외의 설명문을 생성해주세요.",
						    "question": "생성된 질문",
						    "choiceList": ["선택지 1", "선택지 2", "선택지 3", "선택지 4"],
						    "correctAnswerIndex": 정답_선택지의_인덱스 (0-3 사이의 숫자),
						    "explanation": {
						        "correct": "정답인 이유와 본문의 어떤 문장을 근거로 하는지 명확하게 설명해주세요.",
						        "incorrect": "각 오답이 왜 틀렸는지, 본문의 어떤 내용과 다른지 구체적으로 설명해주세요."
						    }
						}
						
						이제 위의 모든 지시사항을 준수하여 문제를 생성해주세요.
						
						
						""", level.getLevelNumber(), level.getVocabularyLevel());
				break;

			default:
            
		}
      
		return prompt;
      
	}
	
	public String generateTestInferentialPassageAndQuestionPrompt(Language language, Level level) {
	      
		String prompt = "";
	      
		switch(language.getLanguageName()) {
	      
			case KOREAN:
				prompt = String.format("""
						당신은 **한국어 문해력 평가 문제 출제 전문가**입니다. 특히, 글에 명시적으로 드러나지 않은 내용이나 글쓴이의 숨겨진 의도를 파악하는 능력을 평가하는 '추론적 이해' 문제 제작을 담당합니다.
						'난이도 %d (%s)' 수준에 맞는, **세 문단으로 구성된 약 200자 내외의 논설문 또는 이야기 글**을 생성하고, 해당 글의 내용을 바탕으로 논리적으로 추론할 수 있는 내용을 찾는 객관식 문제를 1개 생성해주세요.
						
						## 중요 규칙
						- **추론 유형:** 글에 명시적으로 드러나지 않은 **결론, 생략된 내용, 또는 필자의 의도**를 추론하는 문제를 출제해야 합니다.
						- **정답 선택지:** 1개
						- **오답 선택지:** 3개 (그럴듯하지만 논리적 비약이 있거나, 본문의 내용만으로는 근거가 불충분한 매력적인 오답으로 구성)
						- **출력 형식:** 다른 설명 없이, 반드시 아래의 **JSON 객체** 형식으로만 응답해야 합니다.
						
						## 응답 형식:
						{
						    "title": "여기에 지문 제목을 생성해주세요.",
						    "content": "여기에 세 문단으로 구성된, 약 200자 내외의 논설문 또는 이야기 글을 생성해주세요.",
						    "question": "생성된 질문",
						    "choiceList": ["선택지 1", "선택지 2", "선택지 3", "선택지 4"],
						    "correctAnswerIndex": 정답_선택지의_인덱스 (0-3 사이의 숫자),
						    "explanation": {
						        "correct": "정답인 이유와 본문의 어떤 내용을 근거로 추론할 수 있는지 논리적으로 설명해주세요.",
						        "incorrect": "각 오답이 왜 틀렸는지, 어떤 부분에서 논리적 비약이나 잘못된 추론이 발생했는지 구체적으로 설명해주세요."
						    }
						}
						
						이제 위의 모든 지시사항을 준수하여 문제를 생성해주세요.
						""", level.getLevelNumber(), level.getVocabularyLevel());
				break;
	            
			default:
	     
		}
	      
		return prompt;
	      
	}
	
	public String generatePassagePrompt(AiGeneratePassageRequestDto aiGeneratePassageRequestDto) {

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
		        				당신은 **`%s` 분야를 전문적으로 다루는 베테랑 기자**입니다. 이번 기사의 핵심 주제는 **`%s`** 입니다. 당신의 글은 명확한 정보 전달을 목표로 하며, 독자들이 쉽게 이해할 수 있도록 **객관적이고 정보 전달적인 어조**를 유지해야 합니다.
								지금부터 반드시 다음 조건에 맞춰 뉴스 기사를 작성해 주세요.
								
								## 지침
								1.  **언어:** %s
								2.  **카테고리:** %s
								3.  **세부 주제:** %s
								4.  **독자 수준 (난이도):** 전체 10 단계 중 %d
								    - **어휘:** %s 수준
								    - **문장 구조:** %s
								    - **내용:** '%s' 주제와 관련된 최신 동향이나 구체적인 실제 사례를 포함하여 신뢰도를 높여주세요.
								    - **분량:** 제목을 포함하여 총 %d개의 문단으로 구성해주세요.
								5.  **출력 형식:** 다른 어떤 설명도 없이, 반드시 아래 JSON 형식으로만 응답해 주세요.
								
								## 응답 형식:
								{
								  "title": "기사 제목",
								  "content": "기사 본문 내용. 문단 구분을 위해 '\n\n'를 사용하세요."
								}
								
								이제, 위의 모든 지시사항을 철저히 준수하여 기사를 작성해주세요.		        				
		        				""",
		                            typeString,
		                            typeString,
		                            languageString,
		                            categoryString,
		                            typeString,
		                            level.getLevelNumber(),
		                            level.getVocabularyLevel(),
		                            level.getSentenceStructure(),
		                            typeString,
		                            level.getParagraphCount()
		        				);
		        		break;
		        	
		        	default:
		        		prompt = "지원하지 않는 언어입니다.";
		        		break;
		            }
		            break;
			
			default:
				prompt = "지원하지 않는 카테고리입니다.";
				break;
		    }
		
		return prompt;
		
	}
	
	public String generateQuestionPrompt(Passage passage) {

		String prompt = "";

		switch(passage.getLanguage().getLanguageName()) {

			case KOREAN:

				prompt = String.format("""
						당신은 **데이터 기반의 정교한 문해력 평가 시스템을 위한 문제 생성 AI**입니다. 당신의 임무는 주어진 텍스트와 메타데이터를 분석하여, 학생들의 이해도를 정확하게 측정할 수 있는 고품질의 객관식 문제와 상세한 해설을 생성하는 것입니다.
						주어진 텍스트와 조건에 맞춰, 아래의 출력 형식에 따라 문제와 해설을 생성해주세요.
						
						## 입력 데이터
						1.  **주어진 텍스트:**
						    ```
						    {%s}
						    ```
						2.  **텍스트의 난이도:** 전체 10의 난이도 중 **{%d}**
						3.  **질문 유형:** **{%s}**
						4.  **생성할 문제 수:** **{%d}**
						
						## 핵심 규칙
						- **선택지 구성:** 문제의 난이도에 맞게 오답 선택지의 매력도를 조절해야 합니다. (예: 난이도가 높을수록 오답이 더 정교하고 미묘해야 함)
						- **상세한 해설:** 정답의 근거를 **본문의 특정 문장을 인용**하여 명확하게 제시하고, 각 오답이 틀린 이유를 논리적으로 설명해야 합니다.
						- **출력 형식:** 다른 어떤 설명도 없이, 반드시 아래의 **JSON 배열** 형식에 맞춰서, 생성할 문제 수만큼의 JSON 객체를 포함하여 응답해야 합니다.
						
						## 응답 형식:
						[
						  {
						    "question": "문제 내용",
						    "choiceList": ["선택지 1", "선택지 2", "선택지 3", "선택지 4"],
						    "correctAnswerIndex": 0,
						    "explanation": {
						      "correct": "정답인 이유와 본문의 어떤 문장을 근거로 하는지 명확하게 설명해주세요.",
						      "incorrect": "각 오답이 왜 틀렸는지, 본문의 어떤 내용과 다른지 구체적으로 설명해주세요."
						    }
						  }
						]
						
						이제, 위의 모든 지시사항을 준수하여 문제를 생성해주세요.
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
	
	public String gernerateTestVocabularyQuestionPrompt(Language language, Level level, String title, String content) {
	      
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
						
						## 중요 규칙
						- 반드시 아래의 JSON 형식으로만 응답해야 합니다.
						- 다른 설명은 포함하지 마세요.
						
						## 응답 형식
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


	   
	
}
