package com.stephenschafer.ami.handler;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class DateDateComparer implements Comparer {
	@Override
	public int compareValues(final Object object1, final Object object2) {
		final Date value1 = (Date) object1;
		final Date value2 = (Date) object2;
		return value1.compareTo(value2);
	}

	@Override
	public String getHandler1Name() {
		return "date";
	}

	@Override
	public String getHandler2Name() {
		return "date";
	}
}
