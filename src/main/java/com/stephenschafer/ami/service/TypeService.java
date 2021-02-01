package com.stephenschafer.ami.service;

import java.util.List;

import com.stephenschafer.ami.jpa.TypeEntity;

public interface TypeService {
	TypeEntity insert(int userId, TypeEntity type);

	TypeEntity update(TypeEntity type);

	List<TypeEntity> findAll();

	TypeEntity findById(int id);

	void delete(int id);

	TypeEntity getOrCreate(String name, int userId);

	TypeEntity findByName(String string);
}
