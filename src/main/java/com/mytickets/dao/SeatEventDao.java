package com.mytickets.dao;

import java.util.Calendar;

import com.google.inject.ImplementedBy;
import com.mytickets.dao.impl.SeatEventDaoImpl;
import com.mytickets.model.SeatHoldInformation;

@ImplementedBy(SeatEventDaoImpl.class)
public interface SeatEventDao {

	Integer getSeatsInLevel(int levelId, Calendar currentTime);

	SeatHoldInformation holdSeats(int numSeats, int minLevel, int maxLevel, String customerEmail,
			Calendar holdStartTime, Calendar holdEndTime);
}
