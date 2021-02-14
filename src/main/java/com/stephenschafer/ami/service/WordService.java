package com.stephenschafer.ami.service;

import java.util.Set;
import java.util.concurrent.Future;

import com.stephenschafer.ami.jpa.ThingEntity;

public interface WordService {
	Set<ThingEntity> search(String word);

	Set<ThingEntity> searchByType(String word, int typeId);

	Set<ThingEntity> searchByAttribute(String word, int attrDefnId);

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
