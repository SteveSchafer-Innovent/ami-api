package com.stephenschafer.ami.jpa;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordThingDao extends PagingAndSortingRepository<WordThingEntity, WordThingId> {
	List<WordThingEntity> findByWordId(int wordId);

	List<WordThingEntity> findByWordIdAndAttrdefnId(int wordId, int attrdefnId);

	void deleteByThingId(int thingId);

	void deleteByThingIdAndAttrdefnId(int thingId, int attrdefnId);
}
