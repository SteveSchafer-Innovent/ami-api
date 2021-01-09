package com.stephenschafer.ami.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class EmailProperties {
	private int userId;
	private String protocol;
	private String host;
	private String port;
	private String username;
	private String password;
	private int thingId;
}