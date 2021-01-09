package com.stephenschafer.ami.handler;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.controller.Request;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.FloatAttributeDao;
import com.stephenschafer.ami.jpa.FloatAttributeEntity;

@Transactional
@Service
public class FloatHandler extends BaseHandler {
	@Autowired
	private FloatAttributeDao floatAttributeDao;

	@Override
	public void saveAttribute(final Request request) {
		final FloatAttributeEntity entity = new FloatAttributeEntity();
		entity.setAttrDefnId(request.getInteger("attrDefnId"));
		entity.setThingId(request.getInteger("thingId"));
		final Number value = request.getNumber("value");
		entity.setValue(value.doubleValue());
		floatAttributeDao.save(entity);
	}

	@Override
	public void saveAttributeValue(final int thingId, final int attrDefnId, final Object value) {
		final FloatAttributeEntity entity = new FloatAttributeEntity();
		entity.setAttrDefnId(attrDefnId);
		entity.setThingId(thingId);
		entity.setValue((Double) value);
		floatAttributeDao.save(entity);
	}

	@Override
	public Object getAttributeValue(final int thingId, final int attrDefnId) {
		final AttributeId attributeId = new AttributeId();
		attributeId.setAttrDefnId(attrDefnId);
		attributeId.setThingId(thingId);
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

	@Override
	public String getHandlerName() {
		return "float";
	}
}
