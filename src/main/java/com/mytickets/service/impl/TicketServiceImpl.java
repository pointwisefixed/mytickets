package com.mytickets.service.impl;

import java.util.Optional;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mytickets.dao.VenueLevelDao;
import com.mytickets.model.SeatHold;
import com.mytickets.service.api.TicketService;

public class TicketServiceImpl implements TicketService {

	@Inject
	private VenueLevelDao venueLevelDao;

	@Override
	public int numSeatsAvailable(Optional<Integer> venueLevel) {
		int result = -1;
		if (venueLevel.isPresent()) {
			result = venueLevelDao.getSeatsInLevel(venueLevel.get());
		}
		return result;
	}

	@Override
	@Transactional
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
			String customerEmail) {
		
		return null;
	}

	@Override
	@Transactional
	public String reserveSeats(int seatHoldId, String customerEmail) {
		// TODO Auto-generated method stub
		return null;
	}

}
