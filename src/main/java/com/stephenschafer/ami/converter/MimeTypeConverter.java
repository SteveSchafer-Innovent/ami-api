package com.stephenschafer.ami.converter;

import java.io.InputStream;

public interface MimeTypeConverter {
	String convert(InputStream inputStream);
}