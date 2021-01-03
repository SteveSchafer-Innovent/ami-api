package com.stephenschafer.ami.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.handler.LinkHandler;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.service.AttrDefnService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AttrDefnController {
	@Autowired
	private AttrDefnService attrDefnService;
	@Autowired
	private HandlerProvider handlerProvider;
	@Autowired
	private LinkHandler linkHandler;

	@PostMapping("/attrdefn")
	public ApiResponse<Map<String, Object>> insert(
			@RequestBody final Map<String, Object> attrDefn) {
		log.info("POST /attrdefn " + attrDefn);
		final Map<String, Object> response = new HashMap<>(attrDefn);
		final Handler handler = handlerProvider.getHandler((String) attrDefn.get("handler"));
		final int attrDefnId = handler.insertAttrDefn(attrDefn);
		response.put("id", Integer.valueOf(attrDefnId));
		return new ApiResponse<>(HttpStatus.OK.value(), "Attribute definition saved successfully.",
				response);
	}

	@PutMapping("/attrdefn")
	public ApiResponse<Map<String, Object>> update(
			@RequestBody final Map<String, Object> attrDefn) {
		log.info("PUT /attrdefn " + attrDefn);
		final Map<String, Object> response = new HashMap<>(attrDefn);
		final Handler handler = handlerProvider.getHandler((String) attrDefn.get("handler"));
		handler.updateAttrDefn(attrDefn);
		return new ApiResponse<>(HttpStatus.OK.value(), "Attribute definition saved successfully.",
				response);
	}

	@DeleteMapping("/attrdefn/{id}")
	public ApiResponse<Void> delete(@PathVariable final int id) {
		log.info("DELETE /attrdefn/" + id);
		final AttrDefnEntity entity = attrDefnService.findById(id);
		if (entity != null) {
			final Handler handler = handlerProvider.getHandler(entity.getHandler());
			try {
				handler.deleteAttrDefn(id);
			}
			catch (final Exception e) {
				log.error("Failed to delete attribute definition", e);
				return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Failed to delete attribute definition: " + e.getMessage(), null);
			}
		}
		return new ApiResponse<>(HttpStatus.OK.value(),
				"Attribute definition deleted successfully.", null);
	}

	@GetMapping("/attrdefns/{typeId}")
	public ApiResponse<List<Map<String, Object>>> list(@PathVariable final int typeId) {
		log.info("GET /attrdefns/" + typeId);
		final List<Map<String, Object>> result = new ArrayList<>();
		final List<AttrDefnEntity> list = attrDefnService.list(typeId);
		for (final AttrDefnEntity entity : list) {
			final Handler handler = handlerProvider.getHandler(entity.getHandler());
			result.add(handler.getAttrDefnMap(entity));
		}
		return new ApiResponse<>(HttpStatus.OK.value(),
				"Attributes definitions listed successfully.", result);
	}

	@GetMapping("/attrdefns")
	public ApiResponse<List<Map<String, Object>>> list() {
		log.info("GET /attrdefns");
		final List<Map<String, Object>> result = new ArrayList<>();
		final List<AttrDefnEntity> list = attrDefnService.list();
		for (final AttrDefnEntity entity : list) {
			final Handler handler = handlerProvider.getHandler(entity.getHandler());
			result.add(handler.getAttrDefnMap(entity));
		}
		return new ApiResponse<>(HttpStatus.OK.value(),
				"Attributes definitions listed successfully.", result);
	}

	@GetMapping("/link-attrdefns/{targetTypeId}")
	public ApiResponse<List<Map<String, Object>>> getLinkAttrDefns(
			@PathVariable final int targetTypeId) {
		log.info("GET /link-attrdefns/" + targetTypeId);
		final List<Map<String, Object>> result = new ArrayList<>();
		List<AttrDefnEntity> attrDefns = linkHandler.findByTargetTypeId(targetTypeId);
		for (final AttrDefnEntity attrDefn : attrDefns) {
			result.add(linkHandler.getAttrDefnMap(attrDefn));
		}
		attrDefns = linkHandler.findByTargetTypeId(null);
		for (final AttrDefnEntity attrDefn : attrDefns) {
			result.add(linkHandler.getAttrDefnMap(attrDefn));
		}
		return new ApiResponse<>(HttpStatus.OK.value(),
				"Attributes definitions listed successfully.", result);
	}

	@GetMapping("/attrdefnByName")
	public ApiResponse<Map<String, Object>> find(@RequestParam final int typeId,
			@RequestParam final String name) {
		log.info("GET /attrdefnsByName " + name);
		final AttrDefnEntity attrDefn = attrDefnService.findByName(typeId, name);
		final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
		return new ApiResponse<>(HttpStatus.OK.value(),
				"Attribute definition fetched successfully.", handler.getAttrDefnMap(attrDefn));
	}

	@GetMapping("/attrdefn/{id}")
	public ApiResponse<Map<String, Object>> get(@PathVariable final int id) {
		log.info("GET /attrdefn/" + id);
		final AttrDefnEntity entity = attrDefnService.findById(id);
		final Handler handler = handlerProvider.getHandler(entity.getHandler());
		return new ApiResponse<>(HttpStatus.OK.value(), "Attribute definition gotten successfully.",
				handler.getAttrDefnMap(entity));
	}

	@GetMapping("/handlers")
	public ApiResponse<List<String>> getHandlers() {
		log.info("GET /handlers");
		final List<String> names = new ArrayList<>(handlerProvider.getNames());
		Collections.sort(names);
		return new ApiResponse<>(HttpStatus.OK.value(), "Handers found successfully.", names);
	}
}
