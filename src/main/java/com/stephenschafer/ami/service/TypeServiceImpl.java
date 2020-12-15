package com.stephenschafer.ami.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.FindTypeResult;
import com.stephenschafer.ami.jpa.TypeDao;
import com.stephenschafer.ami.jpa.TypeEntity;

@Transactional
@Service(value = "typeService")
public class TypeServiceImpl implements TypeService {
	@Autowired
	private TypeDao typeDao;
	@Autowired
	private AttrDefnService attrDefnService;
	@Autowired
	private HandlerProvider handlerProvider;

	@Override
	public TypeEntity insert(final TypeEntity type) {
		final TypeEntity newType = new TypeEntity();
		newType.setName(type.getName());
		return typeDao.save(newType);
	}

	@Override
	public TypeEntity update(final TypeEntity type) {
		final FindTypeResult newType = findById(type.getId());
		if (newType != null) {
			BeanUtils.copyProperties(type, newType);
			typeDao.save(newType.getTypeEntity());
		}
		return type;
	}

	@Override
	public void delete(final int id) {
		typeDao.deleteById(id);
	}

	@Override
	public FindTypeResult findById(final int id) {
		final Optional<TypeEntity> optional = typeDao.findById(id);
		if (!optional.isPresent()) {
			return null;
		}
		final TypeEntity typeEntity = optional.get();
		final List<Map<String, Object>> attrdefns = new ArrayList<>();
		final List<AttrDefnEntity> list = attrDefnService.list(typeEntity.getId());
		for (final AttrDefnEntity entity : list) {
			final Handler handler = handlerProvider.getHandler(entity.getHandler());
			attrdefns.add(handler.getAttrDefnMap(entity));
		}
		return new FindTypeResult(typeEntity.getId(), typeEntity.getName(), attrdefns);
	}

	@Override
	public List<TypeEntity> findAll() {
		final List<TypeEntity> list = new ArrayList<>();
		typeDao.findAll().iterator().forEachRemaining(list::add);
		return list;
	}
}
