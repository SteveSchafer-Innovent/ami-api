package com.stephenschafer.ami.service;

import java.util.Set;

public interface WordService {
	Set<Integer> search(String word);

	Set<Integer> searchByType(String word, final int typeId);

	Set<Integer> searchByAttribute(String word, final int attrDefnId);

	void updateIndex();

	void updateIndex(final int thingId);

	void updateIndex(final int thingId, final int attrDefnId);

	void deleteIndex();

	void deleteIndex(final int thingId);

	void deleteIndex(final int thingId, final int attrDefnId);

	Set<String> parseWords(String string);
}
