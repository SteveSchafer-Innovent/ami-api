package com.stephenschafer.ami.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.TypeDao;
import com.stephenschafer.ami.jpa.TypeEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service(value = "typeService")
public class TypeServiceImpl implements TypeService {
	@Autowired
	private TypeDao typeDao;

	@Override
	public TypeEntity insert(final int userId, final TypeEntity type) {
		final TypeEntity newType = new TypeEntity();
		newType.setName(type.getName());
		newType.setCreated(new Date());
		newType.setCreator(userId);
		return typeDao.save(newType);
	}

	@Override
	public TypeEntity update(final TypeEntity type) {
		final TypeEntity newType = findById(type.getId());
		if (newType != null) {
			BeanUtils.copyProperties(type, newType);
			typeDao.save(newType);
		}
		return type;
	}

	@Override
	public void delete(final int id) {
		typeDao.deleteById(id);
	}

	@Override
	public TypeEntity findById(final int id) {
		log.info("findById " + id);
		final Optional<TypeEntity> optional = typeDao.findById(id);
		if (!optional.isPresent()) {
			return null;
		}
		return optional.get();
	}

	@Override
	public List<TypeEntity> findAll() {
		final List<TypeEntity> list = new ArrayList<>();
		typeDao.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public TypeEntity getOrCreate(final String name, final int userId) {
		final Optional<TypeEntity> optType = typeDao.findByName(name);
		TypeEntity type;
		if (optType.isPresent()) {
			type = optType.get();
		}
		else {
			type = new TypeEntity();
			type.setCreated(new Date());
			type.setCreator(userId);
			type.setName(name);
			type = typeDao.save(type);
		}
		return type;
	}

	@Override
	public TypeEntity findByName(final String string) {
		final Optional<TypeEntity> optional = typeDao.findByName(string);
		return optional.isPresent() ? optional.get() : null;
	}
}
