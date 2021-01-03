package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThingDao extends CrudRepository<ThingEntity, Integer> {
	List<ThingEntity> findByTypeId(Integer typeId);
}
