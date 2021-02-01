package com.stephenschafer.ami.controller;

import java.util.Date;
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

import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.jpa.UserEntity;
import com.stephenschafer.ami.service.AttributeNotFoundException;
import com.stephenschafer.ami.service.ThingService;
import com.stephenschafer.ami.service.UserService;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ThingController {
	@Autowired
	private ThingService thingService;
	@Autowired
	private UserService userService;

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

	@Getter
	@Setter
	@ToString
	public static class ThingOrderRequest {
		private Integer typeId;
		private Integer contextThingId;
		private List<Integer> thingIds;
	}

	@PutMapping("/thing/order")
	public ApiResponse<Void> thingOrder(@RequestBody final ThingOrderRequest thingOrderRequest,
			final HttpServletRequest httpServletRequest) {
		log.info("PUT /thing/order " + thingOrderRequest);
		final String username = (String) httpServletRequest.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		final Integer contextThingId = thingOrderRequest.getContextThingId();
		if (contextThingId != null) {
			thingService.updateThingOrder(user.getId(), thingOrderRequest.getTypeId(),
				contextThingId, thingOrderRequest.getThingIds());
		}
		else {
			thingService.updateThingOrder(user.getId(), thingOrderRequest.getTypeId(),
				thingOrderRequest.getThingIds());
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing order saved successfully.", null);
	}

	@GetMapping("/thing/order/{typeId}")
	public ApiResponse<List<Integer>> thingOrder(@PathVariable final Integer typeId,
			final HttpServletRequest httpServletRequest) {
		log.info("GET /thing/order/" + typeId);
		final String username = (String) httpServletRequest.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing order fetched successfully.",
				thingService.getThingOrder(user.getId(), typeId));
	}

	@GetMapping("/thing/order/{typeId}/{contextThingId}")
	public ApiResponse<List<Integer>> thingOrder(@PathVariable final Integer typeId,
			@PathVariable final Integer contextThingId,
			final HttpServletRequest httpServletRequest) {
		log.info("GET /thing/order/" + typeId);
		final String username = (String) httpServletRequest.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing order fetched successfully.",
				thingService.getThingOrder(user.getId(), typeId, contextThingId));
	}

	@GetMapping("/things/{typeId}")
	public ApiResponse<List<ThingEntity>> getThings(@PathVariable final Integer typeId) {
		log.info("GET /things/" + typeId);
		final List<ThingEntity> things = thingService.findByTypeId(typeId);
		log.info("  things: " + things.size());
		return new ApiResponse<>(HttpStatus.OK.value(), "Things gotten successfully.", things);
	}

	@GetMapping("/thing/{thingId}")
	public ApiResponse<ThingEntity> getThing(@PathVariable final Integer thingId) {
		log.info("GET /thing/" + thingId);
		final ThingEntity thing = thingService.findById(thingId);
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing gotten successfully.", thing);
	}

	@GetMapping("/thing/name/{thingId}")
	public ApiResponse<String> getThingName(@PathVariable final Integer thingId)
			throws AttributeNotFoundException {
		log.info("GET /thing/name/" + thingId);
		final ThingEntity thing = thingService.findById(thingId);
		final String name = thingService.getName(thing);
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing name gotten successfully.", name);
	}

	@GetMapping("/thing/presentation/{thingId}")
	public ApiResponse<String> getThingPresentation(@PathVariable final Integer thingId)
			throws AttributeNotFoundException {
		log.info("GET /thing/presentation/" + thingId);
		final ThingEntity thing = thingService.findById(thingId);
		final String presentation = thingService.getPresentation(thing, true);
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing presentation gotten successfully.",
				presentation);
	}

	@GetMapping("/thing/parent/{thingId}")
	public ApiResponse<ThingEntity> getThingParent(@PathVariable final Integer thingId)
			throws AttributeNotFoundException {
		log.info("GET /thing/parent/" + thingId);
		final ThingEntity thing = thingService.findById(thingId);
		final Integer parentId = thingService.getParentId(thing);
		final ThingEntity parent = parentId == null ? null : thingService.findById(parentId);
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing parent gotten successfully.",
				parent);
	}

	@GetMapping("/thing/attributes/{thingId}")
	public ApiResponse<Map<String, Object>> getThingAttributes(
			@PathVariable final Integer thingId) {
		log.info("GET /thing/attributes/" + thingId);
		final ThingEntity thing = thingService.findById(thingId);
		final Map<String, Object> attrMap = thingService.getAttributeValues(thing);
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing attributes gotten successfully.",
				attrMap);
	}

	@GetMapping("/thing/source-links/{thingId}")
	public ApiResponse<Map<Integer, Set<Integer>>> getThingSourceLinks(
			@PathVariable final Integer thingId) {
		log.info("GET /thing/source-links/" + thingId);
		final Map<Integer, Set<Integer>> resultMap = thingService.getSourceLinks(thingId);
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing attributes gotten successfully.",
				resultMap);
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
