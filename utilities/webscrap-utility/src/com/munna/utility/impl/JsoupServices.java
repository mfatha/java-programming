package com.munna.utility.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.munna.utility.cache.WebSurfConstants;

/*
 * @author Mohammed Fathauddin
 * @since 2017
 */
public class JsoupServices {

	private final Log log = LogFactory.getLog(this.getClass());

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
			Connection.Response response  = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(10000)
                    .execute();
			if(response.statusCode() == 200) {
				Document doc = (Document) Jsoup.connect(url).userAgent(
						"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
						.timeout(10000).get();
				log.info("Jsoup Connection Established, for " + url);
				//connectionMap.put(url, doc);
				return doc;
			}else {
				log.error("error in connecting to .." + url);
				return null;
			}
		} catch (Exception e) {
			log.error("Error in getting JSoup Connection ["+url+"]: "+ e);
			//connectionMap.put(url, null);
			return null;
		}
	}

	public void initConnection(String url) {
		Document soupDoc = setConnection(url);
		boolean retry = true;
		int retryCount = 1;
		while(retry) {
			if (soupDoc != null) {
				connectionMap.put(url, soupDoc);
				retry = false;
			} else {
				log.error("cant able to connect to " + url);
				if(retryCount >= WebSurfConstants.RETRY_COUNT) {
					retry = false;
					connectionMap.put(url, null);
					log.error("Retry Attempts Also Failed... " + url);
				}else {
					retryCount++;
					log.info("Retrying to Connect For : "+ retryCount +" Time : " + url );
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
			log.info("Connection closed.. (" + url + ")");
		} else {
			log.error("Connection not Found .. (" + url + ")");
		}
	}

	public void clearConnections() {
		if (!connectionMap.isEmpty()) {
			connectionMap.clear();
		}
		log.info("All Jsoup Document Connections are cleared..");
	}
}
