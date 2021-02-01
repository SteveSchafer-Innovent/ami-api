package com.stephenschafer.ami.handler;

import org.springframework.stereotype.Service;

@Service
public class BooleanIntegerComparer implements Comparer {
	@Override
	public int compareValues(final Object object1, final Object object2) {
		final Boolean value1 = (Boolean) object1;
		final Integer value2 = (Integer) object2;
		final Boolean booleanValue2 = value2.intValue() != 0;
		return value1.compareTo(booleanValue2);
	}

	@Override
	public String getHandler1Name() {
		return "boolean";
	}

	@Override
	public String getHandler2Name() {
		return "integer";
	}
}
