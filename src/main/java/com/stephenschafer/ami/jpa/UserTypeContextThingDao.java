package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypeContextThingDao
		extends CrudRepository<UserTypeContextThingEntity, UserTypeContextThingId> {
	void deleteByUserId(int userId);

	void deleteByUserIdAndTypeId(int userId, int typeId);

	void deleteByUserIdAndTypeIdAndContextThingId(int userId, int typeId, int contextThingId);

	List<UserTypeContextThingEntity> findByUserId(int userId);

	List<UserTypeContextThingEntity> findByUserIdAndTypeId(int userId, int typeId);

	List<UserTypeContextThingEntity> findByUserIdAndTypeIdAndContextThingId(int userId, int typeId,
			int contextThingId);
}
