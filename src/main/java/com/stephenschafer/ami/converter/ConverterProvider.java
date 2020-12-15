package com.stephenschafer.ami.converter;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class ConverterProvider {
	private final Map<String, Map<String, Converter>> converters = new HashMap<>();

	@PostConstruct
	void init() {
		final Map<String, Converter> fromFileMap = new HashMap<>();
		fromFileMap.put("rich-text", getFileToRichTextConverter());
		this.converters.put("file", fromFileMap);
	}

	public Converter getConverter(final String fromHandlerName, final String toHandlerName) {
		final Map<String, Converter> fromFileMap = this.converters.get(fromHandlerName);
		if (fromFileMap == null) {
			return null;
		}
		return fromFileMap.get(toHandlerName);
	}

	@Bean
	FileToRichTextConverter getFileToRichTextConverter() {
		return new FileToRichTextConverter();
	}
}
