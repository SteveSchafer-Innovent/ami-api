package com.stephenschafer.ami.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.AttrDefnDao;
import com.stephenschafer.ami.jpa.AttrDefnEntity;

// @Slf4j
@Transactional
@Service(value = "attrDefnService")
public class AttrDefnServiceImpl implements AttrDefnService {
	@Autowired
	private AttrDefnDao attrDefnDao;
	private final Map<Integer, Map<String, AttrDefnEntity>> nameCache = new HashMap<>();
	private final Map<Integer, AttrDefnEntity> idCache = new HashMap<>();

	private Map<String, AttrDefnEntity> getSubCache(final int typeId) {
		Map<String, AttrDefnEntity> subCache = nameCache.get(typeId);
		if (subCache == null) {
			subCache = new HashMap<>();
			nameCache.put(typeId, subCache);
		}
		return subCache;
	}

	private AttrDefnEntity updateCache(final AttrDefnEntity entity) {
		if (entity != null) {
			idCache.put(entity.getId(), entity);
			final Map<String, AttrDefnEntity> subCache = getSubCache(entity.getTypeId());
			subCache.put(entity.getName(), entity);
		}
		return entity;
	}

	private void removeFromCache(final int id) {
		final AttrDefnEntity entity = idCache.get(id);
		if (entity != null) {
			idCache.remove(id);
			final Map<String, AttrDefnEntity> subCache = nameCache.get(id);
			if (subCache != null) {
				subCache.remove(entity.getName());
			}
		}
	}

	private List<AttrDefnEntity> updateCache(final List<AttrDefnEntity> entities) {
		for (final AttrDefnEntity entity : entities) {
			updateCache(entity);
		}
		return entities;
	}

	@Override
	public List<AttrDefnEntity> list() {
		return updateCache(attrDefnDao.findByOrderBySortOrder());
	}

	@Override
	public AttrDefnEntity findByName(final int typeId, final String name) {
		final Map<String, AttrDefnEntity> subCache = getSubCache(typeId);
		final AttrDefnEntity entity = subCache.get(name);
		if (entity != null) {
			return entity;
		}
		final Optional<AttrDefnEntity> optional = attrDefnDao.findByTypeIdAndName(typeId, name);
		if (optional.isPresent()) {
			return updateCache(optional.get());
		}
		return null;
	}

	@Override
	public AttrDefnEntity findById(final int id) {
		final AttrDefnEntity entity = idCache.get(id);
		if (entity != null) {
			return entity;
		}
		final Optional<AttrDefnEntity> optional = attrDefnDao.findById(id);
		if (optional.isPresent()) {
			return updateCache(optional.get());
		}
		return null;
	}

	@Override
	public List<AttrDefnEntity> findByTypeIdOrderBySortOrder(final int typeId) {
		final List<AttrDefnEntity> resultList = attrDefnDao.findByTypeIdOrderBySortOrder(typeId);
		return updateCache(resultList);
	}

	@Override
	public List<AttrDefnEntity> findByTypeId(final int typeId) {
		final List<AttrDefnEntity> resultList = attrDefnDao.findByTypeId(typeId);
		return updateCache(resultList);
	}

	@Override
	public List<AttrDefnEntity> findAll() {
		final List<AttrDefnEntity> resultList = new ArrayList<>();
		for (final AttrDefnEntity attrDefnEntity : attrDefnDao.findAll()) {
			resultList.add(attrDefnEntity);
		}
		return updateCache(resultList);
	}

	@Override
	public AttrDefnEntity save(final AttrDefnEntity entity) {
		return attrDefnDao.save(updateCache(entity));
	}

	@Override
	public void deleteById(final int id) {
		attrDefnDao.deleteById(id);
		removeFromCache(id);
	}
}
