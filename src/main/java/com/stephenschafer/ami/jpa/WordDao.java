package com.stephenschafer.ami.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordDao extends PagingAndSortingRepository<WordEntity, Integer> {
	WordEntity findByWord(String word);
}
