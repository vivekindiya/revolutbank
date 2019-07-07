package com.revolut.fundtransfer.utils;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesLoader {

	private static Properties properties = new Properties();

	static Logger log = Logger.getLogger(PropertiesLoader.class);

	public static void loadConfigs(List<String> configFiles) {

		if (configFiles.isEmpty()) {
			log.warn("PropertiesLoader : No config files provided.");
		} else {

			configFiles.forEach(config -> {
				try {
					log.info("loadConfig(): Loading config file: " + config);
					final InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(config);
					properties.load(fis);
				} catch (FileNotFoundException fne) {
					log.error("loadConfig(): file name not found " + config, fne);
				} catch (IOException ioe) {
					log.error("loadConfig(): error when reading the config " + config, ioe);
				}
			});
		}
	}

	public static String getStringProperty(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			value = System.getProperty(key);
		}
		return value;
	}

	/**
	 * @param key:
	 *            property key
	 * @param defaultVal
	 *            the default value if the key not present in config file
	 * @return string property based on lookup key
	 */
	public static String getStringProperty(String key, String defaultVal) {
		String value = getStringProperty(key);
		return value != null ? value : defaultVal;
	}

	public static int getIntegerProperty(String key, int defaultVal) {
		String valueStr = getStringProperty(key);
		if (valueStr == null) {
			return defaultVal;
		} else {
			try {
				return Integer.parseInt(valueStr);

			} catch (Exception e) {
				log.warn("getIntegerProperty(): cannot parse integer from properties file for: " + key
						+ "fail over to default value: " + defaultVal, e);
				return defaultVal;
			}
		}
	}

	/**
	 * load all properties files
	 */

	static {
		List<String> configs = new ArrayList<String>();

		if (System.getProperty("application.properties") == null) {
			configs.add("application.properties");
		}
		if (System.getProperty("queries.properties") == null) {
			configs.add("application.properties");
		}
		loadConfigs(configs);
	}
}
