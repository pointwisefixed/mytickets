package com.mytickets.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class TestConfigModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			Properties props = new Properties();
			InputStream config = Resources.getResource("config.properties").openStream();
			props.load(config);
			config.close();
			Names.bindProperties(binder(), props);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
