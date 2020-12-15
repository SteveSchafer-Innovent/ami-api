package com.stephenschafer.ami.converter;

import java.io.InputStream;
import java.io.InputStreamReader;

public class PlainTextConverter implements MimeTypeConverter {
	@Override
	public String convert(final InputStream inputStream) {
		final InputStreamReader reader = new InputStreamReader(inputStream);
		final StringBuilder sb = new StringBuilder();
		sb.append("<pre>");
		try {
			final char[] buffer = new char[0x1000];
			int charsRead = reader.read(buffer);
			while (charsRead >= 0) {
				sb.append(buffer, 0, charsRead);
				charsRead = reader.read(buffer);
			}
		}
		catch (final Exception e) {
			throw new RuntimeException("Unable to read input stream", e);
		}
		sb.append("</pre>");
		return sb.toString();
	}
}