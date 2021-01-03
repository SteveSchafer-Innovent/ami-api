package com.stephenschafer.ami.service;

import java.util.List;
import java.util.Map;

import com.stephenschafer.ami.controller.FindThingResult;
import com.stephenschafer.ami.jpa.ThingEntity;

public interface ThingService {
	ThingEntity insert(ThingEntity thing);

	ThingEntity update(ThingEntity thing);

	void delete(int thingId);

	FindThingResult getFindThingResult(ThingEntity thing);

	ThingEntity findById(int thingId);

	List<ThingEntity> findByTypeId(int typeId);

	List<Map<String, Object>> getSelectOptions();

	List<Map<String, Object>> getSelectOptions(int typeId);

	void updateThingOrder(int userId, int typeId, List<Integer> thingIds);

	void updateThingOrder(int userId, int typeId, int contextThingId, List<Integer> thingIds);

	List<Integer> getThingOrder(int userId, int typeId);

	List<Integer> getThingOrder(int userId, int typeId, int contextThingId);
}
