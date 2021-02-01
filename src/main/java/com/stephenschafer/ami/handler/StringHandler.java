package com.stephenschafer.ami.handler;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.controller.Request;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.StringAttributeDao;
import com.stephenschafer.ami.jpa.StringAttributeEntity;
import com.stephenschafer.ami.service.WordService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
public class StringHandler extends BaseHandler {
	@Autowired
	private StringAttributeDao stringAttributeDao;
	@Autowired
	private WordService wordService;

	@Override
	public void saveAttribute(final Request request) {
		final StringAttributeEntity entity = new StringAttributeEntity();
		entity.setAttrDefnId(request.getInteger("attrDefnId"));
		entity.setThingId(request.getInteger("thingId"));
		entity.setValue(request.getString("value"));
		stringAttributeDao.save(entity);
		wordService.updateIndex(entity.getThingId(), entity.getAttrDefnId());
	}

	@Override
	public void saveAttributeValue(final int thingId, final int attrDefnId, final Object value) {
		final StringAttributeEntity entity = new StringAttributeEntity();
		entity.setAttrDefnId(attrDefnId);
		entity.setThingId(thingId);
		String stringValue;
		if (value instanceof String) {
			final StringBuilder sb = new StringBuilder();
			stringValue = (String) value;
			int excludedCount = 0;
			for (int i = 0; i < stringValue.length(); i++) {
				final int codePoint = stringValue.codePointAt(i);
				if (Character.isBmpCodePoint(codePoint)) {
					sb.append((char) codePoint);
				}
				else {
					excludedCount++;
				}
			}
			if (excludedCount > 0) {
				log.info("Excluded non BMP characters from thing " + thingId + ", attr "
					+ attrDefnId + ": " + excludedCount);
				stringValue = sb.toString();
			}
		}
		else if (value == null) {
			stringValue = null;
		}
		else {
			stringValue = value.toString();
		}
		entity.setValue(stringValue);
		stringAttributeDao.save(entity);
		wordService.updateIndex(entity.getThingId(), entity.getAttrDefnId());
	}

	@Override
	public String getAttributeValue(final int thingId, final int attrDefnId) {
		final AttributeId attributeId = new AttributeId();
		attributeId.setAttrDefnId(attrDefnId);
		attributeId.setThingId(thingId);
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

	@Override
	protected Set<String> getWords(final int thingId, final int attrDefnId) {
		log.info("string getWords " + thingId + ", " + attrDefnId);
		final String value = getAttributeValue(thingId, attrDefnId);
		if (value == null) {
			log.info("  value is null");
			return new HashSet<>();
		}
		log.info("  value = " + value);
		final Set<String> words = wordService.parseWords(value);
		log.info("  words = " + words);
		return words;
	}

	@Override
	public String getHandlerName() {
		return "string";
	}

	public List<StringAttributeEntity> findByValue(final int attrDefnId, final String value) {
		return stringAttributeDao.findByAttrDefnIdAndValue(attrDefnId, value);
	}
}
