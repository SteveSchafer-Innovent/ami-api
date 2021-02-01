package com.stephenschafer.ami.handler;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.controller.Request;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.BooleanAttributeDao;
import com.stephenschafer.ami.jpa.BooleanAttributeEntity;

@Transactional
@Service
public class BooleanHandler extends BaseHandler {
	@Autowired
	private BooleanAttributeDao booleanAttributeDao;

	@Override
	public void saveAttribute(final Request request) {
		final BooleanAttributeEntity entity = new BooleanAttributeEntity();
		entity.setAttrDefnId(request.getInteger("attrDefnId"));
		entity.setThingId(request.getInteger("thingId"));
		entity.setValue(request.getBoolean("value"));
		booleanAttributeDao.save(entity);
	}

	@Override
	public void saveAttributeValue(final int thingId, final int attrDefnId, final Object value) {
		final BooleanAttributeEntity entity = new BooleanAttributeEntity();
		entity.setAttrDefnId(attrDefnId);
		entity.setThingId(thingId);
		entity.setValue((Boolean) value);
		booleanAttributeDao.save(entity);
	}

	public void saveAttribute(final BooleanAttributeEntity entity) {
		booleanAttributeDao.save(entity);
	}

	@Override
	public Boolean getAttributeValue(final int thingId, final int attrDefnId) {
		final AttributeId attributeId = new AttributeId();
		attributeId.setAttrDefnId(attrDefnId);
		attributeId.setThingId(thingId);
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

	@Override
	public String getHandlerName() {
		return "boolean";
	}
}
