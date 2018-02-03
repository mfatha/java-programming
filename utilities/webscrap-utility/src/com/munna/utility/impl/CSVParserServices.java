package com.munna.utility.impl;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CSVParserServices {

	private final Log log = LogFactory.getLog(this.getClass());
	
	public void createDirectory(String outputDirectory) {
		File files = new File(outputDirectory);
		if (!files.exists()) {
			if (files.mkdirs()) {
				log.info("directories are created : " + outputDirectory);
			} else {
				log.error("Failed to create multiple directories : " + outputDirectory);
			}
		}
	}
}
