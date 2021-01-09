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
	}

	private void addHandler(final Handler handler) {
		handlers.put(handler.getHandlerName(), handler);
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
}
