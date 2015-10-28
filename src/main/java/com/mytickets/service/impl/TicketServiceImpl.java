package com.mytickets.service.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.mytickets.dao.SeatDao;
import com.mytickets.model.SeatAction;
import com.mytickets.model.SeatHoldInfo;
import com.mytickets.model.SeatLevel;
import com.mytickets.model.SeatsByLevel;
import com.mytickets.service.api.SeatHold;
import com.mytickets.service.api.TicketService;

public class TicketServiceImpl implements TicketService {

	@Inject
	private SeatDao seatEventDao;
	@Inject
	@Named("seatHoldTimeInSeconds")
	private Integer seatHoldTimeInSeconds;

	@Override
	public int numSeatsAvailable(Optional<Integer> venueLevel) {
		Calendar currentTime = Calendar.getInstance();
		List<SeatsByLevel> freeSeats = seatEventDao.getSeatsInLevel(currentTime, Optional.empty());
		Map<Integer, Integer> seatByLevel = freeSeats.stream()
				.collect(Collectors.toMap(x -> x.getLevelId(), x -> x.getAvailableSeats()));
		if (venueLevel.isPresent()) {
			return seatByLevel.get(venueLevel.get());
		}
		return seatByLevel.values().stream().mapToInt(x -> x).sum();
	}

	@Override
	@Transactional
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
			String customerEmail) {
		int ml = minLevel.orElse(Integer.MIN_VALUE);
		int mx = maxLevel.orElse(Integer.MAX_VALUE);
		Calendar startTime = Calendar.getInstance();
		Calendar endTime = (Calendar) startTime.clone();
		endTime.add(Calendar.SECOND, seatHoldTimeInSeconds);
		List<SeatLevel> seatLevels = seatEventDao.getAllLevels();
		Set<SeatLevel> filteredLevels = seatLevels.stream().filter(l -> {
			return l.getLevelId() >= ml && l.getLevelId() <= mx;
		}).collect(Collectors.toSet());
		Set<Integer> levelIds = filteredLevels.stream().mapToInt(x -> x.getLevelId()).boxed()
				.collect(Collectors.toSet());
		Map<SeatLevel, Collection<SeatAction>> seatLocToHoldByLevel = calculateLocToHoldByLevel(
				bestNumberOfSeatsToHoldByLevel(numSeats, levelIds, startTime),
				seatEventDao.getTakenLocationsByLevel(levelIds), seatLevels);
		return seatEventDao.holdSeats(seatLocToHoldByLevel, customerEmail, startTime, endTime).toSeatHold();
	}

	private Map<SeatLevel, Collection<SeatAction>> calculateLocToHoldByLevel(Map<Integer, Integer> seatsToHoldByLevel,
			Map<Integer, List<Integer>> takenLocationsByLevel, List<SeatLevel> seatLevels) {
		Map<Integer, SeatLevel> seatLevelById = seatLevels.stream()
				.collect(Collectors.toMap(x -> x.getLevelId(), v -> v));
		Map<SeatLevel, Collection<SeatAction>> seatActionsToHoldByLevel = new HashMap<>();
		for (Integer seatLevelToHold : seatsToHoldByLevel.keySet()) {
			SeatLevel sl = seatLevelById.get(seatLevelToHold);
			List<Integer> takenLocations = takenLocationsByLevel.get(seatLevelToHold);
			Integer numOfSeatsToHold = seatsToHoldByLevel.get(seatLevelToHold);
			Set<SeatAction> seatActionsToHold = sl.getSeatActionsToHold(takenLocations, numOfSeatsToHold);
			if (!seatActionsToHold.isEmpty()) {
				seatActionsToHoldByLevel.put(sl, seatActionsToHold);
			}
		}
		return seatActionsToHoldByLevel;
	}

	public Map<Integer, Integer> bestNumberOfSeatsToHoldByLevel(int numSeats, Set<Integer> levelIds,
			Calendar holdStartTime) {
		List<SeatsByLevel> seatCountByLevel = seatEventDao.getSeatsInLevel(holdStartTime, Optional.of(levelIds));
		seatCountByLevel
				.sort((o1, o2) -> o1.getLevelId() == o2.getLevelId() ? 0 : o1.getLevelId() > o2.getLevelId() ? 1 : -1);
		Map<Integer, Integer> numberOfSeatsToHoldByLevel = new HashMap<>();
		int numSeatsLeftToHold = numSeats;
		for (SeatsByLevel seatsByLevel : seatCountByLevel) {
			if (numSeatsLeftToHold <= 0)
				break;
			numSeatsLeftToHold = numSeatsLeftToHold - seatsByLevel.getAvailableSeats();
			if (numSeatsLeftToHold >= 0) {
				numberOfSeatsToHoldByLevel.put(seatsByLevel.getLevelId(), seatsByLevel.getAvailableSeats());
			} else {
				numberOfSeatsToHoldByLevel.put(seatsByLevel.getLevelId(),
						numSeatsLeftToHold + seatsByLevel.getAvailableSeats());
			}
		}
		return numberOfSeatsToHoldByLevel;
	}

	@Override
	@Transactional
	public String reserveSeats(int seatHoldId, String customerEmail) {
		Optional<SeatHoldInfo> seatHold = seatEventDao.getSeatHoldInfo(seatHoldId);
		Calendar now = Calendar.getInstance();
		if (!seatHold.isPresent() || seatHold.get().getSeatHoldEndTime().compareTo(now) <= 0)
			return null;
		return seatEventDao.createReservation(seatHoldId, customerEmail).getId();
	}

}
