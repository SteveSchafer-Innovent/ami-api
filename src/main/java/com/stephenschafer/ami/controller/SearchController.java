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
		Set<Integer> execute(Request request);
	}

	private final Map<String, SearchOp> ops = new HashMap<>();

	private void addToSet(final String value, final AttrDefnEntity attrDefnEntity,
			final Set<Integer> resultSet) {
		final String handlerName = attrDefnEntity.getHandler();
		final int typeId = attrDefnEntity.getTypeId();
		final List<ThingEntity> things = thingService.findByTypeId(typeId);
		final Handler handler = handlerProvider.getHandler(handlerName);
		for (final ThingEntity thing : things) {
			final Object attrValue = handler.getAttributeValue(thing.getId(),
				attrDefnEntity.getId());
			if (attrValue != null
				&& attrValue.toString().toLowerCase().indexOf(value.toLowerCase()) >= 0) {
				resultSet.add(thing.getId());
			}
		}
	}

	@PostConstruct
	public void init() {
		ops.put("any", new SearchOp() {
			@Override
			public Set<Integer> execute(final Request request) {
				final String query = request.getString("query");
				final Integer typeId = request.getInteger("typeId", null);
				final Integer attrDefnId = request.getInteger("attrDefnId", null);
				log.info("op.any typeId = " + typeId + ", attrdefnId = " + attrDefnId + ", query = "
					+ query);
				final StringBuilder wordTerms = new StringBuilder();
				final Set<String> literalTerms = new HashSet<>();
				int prevEndIndex = -1;
				int startIndex = query.indexOf("\"");
				while (startIndex >= 0) {
					final String wordTerm = query.substring(prevEndIndex + 1, startIndex);
					wordTerms.append(wordTerm);
					final int endIndex = query.indexOf("\"", startIndex + 1);
					if (endIndex >= 0) {
						if (startIndex + 1 < endIndex) {
							literalTerms.add(query.substring(startIndex + 1, endIndex));
						}
						prevEndIndex = endIndex;
						startIndex = query.indexOf("\"", endIndex + 1);
					}
					else {
						if (startIndex + 1 < query.length()) {
							literalTerms.add(query.substring(startIndex + 1));
						}
						prevEndIndex = query.length();
						startIndex = -1;
					}
				}
				if (prevEndIndex + 1 < query.length()) {
					wordTerms.append(query.substring(prevEndIndex + 1));
				}
				log.info("wordTerms = " + wordTerms);
				log.info("literalTerms = " + literalTerms);
				final Set<Integer> resultSet = new HashSet<>();
				for (final String literalTerm : literalTerms) {
					if (literalTerm.length() == 0) {
						continue;
					}
					log.info("literalTerm = " + literalTerm);
					if (attrDefnId != null) {
						final AttrDefnEntity attrDefnEntity = attrDefnService.findById(attrDefnId);
						addToSet(literalTerm, attrDefnEntity, resultSet);
					}
					else {
						final List<AttrDefnEntity> list = typeId != null
							? attrDefnService.findByTypeId(typeId)
							: attrDefnService.findAll();
						for (final AttrDefnEntity attrDefnEntity : list) {
							addToSet(literalTerm, attrDefnEntity, resultSet);
						}
					}
				}
				for (final String wordTerm : wordService.parseWords(wordTerms.toString())) {
					log.info("wordTerm = '" + wordTerm + "'");
					if (wordTerm.length() == 0) {
						continue;
					}
					Set<Integer> set;
					if (attrDefnId != null) {
						set = wordService.searchByAttribute(wordTerm, attrDefnId);
					}
					else if (typeId != null) {
						set = wordService.searchByType(wordTerm, typeId);
					}
					else {
						set = wordService.search(wordTerm);
					}
					resultSet.addAll(set);
				}
				log.info("search results: " + resultSet.size());
				return resultSet;
			}
		});
		ops.put("word", new SearchOp() {
			@Override
			public Set<Integer> execute(final Request request) {
				final String word = request.getString("word");
				final Integer attrDefnId = request.getInteger("attrDefnId", null);
				if (attrDefnId == null) {
					return wordService.search(word);
				}
				return wordService.searchByAttribute(word, attrDefnId);
			}
		});
		ops.put("value", new SearchOp() {
			@Override
			public Set<Integer> execute(final Request request) {
				final Set<Integer> resultSet = new HashSet<>();
				final String value = request.getString("value");
				final Integer attrDefnId = request.getInteger("attrDefnId", null);
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
			public Set<Integer> execute(final Request request) {
				final Map<String, Object> thingObj = request.getMap("thing");
				final Set<Integer> thingIds = innerSearch(thingObj);
				final Boolean reverse = request.getBoolean("reverse", Boolean.FALSE);
				final Integer attrDefnId = request.getInteger("attrDefnId");
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
			public Set<Integer> execute(final Request request) {
				return request.getSetOfInteger("things");
			}
		});
		ops.put("and", new SearchOp() {
			@Override
			public Set<Integer> execute(final Request request) {
				final List<Map<String, Object>> ops = request.getListOfMap("ops");
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
			public Set<Integer> execute(final Request request) {
				final List<Map<String, Object>> ops = request.getListOfMap("ops");
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
			public Set<Integer> execute(final Request request) {
				final Map<String, Object> op1Obj = request.getMap("op1");
				final Map<String, Object> op2Obj = request.getMap("op2");
				final Set<Integer> set1 = innerSearch(op1Obj);
				final Set<Integer> set2 = innerSearch(op2Obj);
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

	private Set<Integer> innerSearch(final Map<String, Object> map) {
		final Request request = new Request(map);
		final String opName = request.getString("op");
		final SearchOp op = ops.get(opName);
		if (op == null) {
			throw new RuntimeException("op is invalid");
		}
		return op.execute(request);
	}

	@GetMapping("/rebuild-index")
	public ApiResponse<Void> updateIndex() {
		log.info("GET /rebuild-index");
		wordService.updateIndex();
		return new ApiResponse<>(HttpStatus.OK.value(), "Word index rebuilt.", null);
	}
}
