package com.stephenschafer.ami.handler;

import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.StringAttributeDao;
import com.stephenschafer.ami.jpa.StringAttributeEntity;
import com.stephenschafer.ami.jpa.ThingEntity;

@Transactional
@Service
public class RichTextHandler extends BaseHandler {
	@Autowired
	private StringAttributeDao stringAttributeDao;

	@Override
	public void saveAttribute(final Map<String, Object> map) {
		final StringAttributeEntity entity = new StringAttributeEntity();
		entity.setAttrDefnId((Integer) map.get("attrDefnId"));
		entity.setThingId((Integer) map.get("thingId"));
		entity.setValue((String) map.get("value"));
		stringAttributeDao.save(entity);
	}

	@Override
	public Object getAttributeValue(final ThingEntity thing, final AttrDefnEntity attrDefn) {
		final AttributeId attributeId = new AttributeId();
		attributeId.setAttrDefnId(attrDefn.getId());
		attributeId.setThingId(thing.getId());
		final Optional<StringAttributeEntity> optional = stringAttributeDao.findById(attributeId);
		if (optional.isPresent()) {
			final StringAttributeEntity entity = optional.get();
			final String value = entity.getValue();
			return value;
		}
		return null;
	}

	@Override
	public void deleteAttributesByThing(final Integer thingId) {
		stringAttributeDao.deleteByThingId(thingId);
	}
}
