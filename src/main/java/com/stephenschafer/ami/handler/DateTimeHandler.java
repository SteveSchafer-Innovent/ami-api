package com.stephenschafer.ami.handler;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.DateTimeAttributeDao;
import com.stephenschafer.ami.jpa.DateTimeAttributeEntity;
import com.stephenschafer.ami.jpa.ThingEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
public class DateTimeHandler extends BaseHandler {
	// 2020-11-23T17:11:32.000Z
	private static final DateFormat[] DFS = { new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX"),
		new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"), new SimpleDateFormat("MM/dd/yyyy HH:mm"),
		new SimpleDateFormat("MM/dd/yyyy"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
		new SimpleDateFormat("yyyy-MM-dd HH:mm"), new SimpleDateFormat("yyyy-MM-dd") };
	@Autowired
	private DateTimeAttributeDao datetimeAttributeDao;

	@Override
	public void saveAttribute(final Map<String, Object> map) {
		final DateTimeAttributeEntity entity = new DateTimeAttributeEntity();
		entity.setAttrDefnId((Integer) map.get("attrDefnId"));
		entity.setThingId((Integer) map.get("thingId"));
		entity.setValue(toTimestamp(map.get("value")));
		datetimeAttributeDao.save(entity);
	}

	private Timestamp toTimestamp(final Object object) {
		if (object instanceof Timestamp) {
			return (Timestamp) object;
		}
		if (object instanceof Date) {
			return new Timestamp(((Date) object).getTime());
		}
		if (object instanceof String) {
			ParseException exception = null;
			for (int i = 0; i < DFS.length; i++) {
				final DateFormat df = DFS[i];
				try {
					final Timestamp timestamp = new Timestamp(df.parse((String) object).getTime());
					log.info("index = " + i + ", timestamp = " + timestamp);
					return timestamp;
				}
				catch (final ParseException e) {
					exception = e;
				}
			}
			if (exception != null) {
				throw new RuntimeException("Failed to parse '" + object + "' to a date", exception);
			}
		}
		if (object instanceof Long) {
			return new Timestamp(((Long) object).longValue());
		}
		throw new RuntimeException("Failed to convert '" + object + "' to a date");
	}

	@Override
	public Object getAttributeValue(final ThingEntity thing, final AttrDefnEntity attrDefn) {
		final AttributeId attributeId = new AttributeId();
		attributeId.setAttrDefnId(attrDefn.getId());
		attributeId.setThingId(thing.getId());
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
}
