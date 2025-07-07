//package com.readforce.ai.controller;
//
//import java.util.Map;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.readforce.ai.service.AiService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/ai")
//@RequiredArgsConstructor
//public class AiController {
//
//	private final AiService aiService;
//	
//	@GetMapping("/generate-test")
//	public ResponseEntity<Map<String, String>> generateTest(){
//		
//		aiService.generateTestPassage();
//		
//		aiService.generateTestQuestion();
//		
//	}
//	
//}
