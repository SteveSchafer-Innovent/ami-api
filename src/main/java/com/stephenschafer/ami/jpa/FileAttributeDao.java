package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface FileAttributeDao extends CrudRepository<FileAttributeEntity, AttributeId> {
	void deleteByThingId(Integer thingId);

	List<FileAttributeEntity> findByThingId(int thingId);
}
