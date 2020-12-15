package com.stephenschafer.ami.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.jpa.AttrDefnDao;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.StringAttributeDao;
import com.stephenschafer.ami.jpa.ThingDao;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.jpa.TypeDao;
import com.stephenschafer.ami.jpa.TypeEntity;

@Transactional
@Service(value = "thingService")
public class ThingServiceImpl implements ThingService {
	@Autowired
	private ThingDao thingDao;
	@Autowired
	private StringAttributeDao attributeDao;
	@Autowired
	private AttrDefnDao attrDefnDao;
	@Autowired
	private TypeDao typeDao;
	@Autowired
	private HandlerProvider handlerProvider;

	@Override
	public ThingEntity insert(final ThingEntity thing) {
		thing.setId(null);
		return thingDao.save(thing);
	}

	@Override
	public ThingEntity update(final ThingEntity thing) {
		return thingDao.save(thing);
	}

	@Override
	public List<ThingEntity> findByTypeId(final Integer typeId) {
		return thingDao.findByTypeId(typeId);
	}

	private List<ThingEntity> findAll() {
		final List<ThingEntity> list = new ArrayList<>();
		thingDao.findAll().forEach(thing -> {
			list.add(thing);
		});
		return list;
	}

	@Override
	public void delete(final Integer thingId) {
		attributeDao.deleteByThingId(thingId);
		thingDao.deleteById(thingId);
	}

	@Override
	public ThingEntity findById(final Integer thingId) {
		final Optional<ThingEntity> optional = thingDao.findById(thingId);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	private String getPresentation(final ThingEntity thing, final boolean includeType) {
		final StringBuilder sb = new StringBuilder();
		;
		final int typeId = thing.getTypeId();
		if (includeType) {
			final Optional<TypeEntity> optional = typeDao.findById(typeId);
			if (optional.isPresent()) {
				final TypeEntity type = optional.get();
				sb.append(type.getName());
				sb.append(": ");
			}
		}
		final Optional<AttrDefnEntity> optionalAttrDefn = attrDefnDao.findByTypeIdAndName(typeId,
			"name");
		if (optionalAttrDefn.isPresent()) {
			final AttrDefnEntity attrDefn = optionalAttrDefn.get();
			final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
			final Object presObj = handler.getAttributeValue(thing, attrDefn);
			if (presObj != null) {
				sb.append(presObj.toString());
			}
			else {
				sb.append("thing ");
				sb.append(thing.getId().toString());
			}
		}
		else {
			sb.append("thing ");
			sb.append(thing.getId().toString());
		}
		return sb.toString();
	}

	private Map<String, Object> getSelectOption(final ThingEntity thing,
			final boolean includeTypeName) {
		final Map<String, Object> thingMap = new HashMap<>();
		thingMap.put("id", thing.getId());
		thingMap.put("presentation", getPresentation(thing, includeTypeName));
		return thingMap;
	}

	@Override
	public List<Map<String, Object>> getSelectOptions(final Integer typeId) {
		final List<Map<String, Object>> thingList = new ArrayList<>();
		final List<ThingEntity> things;
		if (typeId != null) {
			things = this.findByTypeId(typeId);
		}
		else {
			things = this.findAll();
		}
		for (final ThingEntity thing : things) {
			final Map<String, Object> thingMap = this.getSelectOption(thing, typeId == null);
			thingList.add(thingMap);
		}
		return thingList;
	}
}
