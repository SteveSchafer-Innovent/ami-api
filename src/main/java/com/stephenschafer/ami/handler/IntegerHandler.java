package com.stephenschafer.ami.handler;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.controller.Request;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.IntegerAttributeDao;
import com.stephenschafer.ami.jpa.IntegerAttributeEntity;

@Transactional
@Service
public class IntegerHandler extends BaseHandler {
	@Autowired
	private IntegerAttributeDao integerAttributeDao;

	@Override
	public void saveAttribute(final Request request) {
		final IntegerAttributeEntity entity = new IntegerAttributeEntity();
		entity.setAttrDefnId(request.getInteger("attrDefnId"));
		entity.setThingId(request.getInteger("thingId"));
		entity.setValue(request.getInteger("value"));
		integerAttributeDao.save(entity);
	}

	@Override
	public void saveAttributeValue(final int thingId, final int attrDefnId, final Object value) {
		final IntegerAttributeEntity entity = new IntegerAttributeEntity();
		entity.setAttrDefnId(attrDefnId);
		entity.setThingId(thingId);
		entity.setValue((Integer) value);
		integerAttributeDao.save(entity);
	}

	@Override
	public Object getAttributeValue(final int thingId, final int attrDefnId) {
		final AttributeId attributeId = new AttributeId();
		attributeId.setAttrDefnId(attrDefnId);
		attributeId.setThingId(thingId);
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

	@Override
	public String getHandlerName() {
		return "integer";
	}
}
