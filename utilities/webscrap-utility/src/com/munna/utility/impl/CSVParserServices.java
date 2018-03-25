package com.munna.utility.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVParserServices {

	private static final Logger LOGGER = LoggerFactory.getLogger(CSVParserServices.class);

	public void createDirectory(String outputDirectory) {
		File files = new File(outputDirectory);
		if (!files.exists()) {
			if (files.mkdirs()) {
				LOGGER.info("directories are created : " + outputDirectory);
			} else {
				LOGGER.error("Failed to create multiple directories : " + outputDirectory);
			}
		}
	}
}
