package com.stephenschafer.ami.handler;

import java.util.Set;

import javax.transaction.Transactional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.service.WordService;

@Transactional
@Service
public class RichTextHandler extends StringHandler {
	@Autowired
	private WordService wordService;

	@Override
	protected Set<String> getWords(final ThingEntity thing, final AttrDefnEntity attrDefn) {
		final String value = (String) this.getAttributeValue(thing, attrDefn);
		final Document doc = Jsoup.parse(value);
		final String text = doc.text();
		return wordService.parseWords(text);
	}
}
