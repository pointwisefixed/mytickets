package com.mytickets.dao.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.mytickets.dao.SeatEventDao;
import com.mytickets.model.SeatHoldInfo;

public class SeatEventDaoImpl implements SeatEventDao {

	private static final String GET_SEAT_COUNT_JQL = "select l.levelId as levelId"
			+ "(l.rows * l.numOfSeatInRows) as totalSeats, (select count(sa.id) "
			+ "from SeatAction sa inner join fetch sa.hold h left join fetch h.reservation r "
			+ "where sa.seatLevel in :levelId and (h.seatHoldEndTime < :holdStartTime or "
			+ "r.id is not null)) as reservedOrOnHoldSeats, (totalSeats - reservedOrOnHoldSeats) as availableSeats "
			+ "from SeatLevel l where l.levelId in :levelIds group by l.levelId";

	private static final String GET_ALL_LEVEL_IDS_JQL = "select l.levelId from SeatLevel l";

	@Inject
	private Provider<EntityManager> entityManagerProvider;

	@Override
	@Transactional
	public Integer getSeatsInLevel(int levelId, Calendar currentTime) {
		EntityManager em = entityManagerProvider.get();
		List<Integer> levelIds = Arrays.asList(levelId);
		if (levelId == -1) {
			levelIds.clear();
			TypedQuery<Object[]> idsQuery = em.createQuery(GET_ALL_LEVEL_IDS_JQL, Object[].class);
			List<Object[]> result = idsQuery.getResultList();
			result.forEach(x -> {
				levelIds.add((Integer) x[0]);
			});
		}
		TypedQuery<Object[]> query = em.createQuery(GET_SEAT_COUNT_JQL, Object[].class);
		query.setParameter("levelIds", levelIds);
		query.setParameter("holdStartTime", currentTime);
		List<Object[]> result = query.getResultList();
		return result.stream().mapToInt(x -> (Integer) x[3]).sum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mytickets.dao.SeatEventDao#holdSeats(int, int, int,
	 * java.lang.String, java.util.Calendar, java.util.Calendar)
	 */
	@Override
	@Transactional
	public SeatHoldInfo holdSeats(int numSeats, int minLevel, int maxLevel, String customerEmail,
			Calendar holdStartTime, Calendar holdEndTime) {
		EntityManager em = entityManagerProvider.get();
		// get the unavailable seat count in those levels (reserved or on hold)
		// calculate how many we need for each level i.e if we need 2 and level
		// 1 has 50, we can just
		// create 2 in level 1

		return null;
	}

}
