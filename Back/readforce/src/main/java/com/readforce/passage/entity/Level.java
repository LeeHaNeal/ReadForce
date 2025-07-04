package com.readforce.passage.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Level {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long levelNo;
	
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime lastModifiedAt;
	
}
