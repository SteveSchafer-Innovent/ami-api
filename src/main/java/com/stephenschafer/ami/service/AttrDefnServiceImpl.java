package com.stephenschafer.ami.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.AttrDefnDao;
import com.stephenschafer.ami.jpa.AttrDefnEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service(value = "attrDefnService")
public class AttrDefnServiceImpl implements AttrDefnService {
	@Autowired
	private AttrDefnDao attrDefnDao;

	@Override
	public List<AttrDefnEntity> list() {
		return attrDefnDao.findByOrderBySortOrder();
	}

	@Override
	public AttrDefnEntity findByName(final int typeId, final String name) {
		final Optional<AttrDefnEntity> optional = attrDefnDao.findByTypeIdAndName(typeId, name);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public AttrDefnEntity findById(final int id) {
		final Optional<AttrDefnEntity> optional = attrDefnDao.findById(id);
		return optional.isPresent() ? optional.get() : null;
	}

	@Override
	public List<AttrDefnEntity> findByTypeIdOrderBySortOrder(final int typeId) {
		log.info("findByTypeIdOrderBySortOrder " + typeId);
		final long startTime = System.currentTimeMillis();
		final List<AttrDefnEntity> resultList = attrDefnDao.findByTypeIdOrderBySortOrder(typeId);
		log.info("  count: " + resultList.size());
		log.info("  elapsed: " + (System.currentTimeMillis() - startTime));
		return resultList;
	}

	@Override
	public List<AttrDefnEntity> findByTypeId(final int typeId) {
		log.info("findByTypeId " + typeId);
		final long startTime = System.currentTimeMillis();
		final List<AttrDefnEntity> resultList = attrDefnDao.findByTypeId(typeId);
		log.info("  count: " + resultList.size());
		log.info("  elapsed: " + (System.currentTimeMillis() - startTime));
		return resultList;
	}

	@Override
	public List<AttrDefnEntity> findAll() {
		log.info("AttrDefnService.findAll");
		final List<AttrDefnEntity> resultList = new ArrayList<>();
		for (final AttrDefnEntity attrDefnEntity : attrDefnDao.findAll()) {
			resultList.add(attrDefnEntity);
		}
		log.info("  results: " + resultList.size());
		return resultList;
	}
}
