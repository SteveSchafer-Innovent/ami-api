package com.stephenschafer.ami.service;

import java.util.List;
import java.util.Map;

import com.stephenschafer.ami.jpa.ThingEntity;

public interface ThingService {
	ThingEntity insert(ThingEntity thing);

	ThingEntity update(ThingEntity thing);

	ThingEntity findById(Integer thingId);

	List<ThingEntity> findByTypeId(Integer typeId);

	void delete(Integer thingId);

	List<Map<String, Object>> getSelectOptions(Integer typeId);
}
