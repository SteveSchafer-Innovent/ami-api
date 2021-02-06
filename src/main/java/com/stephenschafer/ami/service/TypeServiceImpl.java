package com.stephenschafer.ami.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.TypeDao;
import com.stephenschafer.ami.jpa.TypeEntity;

@Transactional
@Service(value = "typeService")
public class TypeServiceImpl implements TypeService {
	@Autowired
	private TypeDao typeDao;
	private final Map<String, TypeEntity> nameCache = new HashMap<>();
	private final Map<Integer, TypeEntity> idCache = new HashMap<>();

	private TypeEntity updateCache(final TypeEntity entity) {
		if (entity != null) {
			idCache.put(entity.getId(), entity);
			nameCache.put(entity.getName(), entity);
		}
		return entity;
	}

	private void updateCache(final List<TypeEntity> list) {
		for (final TypeEntity entity : list) {
			updateCache(entity);
		}
	}

	private void removeFromCache(final int id) {
		final TypeEntity entity = idCache.get(id);
		if (entity != null) {
			idCache.remove(entity.getId());
			nameCache.remove(entity.getName());
		}
	}

	@Override
	public TypeEntity insert(final int userId, final TypeEntity type) {
		final TypeEntity newType = new TypeEntity();
		newType.setName(type.getName());
		newType.setCreated(new Date());
		newType.setCreator(userId);
		return updateCache(typeDao.save(newType));
	}

	@Override
	public TypeEntity update(final TypeEntity type) {
		final TypeEntity newType = findById(type.getId());
		if (newType != null) {
			BeanUtils.copyProperties(type, newType);
			return updateCache(typeDao.save(newType));
		}
		return type;
	}

	@Override
	public void delete(final int id) {
		typeDao.deleteById(id);
		removeFromCache(id);
	}

	@Override
	public TypeEntity findById(final int id) {
		final TypeEntity type = idCache.get(id);
		if (type != null) {
			return type;
		}
		final Optional<TypeEntity> optional = typeDao.findById(id);
		if (!optional.isPresent()) {
			return null;
		}
		return updateCache(optional.get());
	}

	@Override
	public List<TypeEntity> findAll() {
		final List<TypeEntity> list = new ArrayList<>();
		typeDao.findAll().iterator().forEachRemaining(list::add);
		updateCache(list);
		return list;
	}

	@Override
	public TypeEntity getOrCreate(final String name, final int userId) {
		TypeEntity type = nameCache.get(name);
		if (type != null) {
			return type;
		}
		final Optional<TypeEntity> optType = typeDao.findByName(name);
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
		return updateCache(type);
	}

	@Override
	public TypeEntity findByName(final String name) {
		final TypeEntity type = nameCache.get(name);
		if (type != null) {
			return type;
		}
		final Optional<TypeEntity> optional = typeDao.findByName(name);
		if (!optional.isPresent()) {
			return null;
		}
		return updateCache(optional.get());
	}
}
