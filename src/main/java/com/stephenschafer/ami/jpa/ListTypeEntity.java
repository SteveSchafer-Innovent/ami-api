package com.stephenschafer.ami.jpa;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ListTypeEntity {
	private int id;
	private String name;
	private int thingCount;
	private int attrCount;
	private int sourceLinkCount;
	private int targetLinkCount;
}
