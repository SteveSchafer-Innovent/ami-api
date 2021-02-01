package com.stephenschafer.ami.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stephenschafer.ami.converter.Converter;
import com.stephenschafer.ami.converter.ConverterProvider;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.service.AttrDefnService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ConversionController {
	@Autowired
	private AttrDefnService attrDefnService;
	@Autowired
	private ConverterProvider converterProvider;

	@GetMapping("/convert/{thingId}/{fromAttrDefnId}/{toAttrDefnId}")
	public ApiResponse<Void> singleFileUpload(@RequestParam("thingId") final Integer thingId,
			@RequestParam("fromAttrDefnId") final Integer fromAttrDefnId,
			@RequestParam("toAttrDefnId") final Integer toAttrDefnId) {
		log.info("GET /convert " + thingId + ", " + fromAttrDefnId + ", " + toAttrDefnId);
		final AttrDefnEntity fromAttrDefn = attrDefnService.findById(fromAttrDefnId);
		if (fromAttrDefn == null) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(),
					"From attribute definition not found", null);
		}
		final String fromHandlerName = fromAttrDefn.getHandler();
		final AttrDefnEntity toAttrDefn = attrDefnService.findById(toAttrDefnId);
		if (toAttrDefn == null) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(),
					"To attribute definition not found", null);
		}
		final String toHandlerName = toAttrDefn.getHandler();
		final Converter converter = converterProvider.getConverter(fromHandlerName, toHandlerName);
		try {
			converter.convert(thingId, fromAttrDefn, toAttrDefn);
		}
		catch (final Exception e) {
			log.error("Failed to convert", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Conversion unsuccessful: " + e.getMessage(), null);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Conversion successful", null);
	}
}
