package com.stephenschafer.ami.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.stephenschafer.ami.handler.FileHandler;
import com.stephenschafer.ami.service.ThingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
public class UploadController {
	@Autowired
	private FileHandler fileHandler;
	@Autowired
	private ThingService thingService;
	@Value("${ami.files.dir:./files}")
	private String filesDir;

	@SuppressWarnings("rawtypes")
	@GetMapping("/download/{thingId}/{attrDefnId}")
	public ResponseEntity download(@PathVariable final int thingId,
			@PathVariable final int attrDefnId) {
		log.info("GET /download/" + thingId + "/" + attrDefnId);
		final FileInfo value = fileHandler.getAttributeValue(thingId, attrDefnId);
		final Path path = Paths.get(filesDir + "/" + thingId + "/" + attrDefnId);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		}
		catch (final MalformedURLException e) {
			log.error("Failed to construct file URL", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok().contentType(
			MediaType.parseMediaType(value.getMimeType())).header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + value.getFilename() + "\"").body(resource);
	}

	@PostMapping("/upload")
	@ResponseBody
	public ApiResponse<FileInfo> singleFileUpload(@RequestParam("file") final MultipartFile file,
			@RequestParam("thingId") final Integer thingId,
			@RequestParam("attrId") final Integer attrId) {
		log.info("POST /upload " + file + ", " + thingId + ", " + attrId);
		if (file.isEmpty()) {
			return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(),
					"Pleaes select a file to upload", null);
		}
		try {
			final byte[] bytes = file.getBytes();
			final FileInfo fileInfo = thingService.saveFile(bytes, file.getOriginalFilename(),
				file.getContentType(), thingId, attrId);
			return new ApiResponse<>(HttpStatus.OK.value(),
					file.getOriginalFilename() + " successfully uploaded.", fileInfo);
		}
		catch (final IOException e) {
			log.error("failed to upload", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
					null);
		}
	}
}
