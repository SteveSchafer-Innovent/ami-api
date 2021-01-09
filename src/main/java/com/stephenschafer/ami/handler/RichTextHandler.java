package com.stephenschafer.ami.handler;

import java.util.Set;

import javax.transaction.Transactional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.service.WordService;

@Transactional
@Service
public class RichTextHandler extends StringHandler {
	@Autowired
	private WordService wordService;

	@Override
	protected Set<String> getWords(final int thingId, final int attrDefnId) {
		final String value = (String) this.getAttributeValue(thingId, attrDefnId);
		final Document doc = Jsoup.parse(value);
		final String text = doc.text();
		return wordService.parseWords(text);
	}

	@Override
	public String getHandlerName() {
		return "rich-text";
	}
}
