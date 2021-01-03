package com.stephenschafer.ami.service;

import java.util.Set;

import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.ThingEntity;

public interface WordService {
	Set<Integer> search(String word);

	Set<Integer> search(String word, final int attrDefnId);

	void updateIndex();

	void updateIndex(final ThingEntity thing);

	void updateIndex(final ThingEntity thing, final AttrDefnEntity attrDefn);

	void deleteIndex();

	void deleteIndex(final int thingId);

	void deleteIndex(final int thingId, final int attrDefnId);

	Set<String> parseWords(String string);
}
