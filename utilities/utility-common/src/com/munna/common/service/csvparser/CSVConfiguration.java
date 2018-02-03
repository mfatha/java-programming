package com.munna.common.service.csvparser;

/*
 * Configuration Object for CSV parser
 * @author Mohammed Fathauddin
 * @since 12/27/2017
 */

public class CSVConfiguration implements java.io.Serializable{
	
	private char comment;

	private char delimiter;

	private String lineSeparator;

	private char normalizedNewline;
	
	private char charToEscapeQuoteEscaping;

	private char quote;

	private char quoteEscape;
	
	private static final long serialVersionUID = 1L;

	public char getComment() {
		return comment;
	}

	public void setComment(char comment) {
		this.comment = comment;
	}

	public char getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public char getNormalizedNewline() {
		return normalizedNewline;
	}

	public void setNormalizedNewline(char normalizedNewline) {
		this.normalizedNewline = normalizedNewline;
	}

	public char getCharToEscapeQuoteEscaping() {
		return charToEscapeQuoteEscaping;
	}

	public void setCharToEscapeQuoteEscaping(char charToEscapeQuoteEscaping) {
		this.charToEscapeQuoteEscaping = charToEscapeQuoteEscaping;
	}

	public String getLineSeparator() {
		return lineSeparator;
	}

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	public char getQuoteEscape() {
		return quoteEscape;
	}

	public void setQuoteEscape(char quoteEscape) {
		this.quoteEscape = quoteEscape;
	}

	public char getQuote() {
		return quote;
	}

	public void setQuote(char quote) {
		this.quote = quote;
	}

}
