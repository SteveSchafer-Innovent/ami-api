package com.stephenschafer.ami.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.controller.FindThingResult;
import com.stephenschafer.ami.controller.User;
import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.handler.LinkHandler;
import com.stephenschafer.ami.jpa.AttrDefnDao;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.FindTypeResult;
import com.stephenschafer.ami.jpa.LinkAttributeEntity;
import com.stephenschafer.ami.jpa.StringAttributeDao;
import com.stephenschafer.ami.jpa.ThingDao;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.jpa.TypeDao;
import com.stephenschafer.ami.jpa.TypeEntity;
import com.stephenschafer.ami.jpa.UserTypeContextThingDao;
import com.stephenschafer.ami.jpa.UserTypeContextThingEntity;
import com.stephenschafer.ami.jpa.UserTypeThingDao;
import com.stephenschafer.ami.jpa.UserTypeThingEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	private UserTypeThingDao userTypeThingDao;
	@Autowired
	private UserTypeContextThingDao userTypeContextThingDao;
	@Autowired
	private HandlerProvider handlerProvider;
	@Autowired
	private WordService wordService;
	@Autowired
	private TypeService typeService;
	@Autowired
	private AttrDefnService attrDefnService;
	@Autowired
	private LinkHandler linkHandler;
	@Autowired
	private UserService userService;

	@Override
	public ThingEntity insert(final ThingEntity thing) {
		thing.setId(null);
		return update(thing);
	}

	@Override
	public ThingEntity update(final ThingEntity thing) {
		final ThingEntity entity = thingDao.save(thing);
		wordService.updateIndex(entity);
		return entity;
	}

	@Override
	public List<ThingEntity> findByTypeId(final int typeId) {
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
	public void delete(final int thingId) {
		attributeDao.deleteByThingId(thingId);
		thingDao.deleteById(thingId);
	}

	@Override
	public ThingEntity findById(final int thingId) {
		final Optional<ThingEntity> optional = thingDao.findById(thingId);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	private String getPresentation(final ThingEntity thing, final boolean includeType) {
		final StringBuilder sb = new StringBuilder();
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
	public List<Map<String, Object>> getSelectOptions() {
		final List<Map<String, Object>> thingList = new ArrayList<>();
		final List<ThingEntity> things = this.findAll();
		for (final ThingEntity thing : things) {
			final Map<String, Object> thingMap = this.getSelectOption(thing, true);
			thingList.add(thingMap);
		}
		return thingList;
	}

	@Override
	public List<Map<String, Object>> getSelectOptions(final int typeId) {
		final List<Map<String, Object>> thingList = new ArrayList<>();
		final List<ThingEntity> things = this.findByTypeId(typeId);
		for (final ThingEntity thing : things) {
			final Map<String, Object> thingMap = this.getSelectOption(thing, false);
			thingList.add(thingMap);
		}
		return thingList;
	}

	@Override
	public void updateThingOrder(final int userId, final int typeId, final List<Integer> thingIds) {
		userTypeThingDao.deleteByUserIdAndTypeId(userId, typeId);
		int sortOrder = 0;
		for (final Integer thingId : thingIds) {
			final UserTypeThingEntity entity = new UserTypeThingEntity(userId, typeId, thingId,
					sortOrder++);
			userTypeThingDao.save(entity);
		}
	}

	@Override
	public void updateThingOrder(final int userId, final int typeId, final int contextThingId,
			final List<Integer> thingIds) {
		userTypeContextThingDao.deleteByUserIdAndTypeIdAndContextThingId(userId, typeId,
			contextThingId);
		int sortOrder = 0;
		for (final Integer thingId : thingIds) {
			final UserTypeContextThingEntity entity = new UserTypeContextThingEntity(userId, typeId,
					contextThingId, thingId, sortOrder++);
			userTypeContextThingDao.save(entity);
		}
	}

	@Override
	public List<Integer> getThingOrder(final int userId, final int typeId) {
		log.info("getThingOrder userId = " + userId + ", typeId = " + typeId);
		final List<UserTypeThingEntity> list = userTypeThingDao.findByUserIdAndTypeId(userId,
			typeId);
		Collections.sort(list, new Comparator<UserTypeThingEntity>() {
			@Override
			public int compare(final UserTypeThingEntity o1, final UserTypeThingEntity o2) {
				final Integer so1 = o1.getSortOrder();
				final Integer so2 = o2.getSortOrder();
				return so1.compareTo(so2);
			}
		});
		log.info("getThingOrder " + list);
		final List<Integer> result = new ArrayList<>();
		for (final UserTypeThingEntity entity : list) {
			result.add(entity.getThingId());
		}
		return result;
	}

	@Override
	public List<Integer> getThingOrder(final int userId, final int typeId,
			final int contextThingId) {
		log.info("getThingOrder userId = " + userId + ", typeId = " + typeId + ", contextThingId = "
			+ contextThingId);
		final List<UserTypeContextThingEntity> list = userTypeContextThingDao.findByUserIdAndTypeIdAndContextThingId(
			userId, typeId, contextThingId);
		Collections.sort(list, new Comparator<UserTypeContextThingEntity>() {
			@Override
			public int compare(final UserTypeContextThingEntity o1,
					final UserTypeContextThingEntity o2) {
				final Integer so1 = o1.getSortOrder();
				final Integer so2 = o2.getSortOrder();
				return so1.compareTo(so2);
			}
		});
		log.info("getThingOrder " + list);
		final List<Integer> result = new ArrayList<>();
		for (final UserTypeContextThingEntity entity : list) {
			result.add(entity.getThingId());
		}
		return result;
	}

	@Override
	public FindThingResult getFindThingResult(final ThingEntity thing) {
		if (thing == null) {
			return null;
		}
		final FindThingResult result = new FindThingResult();
		result.setId(thing.getId());
		result.setCreated(thing.getCreated());
		result.setCreator(new User(userService.findById(thing.getCreator())));
		final int typeId = thing.getTypeId();
		final FindTypeResult type = typeService.findById(typeId);
		result.setType(type);
		final List<AttrDefnEntity> attrDefns = attrDefnService.list(typeId);
		final Map<String, Object> attrMap = new HashMap<>();
		for (final AttrDefnEntity attrDefn : attrDefns) {
			final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
			final Map<String, Object> attrDefnMap = handler.getAttrDefnMap(attrDefn);
			attrDefnMap.put("value", handler.getAttributeValue(thing, attrDefn));
			attrMap.put(attrDefn.getName(), attrDefnMap);
		}
		result.setAttributes(attrMap);
		final List<LinkAttributeEntity> sourceLinks = linkHandler.findByTargetThingId(
			thing.getId());
		final Map<Integer, Set<Integer>> links = new HashMap<>();
		for (final LinkAttributeEntity linkAttribute : sourceLinks) {
			final int attrDefnId = linkAttribute.getAttributeDefnId();
			Set<Integer> thingIds = links.get(attrDefnId);
			if (thingIds == null) {
				thingIds = new HashSet<>();
				links.put(attrDefnId, thingIds);
			}
			thingIds.add(linkAttribute.getThingId());
		}
		result.setLinks(links);
		return result;
	}
}
