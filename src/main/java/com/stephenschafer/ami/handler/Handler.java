package com.stephenschafer.ami.handler;

import java.util.Map;

import com.stephenschafer.ami.controller.Request;
import com.stephenschafer.ami.jpa.AttrDefnEntity;

public interface Handler {
	int insertAttrDefn(Map<String, Object> attrDefn);

	void updateAttrDefn(Map<String, Object> attrDefn);

	void deleteAttrDefn(int id);

	void deleteAttributesByThing(Integer thingId);

	Map<String, Object> getAttrDefnMap(AttrDefnEntity entityAttrDefn);

	void saveAttribute(Request attribute);

	Object getAttributeValue(int thingId, int attrDefnId);

	void saveAttributeValue(int thingId, int attrDefnId, Object value);

	void updateIndex(int thingId, int attrDefnId);

	AttrDefnEntity getOrCreateAttrDefn(int typeId, String name, boolean multiple,
			boolean showInList, boolean editInList);

	String getHandlerName();
}
