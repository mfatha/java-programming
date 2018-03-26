package com.munna.common.properties;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author Mohammed Fathauddin
 * @since 2017
 */

public class PropertiesProvider {
	private PropertiesProvider() {
	}

	private static PropertiesProvider propertiesProvider;

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesProvider.class);

	private Map<String, Properties> propertiesMap = new HashMap<String, Properties>();

	public static PropertiesProvider getInstance() {
		if (propertiesProvider == null) {
			synchronized (PropertiesProvider.class) {
				if (propertiesProvider == null) {
					propertiesProvider = new PropertiesProvider();
				}
			}
		}
		return propertiesProvider;
	}

	public Properties getProperties(String file) {
		if (!propertiesMap.containsKey(file)) {
			loadProperty(file);
		}
		return propertiesMap.get(file);
	}

	private void loadProperty(String file) {
		Properties property = null;
		InputStream inStream;
		try {
			inStream = new FileInputStream(file);
			property = new Properties();
			property.load(inStream);
			propertiesMap.put(file, property);
		} catch (Exception e) {
			LOGGER.error("Error while loading the property", e);
		}
	}

}
