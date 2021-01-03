package com.stephenschafer.ami.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeDao extends CrudRepository<TypeEntity, Integer> {
	UserEntity findByName(String name);
}
