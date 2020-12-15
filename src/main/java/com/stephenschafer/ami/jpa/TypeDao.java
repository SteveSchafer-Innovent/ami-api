package com.stephenschafer.ami.jpa;

import org.springframework.data.repository.CrudRepository;

public interface TypeDao extends CrudRepository<TypeEntity, Integer> {
	UserEntity findByName(String name);
}
