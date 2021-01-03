package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypeThingDao extends CrudRepository<UserTypeThingEntity, UserTypeThingId> {
	void deleteByUserId(int userId);

	void deleteByUserIdAndTypeId(int userId, int typeId);

	List<UserTypeThingEntity> findByUserId(int userId);

	List<UserTypeThingEntity> findByUserIdAndTypeId(int userId, int typeId);
}
