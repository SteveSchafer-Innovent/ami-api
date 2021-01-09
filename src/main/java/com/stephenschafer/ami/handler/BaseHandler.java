package com.stephenschafer.ami.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.stephenschafer.ami.jpa.AttrDefnDao;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.WordDao;
import com.stephenschafer.ami.jpa.WordEntity;
import com.stephenschafer.ami.jpa.WordThingDao;
import com.stephenschafer.ami.jpa.WordThingEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseHandler implements Handler {
	@Autowired
	private AttrDefnDao attrDefnDao;
	@Autowired
	private WordDao wordDao;
	@Autowired
	private WordThingDao wordThingDao;

	@Override
	public int insertAttrDefn(final Map<String, Object> request) {
		request.remove("id");
		final AttrDefnEntity entity = createEntityFromMap(request);
		final AttrDefnEntity result = attrDefnDao.save(entity);
		return result.getId();
	}

	@Override
	public void updateAttrDefn(final Map<String, Object> request) {
		final AttrDefnEntity entity = createEntityFromMap(request);
		attrDefnDao.save(entity);
	}

	private static AttrDefnEntity createEntityFromMap(final Map<String, Object> map) {
		final AttrDefnEntity entity = new AttrDefnEntity();
		entity.setId((Integer) map.get("id"));
		entity.setName((String) map.get("name"));
		entity.setHandler((String) map.get("handler"));
		entity.setTypeId((Integer) map.get("typeId"));
		entity.setMultiple((Boolean) map.get("multiple"));
		entity.setShowInList((Boolean) map.get("showInList"));
		entity.setEditInList((Boolean) map.get("editInList"));
		entity.setSortOrder(toFloat(map.get("order")));
		return entity;
	}

	private static Float toFloat(final Object object) {
		if (object instanceof Float) {
			return (Float) object;
		}
		if (object instanceof String) {
			return Float.valueOf((String) object);
		}
		if (object instanceof Integer) {
			return ((Integer) object).floatValue();
		}
		if (object instanceof Double) {
			return ((Double) object).floatValue();
		}
		throw new RuntimeException("Cannot convert '" + object + "' to float");
	}

	@Override
	public void deleteAttrDefn(final int id) {
		final Optional<AttrDefnEntity> optional = attrDefnDao.findById(id);
		if (optional.isPresent()) {
			attrDefnDao.deleteById(id);
		}
		else {
			log.info("Attribute definition " + id + " not deleted because it was not found");
		}
	}

	@Override
	public Map<String, Object> getAttrDefnMap(final AttrDefnEntity entity) {
		final Map<String, Object> map = new HashMap<>();
		map.put("id", entity.getId());
		map.put("name", entity.getName());
		map.put("handler", entity.getHandler());
		map.put("typeId", entity.getTypeId());
		map.put("multiple", entity.isMultiple());
		map.put("showInList", entity.isShowInList());
		map.put("editInList", entity.isEditInList());
		map.put("order", entity.getSortOrder());
		return map;
	}

	@Override
	public void updateIndex(final int thingId, final int attrDefnId) {
		wordThingDao.deleteByThingIdAndAttrdefnId(thingId, attrDefnId);
		final Set<String> words = getWords(thingId, attrDefnId);
		log.info("words = " + words);
		for (String word : words) {
			if (word.length() > 32) {
				log.info("truncating " + word);
				word = word.substring(0, 32);
			}
			WordEntity wordEntity = wordDao.findByWord(word);
			if (wordEntity == null) {
				wordEntity = new WordEntity();
				wordEntity.setWord(word);
				wordEntity = wordDao.save(wordEntity);
			}
			final WordThingEntity wordThingEntity = new WordThingEntity();
			wordThingEntity.setWordId(wordEntity.getId());
			wordThingEntity.setThingId(thingId);
			wordThingEntity.setAttrdefnId(attrDefnId);
			wordThingDao.save(wordThingEntity);
		}
	}

	protected Set<String> getWords(final int thingId, final int attrDefnId) {
		return new HashSet<>();
	}

	@Override
	public AttrDefnEntity getOrCreateAttrDefn(final int typeId, final String name,
			final boolean multiple, final boolean showInList, final boolean editInList) {
		final Optional<AttrDefnEntity> optAttrDefn = attrDefnDao.findByTypeIdAndName(typeId, name);
		if (optAttrDefn.isPresent()) {
			return optAttrDefn.get();
		}
		final AttrDefnEntity attrDefn = new AttrDefnEntity();
		attrDefn.setTypeId(typeId);
		attrDefn.setHandler(getHandlerName());
		attrDefn.setName(name);
		attrDefn.setMultiple(multiple);
		attrDefn.setShowInList(showInList);
		attrDefn.setEditInList(editInList);
		attrDefnDao.save(attrDefn);
		return attrDefn;
	}
}
