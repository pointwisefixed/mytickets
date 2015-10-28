package com.mytickets.dao.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.mytickets.dao.SeatDao;
import com.mytickets.model.SeatHoldInfo;
import com.mytickets.model.SeatLevel;
import com.mytickets.model.SeatReservation;
import com.mytickets.model.SeatsByLevel;

public class SeatDaoImpl implements SeatDao {

	private static final String GET_SEAT_COUNT_JQL = "select l.levelId as levelId"
			+ "(l.rows * l.numOfSeatInRows) as totalSeats, (select count(sa.id) "
			+ "from SeatAction sa inner join fetch sa.hold h left join fetch h.reservation r "
			+ "where sa.seatLevel in :levelId and (h.seatHoldEndTime < :holdStartTime or "
			+ "r.id is not null)) as reservedOrOnHoldSeats, (totalSeats - reservedOrOnHoldSeats) as availableSeats "
			+ "from SeatLevel l where l.levelId in :levelIds group by l.levelId order by l.levelId asc";

	private static final String GET_ALL_LEVEL_JQL = "select l from SeatLevel l order by l.levelId asc";

	@Inject
	private Provider<EntityManager> entityManagerProvider;

	@Override
	@Transactional
	public List<SeatsByLevel> getSeatsInLevel(Calendar currentTime, Optional<Set<Integer>> levelIds) {
		EntityManager em = entityManagerProvider.get();
		TypedQuery<SeatsByLevel> query = em.createQuery(GET_SEAT_COUNT_JQL, SeatsByLevel.class);
		query.setParameter("levelIds", levelIds
				.orElse(getAllLevels().stream().mapToInt(x -> x.getLevelId()).boxed().collect(Collectors.toSet())));
		query.setParameter("holdStartTime", currentTime);
		return query.getResultList();
	}

	public List<SeatLevel> getAllLevels() {
		EntityManager em = entityManagerProvider.get();
		TypedQuery<SeatLevel> idsQuery = em.createQuery(GET_ALL_LEVEL_JQL, SeatLevel.class);
		return idsQuery.getResultList();
	}

	@Override
	public SeatHoldInfo holdSeats(Map<Integer, List<Integer>> seatsToHoldByLevel, String customerEmail,
			Calendar holdStartTime, Calendar holdEndTime) {

		return null;
	}

	@Override
	public Map<Integer, Integer> getTakenLocationsByLevel(Set<Integer> levels) {
		return null;
	}

	@Transactional
	public Optional<SeatHoldInfo> getSeatHoldInfo(int seatHoldId) {
		EntityManager em = entityManagerProvider.get();
		return Optional.ofNullable(em.find(SeatHoldInfo.class, seatHoldId));
	}

	@Override
	@Transactional
	public SeatReservation createReservation(int seatHoldId, String customerEmail) {
		SeatReservation reservation = new SeatReservation();
		reservation.setCreatedDate(Calendar.getInstance());
		reservation.setCustomerEmail(customerEmail);
		SeatHoldInfo holdInfo = new SeatHoldInfo();
		holdInfo.setId(seatHoldId);
		reservation.setSeatHoldInfo(holdInfo);
		entityManagerProvider.get().persist(reservation);
		return reservation;
	}

}
