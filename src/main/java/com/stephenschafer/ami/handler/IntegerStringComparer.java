package com.stephenschafer.ami.handler;

import org.springframework.stereotype.Service;

@Service
public class IntegerStringComparer implements Comparer {
	@Override
	public int compareValues(final Object object1, final Object object2) {
		final Integer value1 = (Integer) object1;
		final String value2 = (String) object2;
		Integer integerValue2;
		try {
			integerValue2 = Integer.valueOf(value2);
		}
		catch (final NumberFormatException e) {
			integerValue2 = Integer.valueOf(0);
		}
		return value1.compareTo(integerValue2);
	}

	@Override
	public String getHandler1Name() {
		return "integer";
	}

	@Override
	public String getHandler2Name() {
		return "string";
	}
}
