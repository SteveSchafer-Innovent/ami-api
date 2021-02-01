package com.stephenschafer.ami.handler;

import org.springframework.stereotype.Service;

@Service
public class IntegerIntegerComparer implements Comparer {
	@Override
	public int compareValues(final Object object1, final Object object2) {
		final Integer value1 = (Integer) object1;
		final Integer value2 = (Integer) object2;
		return value1.compareTo(value2);
	}

	@Override
	public String getHandler1Name() {
		return "integer";
	}

	@Override
	public String getHandler2Name() {
		return "integer";
	}
}
