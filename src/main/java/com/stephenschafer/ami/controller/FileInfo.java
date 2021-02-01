package com.stephenschafer.ami.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FileInfo implements Comparable<FileInfo> {
	private final String filename;
	private final String mimeType;
	private final String richText;

	public FileInfo(final Request request) {
		filename = request.getString("filename");
		mimeType = request.getString("mimeType");
		richText = request.getString("richText");
	}

	@Override
	public int compareTo(final FileInfo that) {
		int comparison = this.filename == null ? that.filename == null ? 0 : -1
			: that.filename == null ? 1 : this.filename.compareTo(that.filename);
		if (comparison != 0) {
			return comparison;
		}
		comparison = this.mimeType == null ? that.mimeType == null ? 0 : -1
			: that.mimeType == null ? 1 : this.mimeType.compareTo(that.mimeType);
		return comparison;
	}
}