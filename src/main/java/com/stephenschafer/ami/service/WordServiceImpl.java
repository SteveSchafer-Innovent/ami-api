package com.stephenschafer.ami.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
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

	private void updateIndex(final ThingEntity thing) {
		log.info("updateIndex " + thing);
		deleteIndex(thing.getId());
		final int typeId = thing.getTypeId();
		final List<AttrDefnEntity> attrDefns = attrDefnDao.findByTypeId(typeId);
		for (final AttrDefnEntity attrDefn : attrDefns) {
			updateIndex(thing, attrDefn);
		}
	}

	@Override
	public void updateIndex(final int thingId) {
		log.info("updateIndex " + thingId);
		final Optional<ThingEntity> optional = thingDao.findById(thingId);
		if (!optional.isPresent()) {
			throw new RuntimeException("Thing not found for id " + thingId);
		}
		final ThingEntity thing = optional.get();
		updateIndex(thing);
	}

	private void updateIndex(final ThingEntity thing, final AttrDefnEntity attrDefn) {
		log.info("updateIndex " + thing + ", " + attrDefn);
		deleteIndex(thing.getId(), attrDefn.getId());
		final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
		handler.updateIndex(thing.getId(), attrDefn.getId());
	}

	@Override
	public void updateIndex(final int thingId, final int attrDefnId) {
		log.info("updateIndex " + thingId + ", " + attrDefnId);
		final Optional<AttrDefnEntity> optional = attrDefnDao.findById(attrDefnId);
		if (!optional.isPresent()) {
			throw new RuntimeException("AttrDefn not found for id " + attrDefnId);
		}
		final AttrDefnEntity attrDefn = optional.get();
		final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
		handler.updateIndex(thingId, attrDefnId);
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
		log.info("WordService.search " + word);
		final Set<Integer> set = new HashSet<>();
		for (final WordEntity wordEntity : getWordEntities(word)) {
			final List<WordThingEntity> list = wordThingDao.findByWordId(wordEntity.getId());
			for (final WordThingEntity wordThingEntity : list) {
				set.add(wordThingEntity.getThingId());
			}
		}
		log.info("  results: " + set.size());
		return set;
	}

	@Override
	public Set<Integer> searchByType(final String word, final int typeId) {
		log.info("WordService.searchByType " + word + ", " + typeId);
		final Set<Integer> set = new HashSet<>();
		final Set<WordEntity> wordEntities = getWordEntities(word);
		for (final AttrDefnEntity attrdefn : attrDefnDao.findByTypeId(typeId)) {
			for (final WordEntity wordEntity : wordEntities) {
				final List<WordThingEntity> list = wordThingDao.findByWordIdAndAttrdefnId(
					wordEntity.getId(), attrdefn.getId());
				for (final WordThingEntity wordThingEntity : list) {
					set.add(wordThingEntity.getThingId());
				}
			}
		}
		log.info("  results: " + set.size());
		return set;
	}

	@Override
	public Set<Integer> searchByAttribute(final String word, final int attrDefnId) {
		log.info("WordService.searchByAttribute " + word + ", " + attrDefnId);
		final Set<Integer> set = new HashSet<>();
		for (final WordEntity wordEntity : getWordEntities(word)) {
			final List<WordThingEntity> list = wordThingDao.findByWordIdAndAttrdefnId(
				wordEntity.getId(), attrDefnId);
			for (final WordThingEntity wordThingEntity : list) {
				set.add(wordThingEntity.getThingId());
			}
		}
		log.info("  results: " + set.size());
		return set;
	}

	// see https://stackoverflow.com/questions/17447045/java-library-for-keywords-extraction-from-input-text
	@Override
	public Set<String> parseWords(String value) {
		final Set<String> set = new HashSet<>();
		if (value == null) {
			return set;
		}
		// replace any punctuation char but apostrophes and dashes by a space
		value = value.replaceAll("[\\p{Punct}&&[^'-]]+", " ");
		// replace most common english contractions
		value = value.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");
		final Reader reader = new StringReader(value);
		final ClassicTokenizer tokenizer = new ClassicTokenizer();
		try {
			try {
				tokenizer.setReader(reader);
				TokenStream tokenStream = tokenizer;
				tokenStream = new LowerCaseFilter(tokenStream);
				tokenStream = new ClassicFilter(tokenStream);
				tokenStream = new StopFilter(tokenStream, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
				tokenStream = new PorterStemFilter(tokenStream);
				try {
					// add each token in a set, so that duplicates are removed
					final Set<String> stems = new HashSet<String>();
					final CharTermAttribute token = tokenStream.getAttribute(
						CharTermAttribute.class);
					tokenStream.reset();
					while (tokenStream.incrementToken()) {
						stems.add(token.toString());
					}
					return stems;
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
		}
		/*
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
		*/
		return set;
	}

	public static String stem(final String term) {
		final Reader reader = new StringReader(term);
		final ClassicTokenizer tokenizer = new ClassicTokenizer();
		try {
			try {
				tokenizer.setReader(reader);
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
