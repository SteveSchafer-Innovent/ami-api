package com.stephenschafer.ami.service;

import java.util.HashSet;
import java.util.Set;

import com.stephenschafer.ami.jpa.ThingEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchResult {
	private final Set<Integer> typeIds = new HashSet<>();
	private final Set<Integer> thingIds = new HashSet<>();

	public void add(final ThingEntity thing) {
		typeIds.add(thing.getTypeId());
		thingIds.add(thing.getId());
	}
}
