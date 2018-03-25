package com.munna.utility.impl;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.utility.cache.WebSurfConstants;

/*
 * @author Mohammed Fathauddin
 * @since 2017
 */
public class JsoupServices {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsoupServices.class);

	private JsoupServices() {
	}

	private static JsoupServices jsoup;

	private Map<String, Document> connectionMap = new HashMap<String, Document>();

	public static JsoupServices getInstance() {
		synchronized (JsoupServices.class) {
			if (jsoup == null) {
				jsoup = new JsoupServices();
			}
		}
		return jsoup;
	}

	public Document setConnection(String url) {
		try {
			final String userAgent = UAProvider.randomUA();
			Connection.Response response = Jsoup.connect(url).userAgent(userAgent).timeout(10000).execute();
			if (response.statusCode() == 200) {
				Document doc = (Document) Jsoup.connect(url).userAgent(userAgent).timeout(10000).get();
				LOGGER.info("Jsoup Connection Established, for " + url);
				// connectionMap.put(url, doc);
				return doc;
			} else {
				LOGGER.error("error in connecting to .." + url);
				return null;
			}
		} catch (Exception e) {
			LOGGER.error("Error in getting JSoup Connection [" + url + "]: " + e);
			// connectionMap.put(url, null);
			return null;
		}
	}

	public void initConnection(String url) {
		Document soupDoc = setConnection(url);
		boolean retry = true;
		int retryCount = 1;
		while (retry) {
			if (soupDoc != null) {
				connectionMap.put(url, soupDoc);
				retry = false;
			} else {
				LOGGER.error("cant able to connect to " + url);
				if (retryCount >= WebSurfConstants.RETRY_COUNT) {
					retry = false;
					connectionMap.put(url, null);
					LOGGER.error("Retry Attempts Also Failed... " + url);
				} else {
					retryCount++;
					LOGGER.info("Retrying to Connect For : " + retryCount + " Time : " + url);
					soupDoc = setConnection(url);
				}
			}
		}
	}

	public Document getConnection(String url) {
		if (connectionMap.containsKey(url)) {
			return connectionMap.get(url);
		} else {
			initConnection(url);
			return getConnection(url);
		}
	}

	public void closeConnection(String url) {
		if (connectionMap.containsKey(url)) {
			connectionMap.remove(url);
			LOGGER.info("Connection closed.. (" + url + ")");
		} else {
			LOGGER.error("Connection not Found .. (" + url + ")");
		}
	}

	public void clearConnections() {
		if (!connectionMap.isEmpty()) {
			connectionMap.clear();
		}
		LOGGER.info("All Jsoup Document Connections are cleared..");
	}
}
