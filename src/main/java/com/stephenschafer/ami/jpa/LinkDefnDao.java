package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LinkDefnDao extends CrudRepository<LinkDefnEntity, Integer> {
	List<LinkDefnEntity> findByTargetTypeId(int typeId);

	List<LinkDefnEntity> findByTargetTypeIdIsNull();
}
