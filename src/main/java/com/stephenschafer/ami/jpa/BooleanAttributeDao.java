package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BooleanAttributeDao extends CrudRepository<BooleanAttributeEntity, AttributeId> {
	void deleteByThingId(Integer thingId);

	List<BooleanAttributeEntity> findByThingId(int thingId);
}
