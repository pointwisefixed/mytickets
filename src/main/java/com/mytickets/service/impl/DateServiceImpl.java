package com.mytickets.service.impl;

import java.util.Calendar;

import com.mytickets.service.api.DateService;

public class DateServiceImpl implements DateService{

	@Override
	public Calendar getCurrentTime() {
		return Calendar.getInstance();
	}

}
