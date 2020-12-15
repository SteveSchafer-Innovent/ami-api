package com.stephenschafer.ami.handler;

import java.util.Map;

import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.ThingEntity;

public interface Handler {
	int insertAttrDefn(Map<String, Object> attrDefn);

	void updateAttrDefn(Map<String, Object> attrDefn);

	void deleteAttrDefn(int id);

	void deleteAttributesByThing(Integer thingId);

	Map<String, Object> getAttrDefnMap(AttrDefnEntity entityAttrDefn);

	void saveAttribute(Map<String, Object> attribute);

	Object getAttributeValue(ThingEntity thing, AttrDefnEntity attrDefn);
}
