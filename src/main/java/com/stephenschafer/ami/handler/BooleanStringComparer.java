package com.stephenschafer.ami.handler;

import org.springframework.stereotype.Service;

@Service
public class BooleanStringComparer implements Comparer {
	@Override
	public int compareValues(final Object object1, final Object object2) {
		final Boolean value1 = (Boolean) object1;
		final String value2 = (String) object2;
		final Boolean booleanValue2 = value2.length() > 0;
		return value1.compareTo(booleanValue2);
	}

	@Override
	public String getHandler1Name() {
		return "boolean";
	}

	@Override
	public String getHandler2Name() {
		return "string";
	}
}
