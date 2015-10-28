package com.mytickets.dao;

import java.util.Calendar;

import com.google.inject.ImplementedBy;
import com.mytickets.dao.impl.SeatEventDaoImpl;
import com.mytickets.model.SeatHoldInfo;

@ImplementedBy(SeatEventDaoImpl.class)
public interface SeatEventDao {

	Integer getSeatsInLevel(int levelId, Calendar currentTime);

	SeatHoldInfo holdSeats(int numSeats, int minLevel, int maxLevel, String customerEmail,
			Calendar holdStartTime, Calendar holdEndTime);
}
