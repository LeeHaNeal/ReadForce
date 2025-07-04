package com.readforce.passage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.passage.entity.Type;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {

}
