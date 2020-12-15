package com.stephenschafer.ami.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.AttrDefnDao;
import com.stephenschafer.ami.jpa.AttrDefnEntity;

@Transactional
@Service(value = "attrDefnService")
public class AttrDefnServiceImpl implements AttrDefnService {
	@Autowired
	private AttrDefnDao attrDefnDao;

	@Override
	public List<AttrDefnEntity> list(final int typeId) {
		return attrDefnDao.findByTypeIdOrderBySortOrder(typeId);
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
}
