package com.stephenschafer.ami.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.jpa.AttrDefnDao;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.ThingDao;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.jpa.WordDao;
import com.stephenschafer.ami.jpa.WordEntity;
import com.stephenschafer.ami.jpa.WordThingDao;
import com.stephenschafer.ami.jpa.WordThingEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service(value = "wordService")
public class WordServiceImpl implements WordService {
	@Autowired
	private ThingDao thingDao;
	@Autowired
	private AttrDefnDao attrDefnDao;
	@Autowired
	private HandlerProvider handlerProvider;
	@Autowired
	private WordDao wordDao;
	@Autowired
	private WordThingDao wordThingDao;

	@Override
	public void updateIndex() {
		deleteIndex();
		for (final ThingEntity thing : thingDao.findAll()) {
			updateIndex(thing);
		}
	}

	@Override
	public void updateIndex(final ThingEntity thing) {
		log.info("updateIndex " + thing);
		deleteIndex(thing.getId());
		final int typeId = thing.getTypeId();
		final List<AttrDefnEntity> attrDefns = attrDefnDao.findByTypeId(typeId);
		for (final AttrDefnEntity attrDefn : attrDefns) {
			updateIndex(thing, attrDefn);
		}
	}

	@Override
	public void updateIndex(final ThingEntity thing, final AttrDefnEntity attrDefn) {
		log.info("updateIndex " + thing + ", " + attrDefn);
		deleteIndex(thing.getId(), attrDefn.getId());
		final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
		handler.updateIndex(thing, attrDefn);
	}

	@Override
	public void deleteIndex() {
		log.info("deleteIndex");
		wordThingDao.deleteAll();
		wordDao.deleteAll();
	}

	@Override
	public void deleteIndex(final int thingId) {
		log.info("deleteIndex " + thingId);
		wordThingDao.deleteByThingId(thingId);
	}

	@Override
	public void deleteIndex(final int thingId, final int attrDefnId) {
		log.info("deleteIndex " + thingId + ", " + attrDefnId);
		wordThingDao.deleteByThingIdAndAttrdefnId(thingId, attrDefnId);
	}

	private Set<WordEntity> getWordEntities(final String words) {
		final Set<WordEntity> entities = new HashSet<>();
		for (final String word : parseWords(words)) {
			final WordEntity entity = wordDao.findByWord(word);
			if (entity != null) {
				entities.add(entity);
			}
		}
		return entities;
	}

	@Override
	public Set<Integer> search(final String word) {
		log.info("search " + word);
		final Set<Integer> set = new HashSet<>();
		for (final WordEntity wordEntity : getWordEntities(word)) {
			final List<WordThingEntity> list = wordThingDao.findByWordId(wordEntity.getId());
			for (final WordThingEntity wordThingEntity : list) {
				set.add(wordThingEntity.getThingId());
			}
		}
		return set;
	}

	@Override
	public Set<Integer> search(final String word, final int attrDefnId) {
		log.info("search " + word + ", " + attrDefnId);
		final Set<Integer> set = new HashSet<>();
		for (final WordEntity wordEntity : getWordEntities(word)) {
			final List<WordThingEntity> list = wordThingDao.findByWordIdAndAttrdefnId(
				wordEntity.getId(), attrDefnId);
			for (final WordThingEntity wordThingEntity : list) {
				set.add(wordThingEntity.getThingId());
			}
		}
		return set;
	}

	@Override
	public Set<String> parseWords(final String value) {
		final Set<String> set = new HashSet<>();
		if (value == null) {
			return set;
		}
		final String[] strings = value.split("[^a-zA-Z0-9']+");
		for (String string : strings) {
			final String originalString = string;
			if (string.startsWith("'")) {
				string = string.substring(1);
			}
			if (string.endsWith("'")) {
				string = string.substring(0, string.length() - 1);
			}
			if (string.length() == 0) {
				continue;
			}
			string = string.toLowerCase();
			// replace most common english contractions
			string = string.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+$", "");
			string = stem(string);
			if (string != null) {
				set.add(string);
				if (!originalString.equals(string)) {
					log.info(originalString + " -> " + string);
				}
			}
		}
		return set;
	}

	public static String stem(final String term) {
		final Reader reader = new StringReader(term);
		// tokenize
		final ClassicTokenizer tokenizer = new ClassicTokenizer();
		try {
			try {
				tokenizer.setReader(reader);
				// stem
				final TokenStream tokenStream = new PorterStemFilter(tokenizer);
				try {
					// add each token in a set, so that duplicates are removed
					final Set<String> stems = new HashSet<String>();
					final CharTermAttribute token = tokenStream.getAttribute(
						CharTermAttribute.class);
					tokenStream.reset();
					while (tokenStream.incrementToken()) {
						stems.add(token.toString());
					}
					// if no stem or 2+ stems have been found, return null
					if (stems.size() != 1) {
						return null;
					}
					final String stem = stems.iterator().next();
					// if the stem has non-alphanumerical chars, return null
					if (!stem.matches("[a-zA-Z0-9-]+")) {
						return null;
					}
					return stem;
				}
				finally {
					tokenStream.close();
				}
			}
			finally {
				tokenizer.close();
			}
		}
		catch (final IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}
