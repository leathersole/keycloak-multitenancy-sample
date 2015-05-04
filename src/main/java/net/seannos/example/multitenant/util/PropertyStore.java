package net.seannos.example.multitenant.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyStore {
	private static Properties props;

	private static void init(){
		props = new Properties();
		InputStream is = PropertyStore.class.getResourceAsStream(
				"/system.properties");
		try {
			props.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(String key) {
		if (null == props){
			init();
		}
		return props.getProperty(key);
	}
}
