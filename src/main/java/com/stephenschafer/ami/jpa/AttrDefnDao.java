package com.stephenschafer.ami.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface AttrDefnDao extends PagingAndSortingRepository<AttrDefnEntity, Integer> {
	List<AttrDefnEntity> findByTypeIdOrderBySortOrder(int typeId);

	Optional<AttrDefnEntity> findByTypeIdAndName(int typeId, String name);
}
