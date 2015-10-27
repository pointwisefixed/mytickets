package com.mytickets.service.impl;

import java.util.Calendar;
import java.util.Optional;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.mytickets.dao.SeatEventDao;
import com.mytickets.model.SeatReservation;
import com.mytickets.service.api.SeatHold;
import com.mytickets.service.api.TicketService;

public class TicketServiceImpl implements TicketService {

	@Inject
	private SeatEventDao seatEventDao;
	@Inject
	@Named("seatHoldTimeInSeconds")
	private Integer seatHoldTimeInSeconds;

	@Override
	public int numSeatsAvailable(Optional<Integer> venueLevel) {
		int freeSeats = -1;
		if (venueLevel.isPresent()) {
			Calendar currentTime = Calendar.getInstance();
			freeSeats = seatEventDao.getSeatsInLevel(venueLevel.get(), currentTime);
		}
		return freeSeats;
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
			String customerEmail) {
		int ml = minLevel.orElse(Integer.MIN_VALUE);
		int mx = maxLevel.orElse(Integer.MAX_VALUE);
		Calendar startTime = Calendar.getInstance();
		Calendar endTime = (Calendar) startTime.clone();
		endTime.add(Calendar.SECOND, seatHoldTimeInSeconds);
		return seatEventDao.holdSeats(numSeats, ml, mx, customerEmail, startTime, endTime).toSeatHold();
	}

	@Override
	@Transactional
	public String reserveSeats(int seatHoldId, String customerEmail) {
		SeatReservation reservation = new SeatReservation();

		return reservation.getId();
	}

}
