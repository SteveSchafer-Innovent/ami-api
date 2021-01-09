package com.stephenschafer.ami.controller;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Request {
	// 2020-11-23T17:11:32.000Z
	private static final DateFormat[] DFS = { new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX"),
		new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"), new SimpleDateFormat("MM/dd/yyyy HH:mm"),
		new SimpleDateFormat("MM/dd/yyyy"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
		new SimpleDateFormat("yyyy-MM-dd HH:mm"), new SimpleDateFormat("yyyy-MM-dd") };
	private final Map<String, Object> map;

	public Request(final Map<String, Object> map) {
		this.map = map;
	}

	public String getString(final String key) {
		final String value = getString(key, null);
		if (value == null) {
			throw new RuntimeException(key + " not found");
		}
		return value;
	}

	public String getString(final String key, final String defaultValue) {
		final Object object = map.get(key);
		if (object == null) {
			return defaultValue;
		}
		if (!(object instanceof String)) {
			throw new RuntimeException(key + " is not a string");
		}
		return (String) object;
	}

	public Integer getInteger(final String key) {
		final Integer value = getInteger(key, null);
		if (value == null) {
			throw new RuntimeException(key + " not found");
		}
		return value;
	}

	public Integer getInteger(final String key, final Integer defaultValue) {
		final Object object = map.get(key);
		if (object == null) {
			return defaultValue;
		}
		if (!(object instanceof Integer)) {
			throw new RuntimeException(key + " not found or is not an integer");
		}
		return (Integer) object;
	}

	public Number getNumber(final String key) {
		final Number value = getNumber(key, null);
		if (value == null) {
			throw new RuntimeException(key + " not found");
		}
		return value;
	}

	public Number getNumber(final String key, final Number defaultValue) {
		final Object object = map.get(key);
		if (object == null) {
			return defaultValue;
		}
		if (!(object instanceof Number)) {
			throw new RuntimeException(key + " not found or is not a number");
		}
		return (Number) object;
	}

	public Boolean getBoolean(final String key) {
		final Boolean value = getBoolean(key, null);
		if (value == null) {
			throw new RuntimeException(key + " not found");
		}
		return value;
	}

	public Boolean getBoolean(final String key, final Boolean defaultValue) {
		final Object object = map.get(key);
		if (object == null) {
			return defaultValue;
		}
		if (!(object instanceof Boolean)) {
			throw new RuntimeException(key + " not found or is not a boolean");
		}
		return (Boolean) object;
	}

	public Map<String, Object> getMap(final String key) {
		final Map<String, Object> value = getMap(key, null);
		if (value == null) {
			throw new RuntimeException(key + " not found");
		}
		return value;
	}

	public byte[] getBytes(final String key) {
		final byte[] value = getBytes(key, null);
		if (value == null) {
			throw new RuntimeException(key + " not found");
		}
		return value;
	}

	public byte[] getBytes(final String key, final byte[] defaultValue) {
		final Object object = map.get(key);
		if (object == null) {
			return defaultValue;
		}
		if (!(object instanceof byte[])) {
			throw new RuntimeException(key + " not found or is not an array of bytes");
		}
		return (byte[]) object;
	}

	public Request getRequest(final String key) {
		final Map<String, Object> value = getMap(key, null);
		if (value == null) {
			throw new RuntimeException(key + " not found");
		}
		return new Request(value);
	}

	public Map<String, Object> getMap(final String key, final Map<String, Object> defaultValue) {
		final Object object = map.get(key);
		if (object == null) {
			return defaultValue;
		}
		if (!(object instanceof Map)) {
			throw new RuntimeException(key + " not found or is not an object");
		}
		@SuppressWarnings("unchecked")
		final Map<String, Object> map = (Map<String, Object>) object;
		return map;
	}

	public Set<Integer> getSetOfInteger(final String key) {
		final Set<Integer> value = getSetOfInteger(key, null);
		if (value == null) {
			throw new RuntimeException(key + " not found");
		}
		return value;
	}

	public Set<Integer> getSetOfInteger(final String key, final Set<Integer> defaultValue) {
		final Object object = map.get(key);
		if (object == null) {
			return defaultValue;
		}
		if (!(object instanceof Set)) {
			throw new RuntimeException(key + " not found or is not an array");
		}
		@SuppressWarnings("unchecked")
		final Set<Integer> map = (Set<Integer>) object;
		return map;
	}

	public List<Map<String, Object>> getListOfMap(final String key) {
		final List<Map<String, Object>> value = getListOfMap(key, null);
		if (value == null) {
			throw new RuntimeException(key + " not found");
		}
		return value;
	}

	public List<Map<String, Object>> getListOfMap(final String key,
			final List<Map<String, Object>> defaultValue) {
		final Object object = map.get(key);
		if (object == null) {
			return defaultValue;
		}
		if (!(object instanceof List)) {
			throw new RuntimeException(key + " not found or is not an object");
		}
		@SuppressWarnings("unchecked")
		final List<Map<String, Object>> map = (List<Map<String, Object>>) object;
		return map;
	}

	public List<Integer> getListOfInteger(final String key) {
		final List<Integer> value = getListOfInteger(key, null);
		if (value == null) {
			throw new RuntimeException(key + " not found");
		}
		return value;
	}

	public List<Integer> getListOfInteger(final String key, final List<Integer> defaultValue) {
		final Object object = map.get(key);
		if (object == null) {
			return defaultValue;
		}
		if (!(object instanceof List)) {
			throw new RuntimeException(key + " not found or is not an object");
		}
		@SuppressWarnings("unchecked")
		final List<Integer> map = (List<Integer>) object;
		return map;
	}

	public Date getDate(final String key) {
		final Date value = getDate(key, null);
		if (value == null) {
			throw new RuntimeException(key + " not found");
		}
		return value;
	}

	public Date getDate(final String key, final Date defaultValue) {
		final Object object = map.get(key);
		if (object instanceof Timestamp) {
			return (Date) object;
		}
		if (object instanceof Date) {
			return (Date) object;
		}
		if (object instanceof String) {
			ParseException exception = null;
			for (int i = 0; i < DFS.length; i++) {
				final DateFormat df = DFS[i];
				try {
					final Date date = df.parse((String) object);
					log.info("index = " + i + ", date = " + date);
					return date;
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
			return new Date(((Long) object).longValue());
		}
		throw new RuntimeException("Failed to convert '" + object + "' to a date");
	}

	@Override
	public String toString() {
		return "Request: " + map;
	}
}