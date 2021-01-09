package com.stephenschafer.ami.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FileInfo {
	public FileInfo(final Request request) {
		filename = request.getString("filename");
		mimeType = request.getString("mimeType");
		richText = request.getString("richText");
	}

	private String filename;
	private String mimeType;
	private String richText;
}