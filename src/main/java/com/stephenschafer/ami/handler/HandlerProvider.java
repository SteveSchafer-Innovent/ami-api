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
	private BooleanHandler booleanHandler;
	@Autowired
	private UrlHandler urlHandler;
	@Autowired
	private DateTimeHandler datetimeHandler;

	@PostConstruct
	void init() {
		handlers.put("string", stringHandler);
		handlers.put("long-string", stringHandler);
		handlers.put("integer", integerHandler);
		handlers.put("float", floatHandler);
		handlers.put("boolean", booleanHandler);
		handlers.put("rich-text", richTextHandler);
		handlers.put("file", fileHandler);
		handlers.put("link", linkHandler);
		handlers.put("url", urlHandler);
		handlers.put("datetime", datetimeHandler);
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
