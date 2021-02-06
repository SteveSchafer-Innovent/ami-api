package com.stephenschafer.ami.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MisspellingsDoa extends CrudRepository<MisspellingsEntity, String> {
}
