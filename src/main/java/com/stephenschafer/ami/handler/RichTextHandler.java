package com.stephenschafer.ami.handler;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.service.WordService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
public class RichTextHandler extends StringHandler {
	@Autowired
	private WordService wordService;

	@Override
	protected Set<String> getWords(final int thingId, final int attrDefnId) {
		log.info("rich-text getWords " + thingId + ", " + attrDefnId);
		final String value = getAttributeValue(thingId, attrDefnId);
		if (value == null) {
			log.info("  value is null");
			return new HashSet<>();
		}
		log.info("  value = " + value);
		final Document doc = Jsoup.parse(value);
		final String text = doc.text();
		log.info("  text = " + text);
		final Set<String> words = wordService.parseWords(text);
		log.info("  words = " + words);
		return words;
	}

	@Override
	public String getHandlerName() {
		return "rich-text";
	}
}
