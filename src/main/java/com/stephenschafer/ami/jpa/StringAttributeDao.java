package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface StringAttributeDao extends CrudRepository<StringAttributeEntity, AttributeId> {
	void deleteByThingId(Integer thingId);

	List<StringAttributeEntity> findByThingId(int thingId);
}
