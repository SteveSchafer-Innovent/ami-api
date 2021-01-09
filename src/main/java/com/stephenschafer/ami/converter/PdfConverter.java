package com.stephenschafer.ami.converter;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;

public class PdfConverter implements MimeTypeConverter {
	@Override
	public String convert(final InputStream inputStream) {
		try {
			// https://www.baeldung.com/pdf-conversions-java
			final PDDocument pdf = PDDocument.load(inputStream);
			final PDFDomTree pdfDomTree = new PDFDomTree();
			final StringWriter writer = new StringWriter();
			pdfDomTree.writeText(pdf, writer);
			final String string = writer.toString();
			pdf.close();
			return string;
		}
		catch (final Exception e) {
			throw new RuntimeException("Unable to read input stream", e);
		}
	}
}