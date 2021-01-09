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
	private HtmlConverter htmlConverter;
	@Autowired
	private DocConverter docConverter;
	@Autowired
	private DocxConverter docxConverter;
	@Autowired
	private PdfConverter pdfConverter;
	private final Map<String, MimeTypeConverter> converters = new HashMap<>();

	@PostConstruct
	private void init() {
		if (plainTextConverter == null) {
			log.info("plainTextConverter is null");
		}
		else {
			converters.put("text/plain", plainTextConverter);
		}
		if (htmlConverter == null) {
			log.info("htmlConverter is null");
		}
		else {
			converters.put("text/html", htmlConverter);
		}
		if (docConverter == null) {
			log.info("docConverter is null");
		}
		else {
			converters.put("application/msword", docConverter);
		}
		if (docxConverter == null) {
			log.info("docxConverter is null");
		}
		else {
			converters.put(
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				docxConverter);
		}
		if (pdfConverter == null) {
			log.info("pdfConverter is null");
		}
		else {
			converters.put("application/pdf", pdfConverter);
		}
	}

	public MimeTypeConverter getConverter(final String mimeType) {
		return converters.get(mimeType);
	}

	@Bean
	public PlainTextConverter getPlainTextConverter() {
		return new PlainTextConverter();
	}

	@Bean
	public HtmlConverter getHtmlConverter() {
		return new HtmlConverter();
	}

	@Bean
	public DocConverter getDocConverter() {
		return new DocConverter();
	}

	@Bean
	public DocxConverter getDocxConverter() {
		return new DocxConverter();
	}

	@Bean
	public PdfConverter getPdfConverter() {
		return new PdfConverter();
	}
}
