package com.stephenschafer.ami.handler;

import org.springframework.stereotype.Service;

@Service
public class BooleanBooleanComparer implements Comparer {
	@Override
	public int compareValues(final Object object1, final Object object2) {
		final Boolean value1 = (Boolean) object1;
		final Boolean value2 = (Boolean) object2;
		return value1.compareTo(value2);
	}

	@Override
	public String getHandler1Name() {
		return "boolean";
	}

	@Override
	public String getHandler2Name() {
		return "boolean";
	}
}
