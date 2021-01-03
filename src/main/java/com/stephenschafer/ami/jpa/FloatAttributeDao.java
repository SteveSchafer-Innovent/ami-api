package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloatAttributeDao extends CrudRepository<FloatAttributeEntity, AttributeId> {
	void deleteByThingId(Integer thingId);

	List<FloatAttributeEntity> findByThingId(int thingId);
}
