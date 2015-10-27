package com.mytickets.rest.service;

import spark.servlet.SparkApplication;

public class ApiServer implements SparkApplication {

	@Override
	public void init() {
		
	}

	@Override
	public void destroy() {
		SparkApplication.super.destroy();
	}

}
