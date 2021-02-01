package com.stephenschafer.ami.service;

import java.util.List;

import com.stephenschafer.ami.jpa.AttrDefnEntity;

public interface AttrDefnService {
	List<AttrDefnEntity> list();

	List<AttrDefnEntity> findByTypeIdOrderBySortOrder(int typeId);

	List<AttrDefnEntity> findByTypeId(int typeId);

	AttrDefnEntity findById(int id);

	AttrDefnEntity findByName(int typeId, String name);

	List<AttrDefnEntity> findAll();

	AttrDefnEntity save(AttrDefnEntity entity);

	void deleteById(int id);
}
