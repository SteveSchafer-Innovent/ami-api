package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LinkAttributeDao extends CrudRepository<LinkAttributeEntity, LinkAttributeId> {
	List<LinkAttributeEntity> findByThingIdAndAttributeDefnId(Integer thingId,
			Integer attributeDefnId);

	void deleteByThingId(Integer thingId);

	void deleteByThingIdAndAttributeDefnId(Integer thingId, Integer attributeDefnId);

	List<LinkAttributeEntity> findByTargetThingId(Integer thingId);
}
