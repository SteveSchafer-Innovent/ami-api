package com.stephenschafer.ami.service;

import com.stephenschafer.ami.handler.Handler;

public interface AttributeService {
	void deleteByThingId(Integer thingId);

	Handler getHandler(Integer attrDefnId);
}
