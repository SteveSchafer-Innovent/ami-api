package com.stephenschafer.ami.handler;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.controller.Request;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.DateTimeAttributeDao;
import com.stephenschafer.ami.jpa.DateTimeAttributeEntity;

@Transactional
@Service
public class DateTimeHandler extends BaseHandler {
	@Autowired
	private DateTimeAttributeDao datetimeAttributeDao;

	@Override
	public void saveAttribute(final Request request) {
		final DateTimeAttributeEntity entity = new DateTimeAttributeEntity();
		entity.setAttrDefnId(request.getInteger("attrDefnId"));
		entity.setThingId(request.getInteger("thingId"));
		entity.setValue(request.getDate("value"));
		datetimeAttributeDao.save(entity);
	}

	@Override
	public void saveAttributeValue(final int thingId, final int attrDefnId, final Object value) {
		final DateTimeAttributeEntity entity = new DateTimeAttributeEntity();
		entity.setAttrDefnId(attrDefnId);
		entity.setThingId(thingId);
		Timestamp timestamp;
		if (value instanceof Date) {
			final Date date = (Date) value;
			timestamp = new Timestamp(date.getTime());
		}
		else if (value instanceof Timestamp) {
			timestamp = (Timestamp) value;
		}
		else {
			throw new ClassCastException("Expecting either Date or Timestamp but instead got "
				+ (value == null ? "null" : value.getClass().getName()));
		}
		entity.setValue(timestamp);
		datetimeAttributeDao.save(entity);
	}

	@Override
	public Object getAttributeValue(final int thingId, final int attrDefnId) {
		final AttributeId attributeId = new AttributeId();
		attributeId.setAttrDefnId(attrDefnId);
		attributeId.setThingId(thingId);
		final Optional<DateTimeAttributeEntity> optional = datetimeAttributeDao.findById(
			attributeId);
		if (optional.isPresent()) {
			final DateTimeAttributeEntity entity = optional.get();
			return entity.getValue();
		}
		return null;
	}

	@Override
	public void deleteAttributesByThing(final Integer thingId) {
		datetimeAttributeDao.deleteByThingId(thingId);
	}

	@Override
	public String getHandlerName() {
		return "datetime";
	}
}
