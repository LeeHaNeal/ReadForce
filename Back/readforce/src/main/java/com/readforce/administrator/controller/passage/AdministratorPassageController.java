package com.readforce.administrator.controller.passage;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.administrator.dto.AdministratorUploadPassageRequestDto;
import com.readforce.common.MessageCode;
import com.readforce.passage.service.PassageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/administrator/passage")
@RequiredArgsConstructor
public class AdministratorPassageController {

	private final PassageService passageService;
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/upload-passage")
	public ResponseEntity<Map<String, String>> uploadPassage(
			@Valid @RequestBody AdministratorUploadPassageRequestDto requestDto	
	){
		
		passageService.uploadPassage(requestDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.UPLOAD_PASSAGE_SUCCESS
		));
		
	}
	
	
}
