package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkAttributeDao extends CrudRepository<LinkAttributeEntity, LinkAttributeId> {
	List<LinkAttributeEntity> findByThingIdAndAttributeDefnId(Integer thingId,
			Integer attributeDefnId);

	void deleteByThingId(Integer thingId);

	void deleteByThingIdAndAttributeDefnId(Integer thingId, Integer attributeDefnId);

	List<LinkAttributeEntity> findByThingId(Integer thingId);

	List<LinkAttributeEntity> findByTargetThingId(Integer thingId);

	List<LinkAttributeEntity> findByTargetThingIdAndAttributeDefnId(Integer thingId,
			Integer attrDefnId);
}
