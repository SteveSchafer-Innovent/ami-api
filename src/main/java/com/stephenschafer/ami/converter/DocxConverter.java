package com.stephenschafer.ami.converter;

import java.io.InputStream;
import java.util.Set;

import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;

@SuppressWarnings("unused")
public class DocxConverter implements MimeTypeConverter {
	@Override
	public String convert(final InputStream inputStream) {
		try {
			final DocumentConverter converter = new DocumentConverter();
			final Result<String> result = converter.convertToHtml(inputStream);
			final String html = result.getValue(); // The generated HTML
			final Set<String> warnings = result.getWarnings(); // Any warnings during conversion
			return html;
		}
		catch (final Exception e) {
			throw new RuntimeException("Failed to convert docx to HTML", e);
		}
	}
}