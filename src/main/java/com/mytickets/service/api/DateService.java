package com.mytickets.service.api;

import java.util.Calendar;

import com.google.inject.ImplementedBy;
import com.mytickets.service.impl.DateServiceImpl;

@ImplementedBy(DateServiceImpl.class)
public interface DateService {

	Calendar getCurrentTime();
}
