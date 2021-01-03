package com.stephenschafer.ami.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.AttrDefnDao;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.LinkAttributeDao;
import com.stephenschafer.ami.jpa.LinkAttributeEntity;
import com.stephenschafer.ami.jpa.LinkDefnDao;
import com.stephenschafer.ami.jpa.LinkDefnEntity;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.service.ThingService;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class LinkHandler extends BaseHandler {
	@Autowired
	private LinkDefnDao linkDefnDao;
	@Autowired
	private LinkAttributeDao linkAttributeDao;
	@Autowired
	private AttrDefnDao attrDefnDao;
	@Autowired
	private ThingService thingService;

	@Override
	public int insertAttrDefn(final Map<String, Object> request) {
		log.info("insert " + request);
		final int id = super.insertAttrDefn(request);
		final Integer targetTypeId = (Integer) request.get("targetTypeId");
		// if there is no target type id then the link can point to any type
		if (targetTypeId != null && targetTypeId.intValue() != 0) {
			final LinkDefnEntity entity = new LinkDefnEntity();
			entity.setAttributeDefnId(Integer.valueOf(id));
			entity.setTargetTypeId(targetTypeId);
			linkDefnDao.save(entity);
		}
		else {
			final Optional<LinkDefnEntity> optional = linkDefnDao.findById(id);
			if (optional.isPresent()) {
				linkDefnDao.deleteById(id);
			}
		}
		return id;
	}

	@Override
	public void updateAttrDefn(final Map<String, Object> request) {
		super.updateAttrDefn(request);
		final Integer targetTypeId = (Integer) request.get("targetTypeId");
		final Integer id = (Integer) request.get("id");
		if (targetTypeId != null && targetTypeId.intValue() != 0) {
			final LinkDefnEntity entity = new LinkDefnEntity();
			entity.setAttributeDefnId(id);
			entity.setTargetTypeId(targetTypeId);
			linkDefnDao.save(entity);
		}
		else {
			final Optional<LinkDefnEntity> optional = linkDefnDao.findById(id);
			if (optional.isPresent()) {
				linkDefnDao.deleteById(id);
			}
		}
	}

	@Override
	public void deleteAttrDefn(final int id) {
		final Optional<LinkDefnEntity> optional = linkDefnDao.findById(id);
		if (optional.isPresent()) {
			linkDefnDao.deleteById(id);
		}
		else {
			log.info("Link definition " + id + " not deleted because it was not found");
		}
		super.deleteAttrDefn(id);
	}

	@Override
	public Map<String, Object> getAttrDefnMap(final AttrDefnEntity entity) {
		final Map<String, Object> map = super.getAttrDefnMap(entity);
		final Optional<LinkDefnEntity> optionalLinkDefn = linkDefnDao.findById(entity.getId());
		final Integer targetTypeId;
		if (optionalLinkDefn.isPresent()) {
			final LinkDefnEntity linkDefn = optionalLinkDefn.get();
			targetTypeId = linkDefn.getTargetTypeId(); // could be null
		}
		else {
			targetTypeId = null;
		}
		final List<Map<String, Object>> thingList;
		if (targetTypeId != null) {
			map.put("targetTypeId", targetTypeId);
			thingList = thingService.getSelectOptions(targetTypeId);
		}
		else {
			thingList = thingService.getSelectOptions();
		}
		map.put("things", thingList);
		return map;
	}

	@Override
	public void saveAttribute(final Map<String, Object> map) {
		log.info("LinkHandler.saveAttribute " + map);
		final Integer thingId = (Integer) map.get("thingId");
		final Integer attrDefnId = (Integer) map.get("attrDefnId");
		linkAttributeDao.deleteByThingIdAndAttributeDefnId(thingId, attrDefnId);
		@SuppressWarnings("unchecked")
		final List<Integer> targetThingIds = (List<Integer>) map.get("value");
		for (final Integer targetThingId : targetThingIds) {
			if (targetThingId.intValue() > 0) {
				final LinkAttributeEntity entity = new LinkAttributeEntity();
				entity.setAttributeDefnId(attrDefnId);
				entity.setThingId(thingId);
				entity.setTargetThingId(targetThingId);
				linkAttributeDao.save(entity);
			}
		}
	}

	@Override
	public Object getAttributeValue(final ThingEntity thing, final AttrDefnEntity attrDefn) {
		final List<LinkAttributeEntity> list = linkAttributeDao.findByThingIdAndAttributeDefnId(
			thing.getId(), attrDefn.getId());
		final List<Integer> result = new ArrayList<>();
		list.forEach(entity -> {
			result.add(entity.getTargetThingId());
		});
		return result;
	}

	@Override
	public void deleteAttributesByThing(final Integer thingId) {
		linkAttributeDao.deleteByThingId(thingId);
	}

	public List<LinkAttributeEntity> findByThingId(final Integer thingId) {
		return linkAttributeDao.findByThingId(thingId);
	}

	public List<LinkAttributeEntity> findByTargetThingId(final Integer thingId) {
		return linkAttributeDao.findByTargetThingId(thingId);
	}

	public List<AttrDefnEntity> findByTargetTypeId(final Integer typeId) {
		final List<AttrDefnEntity> result = new ArrayList<>();
		final List<LinkDefnEntity> list;
		if (typeId == null) {
			list = linkDefnDao.findByTargetTypeIdIsNull();
		}
		else {
			list = linkDefnDao.findByTargetTypeId(typeId);
		}
		for (final LinkDefnEntity linkDefn : list) {
			final int attrDefnId = linkDefn.getAttributeDefnId();
			final Optional<AttrDefnEntity> optional = attrDefnDao.findById(attrDefnId);
			if (optional.isPresent()) {
				final AttrDefnEntity attrDefn = optional.get();
				result.add(attrDefn);
			}
		}
		return result;
	}
}
