package com.munna.common.service.csvparser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class CSVParser {

	private final static Log log = LogFactory.getLog(CSVParser.class);
	
	private static int maxCharPerColumn = Integer.parseInt(System.getProperty("univocity.max.char.per.column", "-1"));

	public CsvParserSettings getCurrentCsvParserSettings(int recordtoRead, CsvFormat csvFormat, boolean excludeHead) {
		CsvParserSettings settings = null;
		log.info("Entering into getCurrentCsvParserSettings method..");
		try {
			settings = new CsvParserSettings();
			settings.setLineSeparatorDetectionEnabled(true);
			settings.setHeaderExtractionEnabled(excludeHead);
			settings.setMaxCharsPerColumn(maxCharPerColumn);
			//1000000 is the Limit...
			settings.setMaxColumns(1000000);
			settings.setNumberOfRecordsToRead(recordtoRead);
			if (csvFormat != null) {
				settings.setFormat(csvFormat);
			}
		} catch (Exception e) {
			log.error("can't able to get the csv parser settings due to : ", e);
			throw e;
		}
		return settings;
	}
	
	public CsvParser getCsvParser(CsvParserSettings parserSettings) {
		CsvParser csvParser = null;
		log.info("Entering into Csv parser method.");
		try {
			csvParser = new CsvParser(parserSettings);
		} catch (Exception e) {
			log.error("Can't able to create csv parser.", e);
		}
		return csvParser;
	}
	
	
}
