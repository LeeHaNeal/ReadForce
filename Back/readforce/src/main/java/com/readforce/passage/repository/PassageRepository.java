package com.readforce.passage.repository;

import java.util.List;

import com.readforce.common.enums.Category;
import com.readforce.common.enums.Classification;
import com.readforce.common.enums.Language;
import com.readforce.common.enums.Type;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.readforce.passage.dto.PassageResponseDto;
import com.readforce.passage.entity.Passage;



@Repository
public interface PassageRepository extends JpaRepository<Passage, Long> {

	@Query("""
			SELECT new com.readforce.passage.dto.PassageResponseDto(
					p.passageNo, 
					p.title, 
					p.content, 
					p.author, 
					p.publicationDate,
					p.createdAt,
					c.category,
					t.type,
					l.level,
					lang.language,
					cl.classification
			)
			FROM Passage p
			JOIN p.category c
			JOIN p.type t
			JOIN p.level l
			JOIN p.language lang
			JOIN p.classification cl
			WHERE lang.language = :language 
			AND cl.classification = :classification 
			AND c.category = :category
	""")
	List<PassageResponseDto> findByLanguage_LanguageAndClassification_ClassificationAndCategory_Category(
			@Param("language") String language,
			@Param("classification") String classification,
			@Param("category") String category,
			Sort sort
	);

	
	
	@Query("""
			SELECT new com.readforce.passage.dto.PassageResponseDto(
					p.passageNo, 
					p.title, 
					p.content, 
					p.author, 
					p.publicationDate,
					p.createdAt,
					c.category,
					t.type,
					l.level,
					lang.language,
					cl.classification
			)
			FROM Passage p
			JOIN p.category c
			JOIN p.type t
			JOIN p.level l
			JOIN p.language lang
			JOIN p.classification cl
			WHERE lang.language = :language 
			AND cl.classification = :classification
			AND c.category = :category
			AND t.type = :type
	""")
	List<PassageResponseDto> findByLanguage_LanguageAndClassification_ClassificationAndCategory_CategoryAndType_type(
			@Param("language") Language language,
			@Param("classification") Classification classification,
			@Param("category") Category category,
			@Param("type") Type type,
			Sort sort
	);

	@Query("""
			SELECT new com.readforce.passage.dto.PassageResponseDto(
					p.passageNo, 
					p.title, 
					p.content, 
					p.author, 
					p.publicationDate,
					p.createdAt,
					c.category,
					t.type,
					l.level,
					lang.language,
					cl.classification
			)
			FROM Passage p
			JOIN p.category c
			JOIN p.type t
			JOIN p.level l
			JOIN p.language lang
			JOIN p.classification cl
			WHERE lang.language = :language 
			AND cl.classification = :classification
			AND c.category = :category
			AND t.type = :type
			AND l.level = :level
	""")
	List<PassageResponseDto> findByLanguage_LanguageAndClassification_ClassificationAndCategory_CategoryAndType_typeAndLevel_level(
			@Param("language") Language language,
			@Param("classification") Classification classification,
			@Param("category") Category category,
			@Param("type") Type type,
			@Param("level") Integer level,
			Sort sort
	);



	long countByLanguage_LanguageAndCategory_CategoryAndLevel_Level(String language, String category, Integer level);



	@Query("""
			SELECT p.passageNo 
			FROM Passage p
			JOIN p.category c
			JOIN p.type t
			JOIN p.level l
			JOIN p.language lang
			JOIN p.classification cl
			WHERE lang.language = :language 
			AND cl.classification = :classification
			AND c.category = :category
			AND t.type = :type
			AND l.level = :level
			""")
	List<Long> findPassageNoListByLanguageAndClassificationAndCategoryAndTypeAndLevel(
			@Param("language") String language,
			@Param("classification") Classification classification,
			@Param("category") Category category,
			@Param("type") Type type,
			@Param("level") Integer level
	);


}
