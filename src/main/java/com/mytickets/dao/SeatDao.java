package com.mytickets.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.inject.ImplementedBy;
import com.mytickets.dao.impl.SeatDaoImpl;
import com.mytickets.model.SeatAction;
import com.mytickets.model.SeatHoldInfo;
import com.mytickets.model.SeatLevel;
import com.mytickets.model.SeatReservation;
import com.mytickets.model.SeatsByLevel;

@ImplementedBy(SeatDaoImpl.class)
public interface SeatDao {

	List<SeatsByLevel> getSeatsInLevel(Calendar currentTime, Optional<Set<Integer>> levelIds);

	SeatHoldInfo holdSeats(Map<SeatLevel, Collection<SeatAction>> seatsLocToHoldByLevel, String customerEmail,
			Calendar holdStartTime, Calendar holdEndTime);

	List<SeatLevel> getAllLevels();

	Map<Integer, List<Integer>> getTakenLocationsByLevel(Set<Integer> levels);

	SeatReservation createReservation(Calendar createdTime, int seatHoldId, String customerEmail);
	
	Optional<SeatHoldInfo> getSeatHoldInfo(int seatHoldId);
	
	List<SeatLevel> createDefaultSeatLevels();
}
