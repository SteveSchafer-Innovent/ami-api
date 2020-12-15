package com.stephenschafer.ami.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.service.AttrDefnService;
import com.stephenschafer.ami.service.AttributeService;
import com.stephenschafer.ami.service.ThingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AttributeController {
	@Autowired
	private AttributeService attributeService;
	@Autowired
	private AttrDefnService attrDefnService;
	@Autowired
	private HandlerProvider handlerProvider;
	@Autowired
	private ThingService thingService;

	@PostMapping("/attribute")
	public ApiResponse<Map<String, Object>> insert(@RequestBody final Map<String, Object> map) {
		log.info("POST /attribute " + map);
		return saveAttribute(map);
	}

	@PutMapping("/attribute")
	public ApiResponse<Map<String, Object>> update(@RequestBody final Map<String, Object> map) {
		log.info("PUT /attribute " + map);
		return saveAttribute(map);
	}

	private ApiResponse<Map<String, Object>> saveAttribute(final Map<String, Object> map) {
		final Handler handler = getHandler(map);
		if (handler != null) {
			handler.saveAttribute(map);
			return new ApiResponse<>(HttpStatus.OK.value(), "Attribute saved successfully.", map);
		}
		return new ApiResponse<>(HttpStatus.NOT_FOUND.value(),
				"Attribute definition handler not found.", null);
	}

	private Handler getHandler(final Map<String, Object> map) {
		final Integer attrDefnId = (Integer) map.get("attrDefnId");
		return attributeService.getHandler(attrDefnId);
	}

	@DeleteMapping("/attributes-by-thing/{thingId}")
	public ApiResponse<Void> delete(@PathVariable final Integer thingId) {
		log.info("DELETE /attributes-by-thing/" + thingId);
		attributeService.deleteByThingId(thingId);
		return new ApiResponse<>(HttpStatus.OK.value(),
				"Attributes for thing " + thingId + " deleted successfully.", null);
	}

	@GetMapping("/attribute/{thingId}/{attrDefnId}")
	public ApiResponse<Map<String, Object>> getAttribute(@PathVariable final Integer thingId,
			@PathVariable final Integer attrDefnId) {
		final ThingEntity thing = thingService.findById(thingId);
		final AttrDefnEntity attrDefn = attrDefnService.findById(attrDefnId);
		final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
		final Map<String, Object> attrDefnMap = handler.getAttrDefnMap(attrDefn);
		attrDefnMap.put("value", handler.getAttributeValue(thing, attrDefn));
		return new ApiResponse<>(HttpStatus.OK.value(), "Success", attrDefnMap);
	}
}
