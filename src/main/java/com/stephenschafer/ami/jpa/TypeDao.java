package com.stephenschafer.ami.jpa;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeDao extends CrudRepository<TypeEntity, Integer> {
	Optional<TypeEntity> findByName(String name);
}
