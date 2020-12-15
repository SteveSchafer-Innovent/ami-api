package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface FloatAttributeDao extends CrudRepository<FloatAttributeEntity, AttributeId> {
	void deleteByThingId(Integer thingId);

	List<FloatAttributeEntity> findByThingId(int thingId);
}
