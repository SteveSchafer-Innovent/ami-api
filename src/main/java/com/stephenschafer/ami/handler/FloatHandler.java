package com.stephenschafer.ami.handler;

import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.FloatAttributeDao;
import com.stephenschafer.ami.jpa.FloatAttributeEntity;
import com.stephenschafer.ami.jpa.ThingEntity;

@Transactional
@Service
public class FloatHandler extends BaseHandler {
	@Autowired
	private FloatAttributeDao floatAttributeDao;

	@Override
	public void saveAttribute(final Map<String, Object> map) {
		final FloatAttributeEntity entity = new FloatAttributeEntity();
		entity.setAttrDefnId((Integer) map.get("attrDefnId"));
		entity.setThingId((Integer) map.get("thingId"));
		final Number value = (Number) map.get("value");
		entity.setValue(value.doubleValue());
		floatAttributeDao.save(entity);
	}

	@Override
	public Object getAttributeValue(final ThingEntity thing, final AttrDefnEntity attrDefn) {
		final AttributeId attributeId = new AttributeId();
		attributeId.setAttrDefnId(attrDefn.getId());
		attributeId.setThingId(thing.getId());
		final Optional<FloatAttributeEntity> optional = floatAttributeDao.findById(attributeId);
		if (optional.isPresent()) {
			final FloatAttributeEntity entity = optional.get();
			final Double value = entity.getValue();
			return value;
		}
		return null;
	}

	@Override
	public void deleteAttributesByThing(final Integer thingId) {
		floatAttributeDao.deleteByThingId(thingId);
	}
}
