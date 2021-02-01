package com.stephenschafer.ami.service;

import java.util.Set;
import java.util.concurrent.Future;

public interface WordService {
	Set<Integer> search(String word);

	Set<Integer> searchByType(String word, int typeId);

	Set<Integer> searchByAttribute(String word, int attrDefnId);

	void rebuildIndex();

	void submitRebuildIndex();

	void updateIndex();

	void submitUpdateIndex();

	Future<Void> rebuildFuture();

	void updateIndex(int thingId);

	void updateIndex(int thingId, int attrDefnId);

	void updateIndex(int thingId, int attrDefnId, Set<String> words);

	void deleteIndex();

	void deleteIndex(int thingId);

	void deleteIndex(int thingId, int attrDefnId);

	Set<String> parseWords(String string);

	Exception getLastRebuildException();
}
