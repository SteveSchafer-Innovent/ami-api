package com.stephenschafer.ami.service;

import java.util.List;

import com.stephenschafer.ami.jpa.FindTypeResult;
import com.stephenschafer.ami.jpa.TypeEntity;

public interface TypeService {
	TypeEntity insert(TypeEntity type);

	TypeEntity update(TypeEntity type);

	List<TypeEntity> findAll();

	FindTypeResult findById(int id);

	void delete(int id);
}