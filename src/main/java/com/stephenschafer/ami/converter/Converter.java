package com.stephenschafer.ami.converter;

import com.stephenschafer.ami.jpa.AttrDefnEntity;

public interface Converter {
	void convert(int thingId, AttrDefnEntity fromAttrDefn, AttrDefnEntity toAttrDefn);
}
