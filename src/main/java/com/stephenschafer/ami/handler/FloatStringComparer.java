package com.stephenschafer.ami.handler;

import org.springframework.stereotype.Service;

@Service
public class FloatStringComparer implements Comparer {
	@Override
	public int compareValues(final Object object1, final Object object2) {
		final Double value1 = (Double) object1;
		final String value2 = (String) object2;
		Double doubleValue2;
		try {
			doubleValue2 = Double.valueOf(value2);
		}
		catch (final NumberFormatException e) {
			doubleValue2 = Double.valueOf(0);
		}
		return value1.compareTo(doubleValue2);
	}

	@Override
	public String getHandler1Name() {
		return "float";
	}

	@Override
	public String getHandler2Name() {
		return "string";
	}
}
