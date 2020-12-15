package com.stephenschafer.ami.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import com.stephenschafer.ami.handler.LinkHandler;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.FindTypeResult;
import com.stephenschafer.ami.jpa.LinkAttributeEntity;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.jpa.UserEntity;
import com.stephenschafer.ami.service.AttrDefnService;
import com.stephenschafer.ami.service.ThingService;
import com.stephenschafer.ami.service.TypeService;
import com.stephenschafer.ami.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ThingController {
	@Autowired
	private ThingService thingService;
	@Autowired
	private UserService userService;
	@Autowired
	private TypeService typeService;
	@Autowired
	private AttrDefnService attrDefnService;
	@Autowired
	private HandlerProvider handlerProvider;
	@Autowired
	private LinkHandler linkHandler;

	@PostMapping("/thing")
	public ApiResponse<ThingEntity> insert(@RequestBody final ThingEntity thing,
			final HttpServletRequest request) {
		log.info("POST /thing " + thing);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		thing.setCreator(user.getId());
		thing.setCreated(new Date());
		return new ApiResponse<>(HttpStatus.OK.value(), "Entity saved successfully.",
				thingService.insert(thing));
	}

	@PutMapping("/thing")
	public ApiResponse<ThingEntity> update(@RequestBody final ThingEntity thing) {
		log.info("POST /thing " + thing);
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing saved successfully.",
				thingService.update(thing));
	}

	@GetMapping("/things/{typeId}")
	public ApiResponse<List<FindThingResult>> getThings(@PathVariable final Integer typeId) {
		log.info("GET /things/" + typeId);
		final List<FindThingResult> resultList = new ArrayList<>();
		final List<ThingEntity> things = thingService.findByTypeId(typeId);
		log.info("  thing results: " + things.size());
		for (final ThingEntity thing : things) {
			resultList.add(getFindThingResult(thing));
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Things gotten successfully.", resultList);
	}

	@GetMapping("/thing/{thingId}")
	public ApiResponse<FindThingResult> getThing(@PathVariable final Integer thingId) {
		log.info("GET /thing/" + thingId);
		final ThingEntity thing = thingService.findById(thingId);
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing gotten successfully.",
				getFindThingResult(thing));
	}

	private FindThingResult getFindThingResult(final ThingEntity thing) {
		if (thing == null) {
			return null;
		}
		final FindThingResult result = new FindThingResult();
		result.setId(thing.getId());
		result.setCreated(thing.getCreated());
		result.setCreator(userService.findById(thing.getCreator()));
		final int typeId = thing.getTypeId();
		final FindTypeResult type = typeService.findById(typeId);
		result.setType(type);
		final List<AttrDefnEntity> attrDefns = attrDefnService.list(typeId);
		final Map<String, Object> attrMap = new HashMap<>();
		for (final AttrDefnEntity attrDefn : attrDefns) {
			final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
			final Map<String, Object> attrDefnMap = handler.getAttrDefnMap(attrDefn);
			attrDefnMap.put("value", handler.getAttributeValue(thing, attrDefn));
			attrMap.put(attrDefn.getName(), attrDefnMap);
		}
		result.setAttributes(attrMap);
		final List<LinkAttributeEntity> sourceLinks = linkHandler.findByTargetThingId(
			thing.getId());
		final Map<Integer, Set<Integer>> links = new HashMap<>();
		for (final LinkAttributeEntity linkAttribute : sourceLinks) {
			final int attrDefnId = linkAttribute.getAttributeDefnId();
			Set<Integer> thingIds = links.get(attrDefnId);
			if (thingIds == null) {
				thingIds = new HashSet<>();
				links.put(attrDefnId, thingIds);
			}
			thingIds.add(linkAttribute.getThingId());
		}
		result.setLinks(links);
		return result;
	}

	@DeleteMapping("/thing/{id}")
	public ApiResponse<Void> delete(@PathVariable final int id) {
		log.info("DELETE /thing/" + id);
		try {
			thingService.delete(id);
		}
		catch (final Exception e) {
			log.error("Failed to delete thing", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Failed to delete thing: " + e.getMessage(), null);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing " + id + " deleted successfully.",
				null);
	}
}
