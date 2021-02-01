package com.stephenschafer.ami.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HandlerProvider {
	private final Map<String, Handler> handlers = new HashMap<>();
	private final Map<String, Map<String, Comparer>> comparers = new HashMap<>();
	@Autowired
	private LinkHandler linkHandler;
	@Autowired
	private FileHandler fileHandler;
	@Autowired
	private RichTextHandler richTextHandler;
	@Autowired
	private FloatHandler floatHandler;
	@Autowired
	private IntegerHandler integerHandler;
	@Autowired
	private StringHandler stringHandler;
	@Autowired
	private LongStringHandler longStringHandler;
	@Autowired
	private BooleanHandler booleanHandler;
	@Autowired
	private UrlHandler urlHandler;
	@Autowired
	private DateTimeHandler datetimeHandler;
	@Autowired
	private BooleanBooleanComparer booleanBooleanComparer;
	@Autowired
	private BooleanIntegerComparer booleanIntegerComparer;
	@Autowired
	private BooleanFloatComparer booleanFloatComparer;
	@Autowired
	private BooleanStringComparer booleanStringComparer;
	@Autowired
	private IntegerIntegerComparer integerIntegerComparer;
	@Autowired
	private IntegerFloatComparer integerFloatComparer;
	@Autowired
	private IntegerStringComparer integerStringComparer;
	@Autowired
	private FloatFloatComparer floatFloatComparer;
	@Autowired
	private FloatStringComparer floatStringComparer;
	@Autowired
	private StringStringComparer stringStringComparer;
	@Autowired
	private DateDateComparer dateDateComparer;

	@PostConstruct
	void init() {
		addHandler(stringHandler);
		addHandler(longStringHandler);
		addHandler(integerHandler);
		addHandler(floatHandler);
		addHandler(booleanHandler);
		addHandler(richTextHandler);
		addHandler(fileHandler);
		addHandler(linkHandler);
		addHandler(urlHandler);
		addHandler(datetimeHandler);
		addComparer(booleanBooleanComparer);
		addComparer(booleanIntegerComparer);
		addComparer(booleanFloatComparer);
		addComparer(booleanStringComparer);
		addComparer(integerIntegerComparer);
		addComparer(integerFloatComparer);
		addComparer(integerStringComparer);
		addComparer(floatFloatComparer);
		addComparer(floatStringComparer);
		addComparer(stringStringComparer);
		addComparer(dateDateComparer);
	}

	private void addHandler(final Handler handler) {
		handlers.put(handler.getHandlerName(), handler);
	}

	private void addComparer(final Comparer comparer) {
		final String name1 = comparer.getHandler1Name();
		final String name2 = comparer.getHandler2Name();
		Map<String, Comparer> comparers = this.comparers.get(name1);
		if (comparers == null) {
			comparers = new HashMap<>();
			this.comparers.put(name1, comparers);
		}
		comparers.put(name2, comparer);
	}

	public Handler getHandler(final String name) {
		return handlers.get(name);
	}

	public List<String> getNames() {
		final List<String> result = new ArrayList<>(handlers.keySet());
		Collections.sort(result);
		return result;
	}

	public List<Handler> getAllHandlers() {
		return new ArrayList<>(handlers.values());
	}

	public Comparer getComparer(final String name1, final String name2) {
		final Map<String, Comparer> comparers = this.comparers.get(name1);
		if (comparers == null) {
			return null;
		}
		return comparers.get(name2);
	}
}
