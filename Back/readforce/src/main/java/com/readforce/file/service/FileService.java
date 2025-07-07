package com.readforce.file.service;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.FileCategory;
import com.readforce.common.enums.Name;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.file.exception.FileException;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

	@Value("${file.image.profile.upload-dir}")
	private String profileImageUploadDir;
	
	@Value("${file.image.profile.max-size}")
	private Long profileImageMaxFileSize;
	
	@Value("#{'${file.image.profile.allowed-mime-types}'.split(',')}")
	private List<String> profileImageAllowedMimeTypeList;
	
	@Value("${file.image.profile.default-image-path}")
	private String profileDefaultImagePath;
	
	
	private Path getPath(String uploadDir) {
		
		Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
		
		if(!path.isAbsolute()) {
			
			String currentWorkingDir = System.getProperty("user.dir");
			path = Paths.get(currentWorkingDir, uploadDir);
			
		}
		
		Path normalizedPath = path.normalize();		
		
		try {
			
			Files.createDirectories(path);
			return path;
			
		} catch(Exception exception) {
			
			throw new FileException(MessageCode.DIRECTORY_CREATION_FAIL);
			
		}
		
	}
	
	private void validateFile(MultipartFile multipartFile, long maxSize, List<String> typeList) {
		
		if(multipartFile.isEmpty()) {
			
			throw new FileException(MessageCode.FILE_NOT_NULL);
			
		}
		
		if(multipartFile.getSize() > maxSize) {
			
			throw new FileException(MessageCode.FILE_SIZE_INVALID);
			
		}
		
		String mimeType = multipartFile.getContentType();
		if(mimeType == null || !typeList.contains(mimeType)) {
			
			throw new FileException(MessageCode.FILE_TYPE_INVALID);
			
		}
		
	}
	
	
	
	private String generateFileName(MultipartFile multipartFile) {
		
		String originalFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
		String fileName = UUID.randomUUID().toString() + extension;
		
		return fileName;
		
	}
	
	public Resource loadFileAsResource(String fileName, FileCategory fileCategory) {
		
		Map<Name, Object> fileInformationMap = getFileInformationFromFileCategory(fileCategory);
		
		String uploadDir = (String) fileInformationMap.get(Name.UPLOAD_DIR);
		
		try {
			
			Path filePath = this.getPath(uploadDir).resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			
			if(resource.exists()) {
				
				return resource;
				
			} else {
				
				throw new ResourceNotFoundException(MessageCode.FILE_NOT_FOUND);
				
			}
			
		} catch(MalformedURLException exception) {
			
			throw new FileException(MessageCode.FILE_PATTERN_INVALID);
			
		} catch(Exception exception) {
			
			throw new FileException(MessageCode.FILE_LOAD_FAIL);
			
		}
		
	}
	
	public void deleteFile(String fileName, FileCategory fileCategory) {
		
		Map<Name, Object> fileInformationMap = getFileInformationFromFileCategory(fileCategory);
		
		String uploadDir = (String) fileInformationMap.get(Name.UPLOAD_DIR);
		
		try {
			
			Path filePath = this.getPath(uploadDir).resolve(fileName).normalize();
			Files.deleteIfExists(filePath);
			
		} catch(IOException exception) {
			
			throw new FileException(MessageCode.FILE_DELETE_IO_FAIL);
			
		} catch(Exception exception) {
			
			throw new FileException(MessageCode.FILE_DELETE_FAIL);
			
		}
		
	}

	public String storeFile(MultipartFile multipartFile, FileCategory fileCategory) {
		
		Map<Name, Object> fileInformationMap = getFileInformationFromFileCategory(fileCategory);
		
		String uploadDir = (String) fileInformationMap.get(Name.UPLOAD_DIR);
		Long maxFileSize = (Long) fileInformationMap.get(Name.MAX_FILE_SIZE);
		List<String> fileTypeList = (List<String>) fileInformationMap.get(Name.FILE_TYPE_LIST);
				
		validateFile(multipartFile, maxFileSize, fileTypeList);
		
		String fileName = generateFileName(multipartFile);
		
		try {
			
			Path targetLocation = this.getPath(uploadDir).resolve(fileName);
			Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return fileName;
			
		} catch(Exception exception) {
			
			throw new FileException(MessageCode.FILE_STORE_FAIL);
			
		}

	}
	
	private Map<Name, Object> getFileInformationFromFileCategory(FileCategory fileCategory) {

		Map<Name, Object> fileInformationMap = new HashMap<>();
		
		switch(fileCategory) {
			
			case PROFILE_IMAGE:
				fileInformationMap.put(Name.UPLOAD_DIR, profileImageUploadDir);
				fileInformationMap.put(Name.MAX_FILE_SIZE, profileImageMaxFileSize);
				fileInformationMap.put(Name.FILE_TYPE_LIST, profileImageAllowedMimeTypeList);
				break;
				
			default:
				throw new FileException(MessageCode.FILE_CATEGORY_REQUIRED);
				
		}
		
		return fileInformationMap;
		
	}

	
	
	
	
}
