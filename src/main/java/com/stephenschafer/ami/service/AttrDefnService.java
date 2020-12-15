package com.stephenschafer.ami.service;

import java.util.List;

import com.stephenschafer.ami.jpa.AttrDefnEntity;

public interface AttrDefnService {
	List<AttrDefnEntity> list(int typeId);

	AttrDefnEntity findById(int id);

	AttrDefnEntity findByName(int typeId, String name);
}
