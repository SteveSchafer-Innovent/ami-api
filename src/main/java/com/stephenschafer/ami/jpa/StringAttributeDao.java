package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StringAttributeDao extends CrudRepository<StringAttributeEntity, AttributeId> {
	void deleteByThingId(Integer thingId);

	List<StringAttributeEntity> findByThingId(int thingId);

	List<StringAttributeEntity> findByAttrDefnIdAndValue(int attrDefnId, String value);
}
