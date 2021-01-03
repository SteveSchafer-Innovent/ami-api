package com.stephenschafer.ami.controller;

import java.io.Serializable;

import com.stephenschafer.ami.jpa.UserEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String firstName;
	private String lastName;
	private String username;
	private String context;

	public User(final UserEntity userEntity) {
		this.id = userEntity.getId();
		this.firstName = userEntity.getFirstName();
		this.lastName = userEntity.getLastName();
		this.username = userEntity.getUsername();
		this.context = userEntity.getContext();
	}
}
