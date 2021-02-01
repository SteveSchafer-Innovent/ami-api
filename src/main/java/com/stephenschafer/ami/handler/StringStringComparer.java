package com.stephenschafer.ami.handler;

import org.springframework.stereotype.Service;

@Service
public class StringStringComparer implements Comparer {
	@Override
	public int compareValues(final Object object1, final Object object2) {
		final String value1 = (String) object1;
		final String value2 = (String) object2;
		return value1.compareTo(value2);
	}

	@Override
	public String getHandler1Name() {
		return "string";
	}

	@Override
	public String getHandler2Name() {
		return "string";
	}
}
