//package com.readforce.ai.service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.readforce.passage.entity.Language;
//import com.readforce.passage.entity.Level;
//import com.readforce.passage.service.CategoryService;
//import com.readforce.passage.service.LanguageService;
//import com.readforce.passage.service.LevelService;
//import com.readforce.passage.service.TypeService;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class AiService {
//	
//	private final LevelService levelService;
//	private final CategoryService categoryService;
//	private final TypeService typeService;
//	private final LanguageService languageService;
//	
//	@Transactional
//	public void generateTestPassage() {
//		
//		List<Language> languageList = languageService.getAllLanguageList();
//		
//		List<Level> levelList = levelService.getAllLevelList();
//		
//		List<String> passageTitleAndContentList = new ArrayList<>();
//		
//		for(Language language : languageList) {
//			
//			for(Level level : levelList) {
//				
//				passageTitleAndContentList.add(gernerateTestPassagePrompt(language, level));
//				
//			}
//			
//		}
//		
//	}
//	
//	
//	private String gernerateTestPassagePrompt(Language language, Level level) {
//
//		switch(language.getLanguage()) {
//		
//		default String.format(
//			"""
//				당신은 한국어 어휘의 전문가입니다.
//				'난이도 %d (%s)' 수준의 단어를 JSON 형식으로 생성해 주세요.
//				
//				요청 형식:
//				{
//				  "title": "여기에 단어를 생성해주세요.",
//				  "content": "여기에 단어의 사전적 의미를 작성해주세요.",
//				  "level": "여기에 단어 생성에 사용된 난이도를 적어주세요." 
//				}
//				
//				
//				""", level.getLevel(), level.getVocabularyLevel());
//		}
//			
//		
//		
//	}
//
//
//	
//
//
//
//
//	
//	
//}
