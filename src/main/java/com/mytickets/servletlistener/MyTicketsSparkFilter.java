package com.mytickets.servletlistener;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import spark.servlet.SparkApplication;
import spark.servlet.SparkFilter;

@Singleton
public class MyTicketsSparkFilter extends SparkFilter {

	@Inject
	private SparkApplication sparkApplication;

	@Override
	protected SparkApplication getApplication(FilterConfig filterConfig) throws ServletException {
		return sparkApplication;
	}
}
