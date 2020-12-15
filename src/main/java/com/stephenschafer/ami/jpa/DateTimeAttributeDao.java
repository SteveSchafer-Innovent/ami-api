package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface DateTimeAttributeDao
		extends CrudRepository<DateTimeAttributeEntity, AttributeId> {
	void deleteByThingId(Integer thingId);

	List<DateTimeAttributeEntity> findByThingId(int thingId);
}
