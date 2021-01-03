package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypeDao extends CrudRepository<UserTypeEntity, UserTypeId> {
	void deleteByUserId(Integer userId);

	List<UserTypeEntity> findByUserId(int userId);
}
