package com.stephenschafer.ami.converter;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Configuration
public class MimeTypeConverterProvider {
	@Autowired
	private PlainTextConverter plainTextConverter;
	@Autowired
	private DocConverter docConverter;
	@Autowired
	private DocxConverter docxConverter;
	private final Map<String, MimeTypeConverter> converters = new HashMap<>();

	@PostConstruct
	private void init() {
		if (plainTextConverter == null) {
			log.info("plainTextConverter is null");
		}
		converters.put("text/plain", plainTextConverter);
		if (docConverter == null) {
			log.info("docConverter is null");
		}
		converters.put("application/msword", docConverter);
		if (docxConverter == null) {
			log.info("docxConverter is null");
		}
		converters.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			docxConverter);
	}

	public MimeTypeConverter getConverter(final String mimeType) {
		return converters.get(mimeType);
	}

	@Bean
	public PlainTextConverter getPlainTextConverter() {
		return new PlainTextConverter();
	}

	@Bean
	public DocConverter getDocConverter() {
		return new DocConverter();
	}

	@Bean
	public DocxConverter getDocxConverter() {
		return new DocxConverter();
	}
}
