package com.stephenschafer.ami.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.stephenschafer.ami.controller.FileInfo;
import com.stephenschafer.ami.jpa.ThingEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public interface ThingService {
	ThingEntity insert(ThingEntity thing);

	ThingEntity update(ThingEntity thing);

	void delete(int thingId);

	ThingEntity findById(int thingId);

	String getName(ThingEntity thing);

	@Getter
	@Setter
	@ToString
	public static class MimeType {
		private final String name;
		private Map<String, String> attributes = new HashMap<>();

		public MimeType(final String mimeType) {
			final String[] parts = mimeType.split("; *");
			name = parts[0].toLowerCase().trim();
			for (int i = 1; i < parts.length; i++) {
				final String part = parts[i];
				final int indexOfEquals = part.indexOf("=");
				final String key;
				final String value;
				if (indexOfEquals >= 0) {
					key = part.substring(0, indexOfEquals);
					value = part.substring(indexOfEquals + 1);
				}
				else {
					key = part;
					value = null;
				}
				attributes.put(key, value);
			}
		}

		public boolean isHtml() {
			return "text/html".equals(this.name);
		}

		public boolean isPlainText() {
			return "text/plain".equals(this.name);
		}
	}

	MimeType getMimeType(ThingEntity thing);

	MimeType getMimeType(String string);

	String getPresentation(ThingEntity thing, boolean includeType);

	Integer getParentId(ThingEntity thing);

	List<ThingEntity> findByTypeId(int typeId);

	List<Map<String, Object>> getSelectOptions();

	List<Map<String, Object>> getSelectOptions(int typeId);

	void updateThingOrder(int userId, int typeId, List<Integer> thingIds);

	void updateThingOrder(int userId, int typeId, int contextThingId, List<Integer> thingIds);

	List<Integer> getThingOrder(int userId, int typeId);

	List<Integer> getThingOrder(int userId, int typeId, int contextThingId);

	Map<Integer, Set<Integer>> getSourceLinks(int thingId);

	Set<Integer> getSourceLinks(int thingId, int attrDefnId);

	String getAttributeStringValue(ThingEntity thing, String attrName)
			throws AttributeNotFoundException;

	Map<String, Object> getAttributeValues(ThingEntity thing);

	FileInfo saveFile(byte[] bytes, String filename, String mimeType, int thingId, int attrId)
			throws IOException;

	Iterable<ThingEntity> findAll();

	ThingEntity save(ThingEntity thing);

	String getText(ThingEntity thing);
}
