package com.stephenschafer.ami.service;

import java.util.List;

import com.stephenschafer.ami.jpa.UserEntity;

public interface UserService {
	UserEntity save(UserEntity user);

	List<UserEntity> findAll();

	void delete(int id);

	UserEntity findByUsername(String username);

	UserEntity findById(int id);

	UserEntity update(UserEntity user);

	void changePassword(int id, String password);

	String getContext(int id);

	void setContext(int id, String context);
}
