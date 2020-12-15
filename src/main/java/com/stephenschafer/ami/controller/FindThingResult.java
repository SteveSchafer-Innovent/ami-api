package com.stephenschafer.ami.controller;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.stephenschafer.ami.jpa.FindTypeResult;
import com.stephenschafer.ami.jpa.UserEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindThingResult {
	private int id;
	private UserEntity creator;
	private Date created;
	private FindTypeResult type;
	private Map<String, Object> attributes;
	private Map<Integer, Set<Integer>> links;
}
