package com.stephenschafer.ami.handler;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Transactional
@Service
public class LongStringHandler extends StringHandler {
	@Override
	public String getHandlerName() {
		return "long-string";
	}
}
