//package com.readforce.ai.controller;
//
//import java.util.Map;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.readforce.ai.service.AiService;
//import com.readforce.common.MessageCode;
//import com.readforce.common.enums.Language;
//import com.readforce.passage.validation.ValidEnum;
//
//import jakarta.validation.constraints.NotBlank;
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/ai")
//@RequiredArgsConstructor
//@Validated
//public class AiController {
//
//	private final AiService aiService;
////	
////	@GetMapping("/generate-test")
////	public ResponseEntity<Map<String, String>> generateTest(
////			@RequestParam("langugage")
////			@NotBlank(message = MessageCode.LANGUAGE_NOT_BLANK)
////			@ValidEnum(enumClass = Language.class, message = MessageCode.LANGUAGE_INVALID)
////			String language
////	){
////		
////		aiService.generateTestVocabulary();
////		
////		aiService.generateTestQuestion();
////		
////	}
////	
//}
