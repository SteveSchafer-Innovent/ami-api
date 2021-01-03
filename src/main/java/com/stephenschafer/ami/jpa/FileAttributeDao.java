package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileAttributeDao extends CrudRepository<FileAttributeEntity, AttributeId> {
	void deleteByThingId(Integer thingId);

	List<FileAttributeEntity> findByThingId(int thingId);
}
