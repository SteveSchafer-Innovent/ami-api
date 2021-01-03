package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegerAttributeDao extends CrudRepository<IntegerAttributeEntity, AttributeId> {
	void deleteByThingId(Integer thingId);

	List<IntegerAttributeEntity> findByThingId(int thingId);
}
