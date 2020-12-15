package com.stephenschafer.ami.handler;

import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.BooleanAttributeDao;
import com.stephenschafer.ami.jpa.BooleanAttributeEntity;
import com.stephenschafer.ami.jpa.ThingEntity;

@Transactional
@Service
public class BooleanHandler extends BaseHandler {
	@Autowired
	private BooleanAttributeDao booleanAttributeDao;

	@Override
	public void saveAttribute(final Map<String, Object> map) {
		final BooleanAttributeEntity entity = new BooleanAttributeEntity();
		entity.setAttrDefnId((Integer) map.get("attrDefnId"));
		entity.setThingId((Integer) map.get("thingId"));
		entity.setValue((Boolean) map.get("value"));
		booleanAttributeDao.save(entity);
	}

	public void saveAttribute(final BooleanAttributeEntity entity) {
		booleanAttributeDao.save(entity);
	}

	@Override
	public Object getAttributeValue(final ThingEntity thing, final AttrDefnEntity attrDefn) {
		final AttributeId attributeId = new AttributeId();
		attributeId.setAttrDefnId(attrDefn.getId());
		attributeId.setThingId(thing.getId());
		final Optional<BooleanAttributeEntity> optional = booleanAttributeDao.findById(attributeId);
		if (optional.isPresent()) {
			final BooleanAttributeEntity entity = optional.get();
			final Boolean value = entity.getValue();
			return value;
		}
		return null;
	}

	@Override
	public void deleteAttributesByThing(final Integer thingId) {
		if (thingId == null) {
			throw new NullPointerException("thingId");
		}
		booleanAttributeDao.deleteByThingId(thingId);
	}
}
