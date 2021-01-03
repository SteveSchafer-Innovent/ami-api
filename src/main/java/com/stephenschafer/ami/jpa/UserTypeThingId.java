package com.stephenschafer.ami.jpa;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserTypeThingId implements Serializable {
	private static final long serialVersionUID = 1L;
	private int userId;
	private int typeId;
	private int thingId;
}
