package com.stephenschafer.ami.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FileData {
	private String filename;
	private String mimeType;
	private byte[] bytes;
}