package com.stephenschafer.ami.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.jpa.AttrDefnDao;
import com.stephenschafer.ami.jpa.AttrDefnEntity;

@Transactional
@Service(value = "attributeService")
public class AttributeServiceImpl implements AttributeService {
	@Autowired
	private HandlerProvider handlerProvider;
	@Autowired
	private AttrDefnDao attrDefnDao;

	@Override
	public void deleteByThingId(final Integer thingId) {
		final List<Handler> handlers = handlerProvider.getAllHandlers();
		for (final Handler handler : handlers) {
			handler.deleteAttributesByThing(thingId);
		}
	}

	@Override
	public Handler getHandler(final Integer attrDefnId) {
		final Optional<AttrDefnEntity> optionalAttrDefn = attrDefnDao.findById(attrDefnId);
		if (optionalAttrDefn.isPresent()) {
			final AttrDefnEntity attrDefn = optionalAttrDefn.get();
			return handlerProvider.getHandler(attrDefn.getHandler());
		}
		return null;
	}
}
