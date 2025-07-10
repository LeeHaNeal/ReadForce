package com.readforce.administrator.controller.type;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.administrator.dto.AdministratorTypeRequestDto;
import com.readforce.administrator.dto.AdministratorTypeResponseDto;
import com.readforce.common.MessageCode;
import com.readforce.passage.entity.Type;
import com.readforce.passage.service.TypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/administrator/type")
@RequiredArgsConstructor
@Validated
public class AdministratorTypeController {

	private final TypeService typeService;
	
	@GetMapping("/get-all-list")
	public ResponseEntity<List<AdministratorTypeResponseDto>> getAllList(){
		
		List<AdministratorTypeResponseDto> typeList = typeService.getAllTypeList().stream()
				.map(AdministratorTypeResponseDto::new)
				.collect(Collectors.toList());
		
		return ResponseEntity.status(HttpStatus.OK).body(typeList);
		
	}
	
	@PostMapping("/create")
	public ResponseEntity<Map<String, String>> create(
		@Valid @RequestBody AdministratorTypeRequestDto requestDto	
	){
		
		typeService.saveType(Type.builder().typeName(requestDto.getType()).build());
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.CREATE_TYPE_SUCCSEE
		));
		
	}
	
}
