package com.stephenschafer.ami.handler;

import org.springframework.stereotype.Service;

@Service
public class BooleanFloatComparer implements Comparer {
	@Override
	public int compareValues(final Object object1, final Object object2) {
		final Boolean value1 = (Boolean) object1;
		final Double value2 = (Double) object2;
		final Boolean booleanValue2 = value2.doubleValue() != 0.0;
		return value1.compareTo(booleanValue2);
	}

	@Override
	public String getHandler1Name() {
		return "boolean";
	}

	@Override
	public String getHandler2Name() {
		return "float";
	}
}
