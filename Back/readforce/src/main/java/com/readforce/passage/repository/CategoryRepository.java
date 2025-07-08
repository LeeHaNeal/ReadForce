package com.readforce.passage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // ✅ 이걸로 수정
import org.springframework.stereotype.Repository;

import com.readforce.passage.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	// ✅ NativeQuery 삭제, JPA 쿼리 방식으로
	Optional<Category> findByCategory(String category);
}

