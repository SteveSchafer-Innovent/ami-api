package com.stephenschafer.ami.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.handler.LinkHandler;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.LinkAttributeEntity;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.service.AttrDefnService;
import com.stephenschafer.ami.service.ThingService;
import com.stephenschafer.ami.service.WordService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class SearchController {
	@Autowired
	private ThingService thingService;
	@Autowired
	private WordService wordService;
	@Autowired
	private AttrDefnService attrDefnService;
	@Autowired
	private HandlerProvider handlerProvider;

	private interface SearchOp {
		Set<Integer> execute(Map<String, Object> map);
	}

	private final Map<String, SearchOp> ops = new HashMap<>();

	@PostConstruct
	public void init() {
		ops.put("word", new SearchOp() {
			@Override
			public Set<Integer> execute(final Map<String, Object> map) {
				final String word = (String) map.get("word");
				final Integer attrDefnId = (Integer) map.get("attrDefnId");
				if (attrDefnId == null) {
					return wordService.search(word);
				}
				return wordService.search(word, attrDefnId);
			}
		});
		ops.put("value", new SearchOp() {
			private void addToSet(final String value, final AttrDefnEntity attrDefnEntity,
					final Set<Integer> resultSet) {
				final String handlerName = attrDefnEntity.getHandler();
				final int typeId = attrDefnEntity.getTypeId();
				final List<ThingEntity> things = thingService.findByTypeId(typeId);
				final Handler handler = handlerProvider.getHandler(handlerName);
				for (final ThingEntity thing : things) {
					final Object attrValue = handler.getAttributeValue(thing, attrDefnEntity);
					if (attrValue != null && attrValue.toString().indexOf(value) >= 0) {
						resultSet.add(thing.getId());
					}
				}
			}

			@Override
			public Set<Integer> execute(final Map<String, Object> map) {
				final Set<Integer> resultSet = new HashSet<>();
				final String value = (String) map.get("value");
				final Integer attrDefnId = (Integer) map.get("attrDefnId");
				if (attrDefnId != null) {
					final AttrDefnEntity attrDefnEntity = attrDefnService.findById(attrDefnId);
					addToSet(value, attrDefnEntity, resultSet);
				}
				else {
					final List<AttrDefnEntity> list = attrDefnService.findAll();
					for (final AttrDefnEntity attrDefnEntity : list) {
						addToSet(value, attrDefnEntity, resultSet);
					}
				}
				return resultSet;
			}
		});
		ops.put("link", new SearchOp() {
			@Override
			public Set<Integer> execute(final Map<String, Object> map) {
				final Object thingObj = map.get("thing");
				if (!(thingObj instanceof Map)) {
					throw new RuntimeException("thing is not an object");
				}
				@SuppressWarnings("unchecked")
				final Set<Integer> thingIds = innerSearch((Map<String, Object>) thingObj);
				final Boolean reverse = (Boolean) map.get("reverse");
				final Integer attrDefnId = (Integer) map.get("attrDefnId");
				final AttrDefnEntity attrDefnEntity = attrDefnService.findById(attrDefnId);
				final Handler handler = handlerProvider.getHandler(attrDefnEntity.getHandler());
				final Set<Integer> resultSet = new HashSet<>();
				if (handler instanceof LinkHandler) {
					final LinkHandler linkHandler = (LinkHandler) handler;
					for (final Integer thingId : thingIds) {
						if (reverse != null && reverse.booleanValue()) {
							final List<LinkAttributeEntity> linkAttrEntities = linkHandler.findByTargetThingId(
								thingId);
							for (final LinkAttributeEntity linkAttrEntity : linkAttrEntities) {
								resultSet.add(linkAttrEntity.getThingId());
							}
						}
						else {
							final List<LinkAttributeEntity> linkAttrEntities = linkHandler.findByThingId(
								thingId);
							for (final LinkAttributeEntity linkAttrEntity : linkAttrEntities) {
								resultSet.add(linkAttrEntity.getTargetThingId());
							}
						}
					}
				}
				return resultSet;
			}
		});
		ops.put("things", new SearchOp() {
			@Override
			public Set<Integer> execute(final Map<String, Object> map) {
				final Object thingsObj = map.get("things");
				if (!(thingsObj instanceof List)) {
					throw new RuntimeException("ops is not an array");
				}
				@SuppressWarnings("unchecked")
				final Set<Integer> resultSet = new HashSet<>((List<Integer>) thingsObj);
				return resultSet;
			}
		});
		ops.put("and", new SearchOp() {
			@Override
			public Set<Integer> execute(final Map<String, Object> map) {
				final Object opsObj = map.get("ops");
				if (!(opsObj instanceof List)) {
					throw new RuntimeException("ops is not an array");
				}
				@SuppressWarnings("unchecked")
				final List<Map<String, Object>> ops = (List<Map<String, Object>>) opsObj;
				final List<Set<Integer>> sets = new ArrayList<>();
				for (final Map<String, Object> op : ops) {
					sets.add(innerSearch(op));
				}
				final Map<Integer, AtomicBoolean> resultMap = new HashMap<>();
				final Set<Integer> resultSet = new HashSet<>();
				if (!sets.isEmpty()) {
					final Set<Integer> firstSet = sets.get(0);
					for (final Integer i : firstSet) {
						resultMap.put(i, new AtomicBoolean(true));
					}
					for (final Integer i : resultMap.keySet()) {
						final AtomicBoolean b = resultMap.get(i);
						for (int setIndex = 1; setIndex < sets.size(); setIndex++) {
							final Set<Integer> set = sets.get(i);
							if (!set.contains(i)) {
								b.set(false);
								break;
							}
						}
					}
					for (final Integer i : resultMap.keySet()) {
						final AtomicBoolean b = resultMap.get(i);
						if (b != null && b.get()) {
							resultSet.add(i);
						}
					}
				}
				return resultSet;
			}
		});
		ops.put("or", new SearchOp() {
			@Override
			public Set<Integer> execute(final Map<String, Object> map) {
				final Object opsObj = map.get("ops");
				if (!(opsObj instanceof List)) {
					throw new RuntimeException("ops is not an array");
				}
				@SuppressWarnings("unchecked")
				final List<Map<String, Object>> ops = (List<Map<String, Object>>) opsObj;
				final List<Set<Integer>> sets = new ArrayList<>();
				for (final Map<String, Object> op : ops) {
					sets.add(innerSearch(op));
				}
				final Set<Integer> resultSet = new HashSet<>();
				for (final Set<Integer> set : sets) {
					for (final Integer i : set) {
						resultSet.add(i);
					}
				}
				return resultSet;
			}
		});
		ops.put("andnot", new SearchOp() {
			@Override
			public Set<Integer> execute(final Map<String, Object> map) {
				final Object op1Obj = map.get("op1");
				if (!(op1Obj instanceof Map)) {
					throw new RuntimeException("op1 is not an object");
				}
				final Object op2Obj = map.get("op2");
				if (!(op2Obj instanceof Map)) {
					throw new RuntimeException("op2 is not an object");
				}
				@SuppressWarnings("unchecked")
				final Set<Integer> set1 = innerSearch((Map<String, Object>) op1Obj);
				@SuppressWarnings("unchecked")
				final Set<Integer> set2 = innerSearch((Map<String, Object>) op2Obj);
				final Set<Integer> resultSet = new HashSet<>();
				for (final Integer i : set1) {
					resultSet.add(i);
				}
				for (final Integer i : set2) {
					resultSet.remove(i);
				}
				return resultSet;
			}
		});
	}

	@PostMapping("/search")
	public ApiResponse<List<FindThingResult>> search(
			@RequestBody final List<Map<String, Object>> searchRequests) {
		log.info("POST /search " + searchRequests);
		final List<FindThingResult> resultList = new ArrayList<>();
		for (final Map<String, Object> searchRequest : searchRequests) {
			final Set<Integer> thingIds = innerSearch(searchRequest);
			for (final Integer thingId : thingIds) {
				resultList.add(thingService.getFindThingResult(thingService.findById(thingId)));
			}
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Things gotten successfully.", resultList);
	}

	private Set<Integer> innerSearch(final Map<String, Object> searchRequest) {
		final String opName = (String) searchRequest.get("op");
		if (opName == null) {
			throw new RuntimeException("op is missing");
		}
		final SearchOp op = ops.get(opName);
		if (op == null) {
			throw new RuntimeException("op is invalid");
		}
		return op.execute(searchRequest);
	}

	@GetMapping("/rebuild-index")
	public ApiResponse<Void> updateIndex() {
		log.info("GET /rebuild-index");
		wordService.updateIndex();
		return new ApiResponse<>(HttpStatus.OK.value(), "Word index rebuilt.", null);
	}
}
