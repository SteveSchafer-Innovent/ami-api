package com.stephenschafer.ami.handler;

import org.springframework.stereotype.Service;

@Service
public class IntegerFloatComparer implements Comparer {
	@Override
	public int compareValues(final Object object1, final Object object2) {
		final Integer value1 = (Integer) object1;
		final Double value2 = (Double) object2;
		final Integer intValue2 = Integer.valueOf(value2.intValue());
		return value1.compareTo(intValue2);
	}

	@Override
	public String getHandler1Name() {
		return "integer";
	}

	@Override
	public String getHandler2Name() {
		return "float";
	}
}
