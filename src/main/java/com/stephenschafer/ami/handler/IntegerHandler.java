package com.stephenschafer.ami.handler;

import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.IntegerAttributeDao;
import com.stephenschafer.ami.jpa.IntegerAttributeEntity;
import com.stephenschafer.ami.jpa.ThingEntity;

@Transactional
@Service
public class IntegerHandler extends BaseHandler {
	@Autowired
	private IntegerAttributeDao integerAttributeDao;

	@Override
	public void saveAttribute(final Map<String, Object> map) {
		final IntegerAttributeEntity entity = new IntegerAttributeEntity();
		entity.setAttrDefnId((Integer) map.get("attrDefnId"));
		entity.setThingId((Integer) map.get("thingId"));
		entity.setValue((Integer) map.get("value"));
		integerAttributeDao.save(entity);
	}

	@Override
	public Object getAttributeValue(final ThingEntity thing, final AttrDefnEntity attrDefn) {
		final AttributeId attributeId = new AttributeId();
		attributeId.setAttrDefnId(attrDefn.getId());
		attributeId.setThingId(thing.getId());
		final Optional<IntegerAttributeEntity> optional = integerAttributeDao.findById(attributeId);
		if (optional.isPresent()) {
			final IntegerAttributeEntity entity = optional.get();
			final Integer value = entity.getValue();
			return value;
		}
		return null;
	}

	@Override
	public void deleteAttributesByThing(final Integer thingId) {
		integerAttributeDao.deleteByThingId(thingId);
	}
}
