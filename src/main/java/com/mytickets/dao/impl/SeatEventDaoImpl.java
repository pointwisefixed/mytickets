package com.mytickets.dao.impl;

import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.mytickets.dao.SeatEventDao;
import com.mytickets.model.SeatHoldInformation;

public class SeatEventDaoImpl implements SeatEventDao {

	private static final String GET_AVAILABLE_JQL = "select count(distinct(s.id)) " + "from Seat s "
			+ "left join s.holdInformation hi "
			+ "where s.seatLevel.id = :levelId " 
			+ "and ri.reservedSeats is null and (s.holdInformation is null "
			+ "or s.holdInformation.seatHoldEndTime < :holdEndTime)" ;
	@Inject
	private Provider<EntityManager> entityManagerProvider;

	@Override
	@Transactional
	public Integer getSeatsInLevel(int levelId, Calendar currentTime) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery(GET_AVAILABLE_JQL);
		query.setParameter("levelId", levelId);
		query.setParameter("holdEndTime", currentTime);
		return (Integer) query.getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mytickets.dao.SeatEventDao#holdSeats(int, int, int,
	 * java.lang.String) 
	 * select distinct(s.*) from mt_seat s inner join mt_seat_level l on s.seat_level_id = l.seat_level_id
	 * left outer join mt_seat_hold h on s.seat_hold_id = h.seat_hold_id
	 * left outer join mt_seat_reservation r on s.seat_reservation_id = r.seat_reservation_id
	 * where (s.seat_hold_id is null or h.hold_end_time < :holdStartTime) and s.seat_reservation_id is null 
	 * and l.seat_level_id between :min_level and :max_level
	 * limit 0, :numSeats
	 * order by l.level asc
	 * 
	 * get all seats to hold then create the hold information object
	 * save it and send it back
	 */

	@Override
	@Transactional
	public SeatHoldInformation holdSeats(int numSeats, int minLevel, int maxLevel, String customerEmail,
			Calendar holdStartTime, Calendar holdEndTime) {
		return null;
	}

}
