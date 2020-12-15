package com.stephenschafer.ami.converter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.converter.WordToHtmlUtils;
import org.w3c.dom.Document;

public class DocConverter implements MimeTypeConverter {
	@Override
	public String convert(final InputStream inputStream) {
		try {
			final HWPFDocumentCore wordDocument = WordToHtmlUtils.loadDoc(inputStream);
			final WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
					DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
			wordToHtmlConverter.processDocument(wordDocument);
			final Document htmlDocument = wordToHtmlConverter.getDocument();
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final DOMSource domSource = new DOMSource(htmlDocument);
			final StreamResult streamResult = new StreamResult(out);
			final TransformerFactory tf = TransformerFactory.newInstance();
			final Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(OutputKeys.METHOD, "html");
			serializer.transform(domSource, streamResult);
			out.close();
			final String result = new String(out.toByteArray());
			return result;
		}
		catch (final Exception e) {
			throw new RuntimeException("Failed to convert doc to HTML", e);
		}
	}
}