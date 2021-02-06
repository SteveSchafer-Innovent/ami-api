package com.stephenschafer.ami.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stephenschafer.ami.handler.Comparer;
import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.handler.LinkHandler;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.LinkAttributeEntity;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.jpa.UserEntity;
import com.stephenschafer.ami.service.AttrDefnService;
import com.stephenschafer.ami.service.ThingService;
import com.stephenschafer.ami.service.UserService;
import com.stephenschafer.ami.service.WordService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class SearchController {
	@Autowired
	private UserService userService;
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
			final Set<Integer> inputSet, final Set<Integer> resultSet) {
		final String handlerName = attrDefnEntity.getHandler();
		final Handler handler = handlerProvider.getHandler(handlerName);
		if (inputSet != null) {
			for (final Integer thingId : inputSet) {
				final Object attrValue = handler.getAttributeValue(thingId, attrDefnEntity.getId());
				if (attrValue != null
					&& attrValue.toString().toLowerCase().indexOf(value.toLowerCase()) >= 0) {
					resultSet.add(thingId);
				}
			}
		}
		else {
			final int typeId = attrDefnEntity.getTypeId();
			final List<ThingEntity> things = thingService.findByTypeId(typeId);
			for (final ThingEntity thing : things) {
				final Object attrValue = handler.getAttributeValue(thing.getId(),
					attrDefnEntity.getId());
				if (attrValue != null
					&& attrValue.toString().toLowerCase().indexOf(value.toLowerCase()) >= 0) {
					resultSet.add(thing.getId());
				}
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
				String sep = "";
				while (startIndex >= 0) {
					final String wordTerm = query.substring(prevEndIndex + 1, startIndex);
					wordTerms.append(sep);
					sep = " ";
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
					wordTerms.append(sep);
					sep = " ";
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
					final Set<Integer> set = new HashSet<>();
					for (final String word : wordService.parseWords(literalTerm)) {
						if (attrDefnId != null) {
							set.addAll(wordService.searchByAttribute(word, attrDefnId));
						}
						else if (typeId != null) {
							set.addAll(wordService.searchByType(word, typeId));
						}
						else {
							set.addAll(wordService.search(word));
						}
					}
					if (attrDefnId != null) {
						final AttrDefnEntity attrDefnEntity = attrDefnService.findById(attrDefnId);
						addToSet(literalTerm, attrDefnEntity, set, resultSet);
					}
					else {
						final List<AttrDefnEntity> list = typeId != null
							? attrDefnService.findByTypeId(typeId)
							: attrDefnService.findAll();
						for (final AttrDefnEntity attrDefnEntity : list) {
							addToSet(literalTerm, attrDefnEntity, set, resultSet);
						}
					}
				}
				for (final String word : wordService.parseWords(wordTerms.toString())) {
					log.info("wordTerm = '" + word + "'");
					if (word.length() == 0) {
						continue;
					}
					Set<Integer> set;
					if (attrDefnId != null) {
						set = wordService.searchByAttribute(word, attrDefnId);
					}
					else if (typeId != null) {
						set = wordService.searchByType(word, typeId);
					}
					else {
						set = wordService.search(word);
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
					addToSet(value, attrDefnEntity, null, resultSet);
				}
				else {
					final List<AttrDefnEntity> list = attrDefnService.findAll();
					for (final AttrDefnEntity attrDefnEntity : list) {
						addToSet(value, attrDefnEntity, null, resultSet);
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

	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	public static class SearchResult {
		private int resultCount;
		private String id;
		private List<String> sortNames;
	}

	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	public static class UserSearchResults {
		private final Map<String, List<Integer>> searchResults = new HashMap<>();
		private final List<String> keys = new ArrayList<>();

		public String add(final List<Integer> searchResult) {
			if (keys.size() > 4) {
				keys.remove(0);
			}
			final String key = UUID.randomUUID().toString();
			keys.add(key);
			searchResults.put(key, searchResult);
			return key;
		}

		public List<Integer> get(final String key) {
			return searchResults.get(key);
		}
	}

	private final Map<Integer, UserSearchResults> searchResults = new HashMap<>();

	@Getter
	@Setter
	@ToString
	private class SortableSearchResult implements Comparable<SortableSearchResult> {
		private final ThingEntity thing;

		@Getter
		@Setter
		@ToString
		@AllArgsConstructor
		private class Field implements Comparable<Field> {
			private final Object value;
			private final String handlerName;

			@Override
			public int compareTo(final Field that) {
				if (this.value == null) {
					if (that.value == null) {
						return 0;
					}
					return -1;
				}
				if (that.value == null) {
					return 1;
				}
				final Comparer comparer = handlerProvider.getComparer(this.handlerName,
					that.handlerName);
				if (comparer != null) {
					return comparer.compareValues(this.value, that.value);
				}
				if (!this.handlerName.equals(that.handlerName)) {
					final Comparer inverseComparer = handlerProvider.getComparer(that.handlerName,
						this.handlerName);
					if (inverseComparer != null) {
						return inverseComparer.compareValues(that.value, this.value);
					}
				}
				return 0;
			}
		}

		private List<Field> fields = new ArrayList<>();

		public SortableSearchResult(final ThingEntity thing,
				final List<AttrDefnEntity> sortAttrDefns) {
			this.thing = thing;
			for (final AttrDefnEntity attrDefn : sortAttrDefns) {
				Field field = null;
				if (attrDefn != null) {
					final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
					if (handler.isSortable()) {
						if (thing != null) {
							final Object value = handler.getAttributeValue(thing.getId(),
								attrDefn.getId());
							field = new Field(value, handler.getHandlerName());
						}
					}
				}
				this.fields.add(field);
			}
		}

		@Override
		public int compareTo(final SortableSearchResult that) {
			final Iterator<Field> thisIter = this.fields.iterator();
			final Iterator<Field> thatIter = that.fields.iterator();
			while (thisIter.hasNext()) {
				final Field thisField = thisIter.next();
				final Field thatField = thatIter.next();
				final int comparison;
				if (thisField == null) {
					if (thatField == null) {
						comparison = 0;
					}
					else {
						comparison = -1;
					}
				}
				else {
					if (thatField == null) {
						comparison = 1;
					}
					else {
						comparison = thisField.compareTo(thatField);
					}
				}
				if (comparison != 0) {
					return comparison;
				}
			}
			return 0;
		}
	}

	@PostMapping("/search")
	public ApiResponse<SearchResult> search(@RequestBody final List<Map<String, Object>> ops,
			final HttpServletRequest request) {
		log.info("POST /search " + ops);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		final Set<Integer> resultSet = new HashSet<>();
		for (final Map<String, Object> op : ops) {
			final Set<Integer> thingIds = innerSearch(op);
			resultSet.addAll(thingIds);
		}
		final Set<Integer> typeIds = new HashSet<>();
		for (final Integer thingId : resultSet) {
			final ThingEntity thing = thingService.findById(thingId);
			typeIds.add(thing.getTypeId());
		}
		final List<String> sortNames = getSortNames(typeIds);
		final List<Integer> resultList = new ArrayList<>(resultSet);
		UserSearchResults userSearchResults = searchResults.get(user.getId());
		if (userSearchResults == null) {
			userSearchResults = new UserSearchResults();
			searchResults.put(user.getId(), userSearchResults);
		}
		final String searchId = userSearchResults.add(resultList);
		final SearchResult result = new SearchResult(resultList.size(), searchId, sortNames);
		return new ApiResponse<>(HttpStatus.OK.value(), "Search successful.", result);
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

	@GetMapping("/rebuild-index/run")
	public ApiResponse<Void> rebuildIndex() {
		log.info("GET /rebuild-index/run");
		wordService.rebuildIndex();
		return new ApiResponse<>(HttpStatus.OK.value(), "Word index rebuilt.", null);
	}

	@GetMapping("/rebuild-index/start")
	public ApiResponse<Void> startRebuildIndex() {
		log.info("GET /rebuild-index/start");
		wordService.submitRebuildIndex();
		return new ApiResponse<>(HttpStatus.OK.value(), "Word index rebuild started.", null);
	}

	@GetMapping("/update-index/run")
	public ApiResponse<Void> updateIndex() {
		log.info("GET /update-index/run");
		wordService.updateIndex();
		return new ApiResponse<>(HttpStatus.OK.value(), "Word index updated.", null);
	}

	@GetMapping("/update-index/start")
	public ApiResponse<Void> startUpdateIndex() {
		log.info("GET /update-index/start");
		wordService.submitUpdateIndex();
		return new ApiResponse<>(HttpStatus.OK.value(), "Word index update started.", null);
	}

	@GetMapping("/update-index/status")
	public ApiResponse<Map<String, Object>> getUpdateIndexJob() {
		log.info("GET /update-index/status");
		final Future<Void> future = wordService.rebuildFuture();
		final Map<String, Object> map = new HashMap<>();
		map.put("done", future.isDone());
		map.put("cancelled", future.isCancelled());
		if (future instanceof CompletableFuture) {
			final CompletableFuture<Void> completableFuture = (CompletableFuture<Void>) future;
			map.put("completedExceptionally", completableFuture.isCompletedExceptionally());
		}
		final Exception exception = wordService.getLastRebuildException();
		if (exception != null) {
			map.put("exception-class", exception.getClass().getName());
			map.put("exception", exception);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Future successfully retrieved.", map);
	}

	private List<String> getSortNames(final Set<Integer> typeIds) {
		final Set<String> resultSet = new HashSet<>();
		for (final int typeId : typeIds) {
			final List<AttrDefnEntity> attrDefns = attrDefnService.findByTypeId(typeId);
			for (final AttrDefnEntity attrDefn : attrDefns) {
				resultSet.add(attrDefn.getName());
			}
		}
		final List<String> resultList = new ArrayList<>(resultSet);
		Collections.sort(resultList, new Comparator<String>() {
			@Override
			public int compare(final String s1, final String s2) {
				return s1.toLowerCase().compareTo(s2.toLowerCase());
			}
		});
		return resultList;
	}

	@GetMapping("/search-results/{searchId}")
	public ApiResponse<List<Integer>> getSearchResults(@PathVariable final String searchId,
			final HttpServletRequest request) {
		log.info("GET /search-results " + searchId);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		final UserSearchResults userSearchResults = searchResults.get(user.getId());
		if (userSearchResults == null) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "No search results found.",
					new ArrayList<Integer>());
		}
		final List<Integer> searchResults = userSearchResults.get(searchId);
		if (searchResults == null) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "No search results found.",
					new ArrayList<Integer>());
		}
		final String sortNamesParam = request.getParameter("sorts");
		log.info("sorts = " + sortNamesParam);
		final String pageSizeParam = request.getParameter("page-size");
		log.info("page-size = " + pageSizeParam);
		final int pageSize = pageSizeParam == null ? 20 : Integer.parseInt(pageSizeParam);
		final String pageParam = request.getParameter("page");
		log.info("page = " + pageParam);
		final int page = pageParam == null ? 0 : Integer.parseInt(pageParam);
		final String[] sortNames = sortNamesParam == null ? null : sortNamesParam.split(", *");
		final List<SortableSearchResult> sortableList = new ArrayList<>();
		for (final Integer thingId : searchResults) {
			final ThingEntity thing = thingService.findById(thingId);
			final List<AttrDefnEntity> sortList = new ArrayList<>();
			if (sortNames != null) {
				for (final String sortName : sortNames) {
					final AttrDefnEntity attrDefn;
					if (thing != null) {
						attrDefn = attrDefnService.findByName(thing.getTypeId(), sortName);
					}
					else {
						attrDefn = null;
					}
					sortList.add(attrDefn);
				}
			}
			sortableList.add(new SortableSearchResult(thing, sortList));
		}
		Collections.sort(sortableList);
		final List<Integer> result = new ArrayList<>();
		final int startCount = page * pageSize;
		final int endCount = startCount + pageSize;
		int index = 0;
		for (final SortableSearchResult sortableSearchResult : sortableList) {
			if (index >= endCount) {
				break;
			}
			if (index >= startCount) {
				result.add(sortableSearchResult.getThing().getId());
			}
			index++;
		}
		log.info("result = " + result);
		return new ApiResponse<>(HttpStatus.OK.value(), "Search results fetched successfully.",
				result);
	}
}
