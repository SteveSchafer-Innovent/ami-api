package com.stephenschafer.ami.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.controller.Request;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.LinkAttributeDao;
import com.stephenschafer.ami.jpa.LinkAttributeEntity;
import com.stephenschafer.ami.jpa.LinkDefnDao;
import com.stephenschafer.ami.jpa.LinkDefnEntity;
import com.stephenschafer.ami.service.AttrDefnService;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
	private AttrDefnService attrDefnService;

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
	public void saveAttribute(final Request request) {
		log.info("LinkHandler.saveAttribute " + request);
		final Integer thingId = request.getInteger("thingId");
		final Integer attrDefnId = request.getInteger("attrDefnId");
		final List<Integer> targetThingIds = request.getListOfInteger("value");
		saveAttributeValue(thingId, attrDefnId, targetThingIds);
	}

	@Override
	public void saveAttributeValue(final int thingId, final int attrDefnId, final Object value) {
		log.info("LinkHandler.saveAttribute " + thingId + ", " + attrDefnId + ", " + value);
		linkAttributeDao.deleteByThingIdAndAttributeDefnId(thingId, attrDefnId);
		List<Integer> targetThingIds;
		if (value instanceof Set) {
			@SuppressWarnings("unchecked")
			final Set<Integer> set = (Set<Integer>) value;
			targetThingIds = new ArrayList<>(set);
		}
		else if (value instanceof List) {
			@SuppressWarnings("unchecked")
			final List<Integer> list = (List<Integer>) value;
			targetThingIds = list;
		}
		else if (value instanceof Integer) {
			targetThingIds = new ArrayList<>();
			targetThingIds.add((Integer) value);
		}
		else {
			throw new ClassCastException("Expecting Integer, List<Integer>, or Set<Integer>");
		}
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

	@Getter
	@Setter
	@ToString
	public static class Links extends ArrayList<Integer> implements Comparable<Links> {
		private static final long serialVersionUID = 1L;
		private final List<Integer> thingIds = new ArrayList<>();

		@Override
		public int compareTo(final Links that) {
			final Iterator<Integer> thisIter = this.iterator();
			final Iterator<Integer> thatIter = that.iterator();
			while (thisIter.hasNext()) {
				if (!thatIter.hasNext()) {
					return 1;
				}
				final Integer thisThingId = thisIter.next();
				final Integer thatThingId = thatIter.next();
				final int comparison = thisThingId == null ? thatThingId == null ? 0 : -1
					: thatThingId == null ? 1 : thisThingId.compareTo(thatThingId);
				if (comparison != 0) {
					return comparison;
				}
			}
			if (thatIter.hasNext()) {
				return -1;
			}
			return 0;
		}
	}

	@Override
	public Links getAttributeValue(final int thingId, final int attrDefnId) {
		final List<LinkAttributeEntity> list = linkAttributeDao.findByThingIdAndAttributeDefnId(
			thingId, attrDefnId);
		final Links result = new Links();
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

	public List<LinkAttributeEntity> findByTargetThingIdAndAttributeDefnId(final Integer thingId,
			final Integer attrDefnId) {
		return linkAttributeDao.findByTargetThingIdAndAttributeDefnId(thingId, attrDefnId);
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
			final AttrDefnEntity attrDefn = attrDefnService.findById(attrDefnId);
			if (attrDefn != null) {
				result.add(attrDefn);
			}
		}
		return result;
	}

	@Override
	public String getHandlerName() {
		return "link";
	}

	@Override
	public boolean isSortable() {
		return false;
	}
}
