package com.stephenschafer.ami.handler;

import org.springframework.stereotype.Service;

@Service
public class FloatFloatComparer implements Comparer {
	@Override
	public int compareValues(final Object object1, final Object object2) {
		final Double value1 = (Double) object1;
		final Double value2 = (Double) object2;
		return value1.compareTo(value2);
	}

	@Override
	public String getHandler1Name() {
		return "float";
	}

	@Override
	public String getHandler2Name() {
		return "float";
	}
}
