package com.stephenschafer.ami.handler;

public interface Comparer {
	int compareValues(Object object1, Object object2);

	String getHandler1Name();

	String getHandler2Name();
}
