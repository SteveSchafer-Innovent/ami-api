package com.stephenschafer.ami.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	public ApiResponse<List<FindThingResult>> getThings(@PathVariable final Integer typeId) {
		log.info("GET /things/" + typeId);
		final List<FindThingResult> resultList = new ArrayList<>();
		final List<ThingEntity> things = thingService.findByTypeId(typeId);
		log.info("  thing results: " + things.size());
		for (final ThingEntity thing : things) {
			resultList.add(thingService.getFindThingResult(thing));
		}
		log.info("  findThingResults: " + resultList.size());
		return new ApiResponse<>(HttpStatus.OK.value(), "Things gotten successfully.", resultList);
	}

	@GetMapping("/thing/{thingId}")
	public ApiResponse<FindThingResult> getThing(@PathVariable final Integer thingId) {
		log.info("GET /thing/" + thingId);
		final ThingEntity thing = thingService.findById(thingId);
		return new ApiResponse<>(HttpStatus.OK.value(), "Thing gotten successfully.",
				thingService.getFindThingResult(thing));
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
