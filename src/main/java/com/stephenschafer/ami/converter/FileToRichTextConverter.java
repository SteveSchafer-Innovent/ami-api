package com.stephenschafer.ami.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.stephenschafer.ami.controller.FileInfo;
import com.stephenschafer.ami.controller.Request;
import com.stephenschafer.ami.handler.FileHandler;
import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.handler.RichTextHandler;
import com.stephenschafer.ami.jpa.AttrDefnEntity;

public class FileToRichTextConverter implements Converter {
	@Autowired
	private HandlerProvider handlerProvider;
	@Autowired
	private MimeTypeConverterProvider mimeTypeConverterProvider;
	@Value("${ami.files.dir:./files}")
	private String filesDir;

	@Override
	public void convert(final int thingId, final AttrDefnEntity fromAttrDefn,
			final AttrDefnEntity toAttrDefn) {
		final Handler fromHandler = handlerProvider.getHandler(fromAttrDefn.getHandler());
		if (!(fromHandler instanceof FileHandler)) {
			throw new RuntimeException("From handler is not a file handler");
		}
		final Handler toHandler = handlerProvider.getHandler(toAttrDefn.getHandler());
		if (!(toHandler instanceof RichTextHandler)) {
			throw new RuntimeException("To handler is not a rich text handler");
		}
		final Object fromObject = fromHandler.getAttributeValue(thingId, fromAttrDefn.getId());
		if (fromObject == null) {
			throw new RuntimeException("From attribute does not exist");
		}
		if (!(fromObject instanceof FileInfo)) {
			throw new RuntimeException("From object is not a FileInfo");
		}
		final FileInfo fileValue = (FileInfo) fromObject;
		final String filename = filesDir + "/" + thingId + "/" + fromAttrDefn.getId();
		final File file = new File(filename);
		FileInputStream fis = null;
		try {
			try {
				fis = new FileInputStream(file);
			}
			finally {
				fis.close();
			}
		}
		catch (final IOException e) {
			throw new RuntimeException("Failed to open or close: " + filename);
		}
		final MimeTypeConverter converter = mimeTypeConverterProvider.getConverter(
			fileValue.getMimeType());
		if (converter == null) {
			throw new RuntimeException("No converter found for " + fileValue.getMimeType());
		}
		final String convertedValue = converter.convert(fis);
		final Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put("thingId", thingId);
		attributeMap.put("attrDefnId", toAttrDefn.getId());
		attributeMap.put("value", convertedValue);
		toHandler.saveAttribute(new Request(attributeMap));
	}
}
