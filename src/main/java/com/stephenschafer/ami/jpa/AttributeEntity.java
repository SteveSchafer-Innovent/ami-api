package com.stephenschafer.ami.jpa;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AttributeEntity {
	private int thingId;
	private int attrDefnId;
	private Object value;
}
