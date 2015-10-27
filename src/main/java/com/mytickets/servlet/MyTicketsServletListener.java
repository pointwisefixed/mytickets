package com.mytickets.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.mytickets.rest.service.ApiServer;
import com.mytickets.servletlistener.MyTicketsSparkFilter;

import spark.servlet.SparkApplication;

public class MyTicketsServletListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		final Injector injector = Guice.createInjector(new ServletModule() {
			@Override
			protected void configureServlets() {
				super.configureServlets();
				try {
					Properties props = new Properties();
					InputStream config = Resources.getResource("config.properties").openStream();
					props.load(config);
					config.close();
					Names.bindProperties(binder(), props);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				bind(SparkApplication.class).to(ApiServer.class);
				install(new JpaPersistModule("myTicketsInMem"));
				filter("/*").through(PersistFilter.class);
				filter("/*").through(MyTicketsSparkFilter.class);
			}
		});
		return injector;
	}

}
