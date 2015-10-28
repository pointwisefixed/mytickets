package com.mytickets.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.mytickets.dao.SeatDao;
import com.mytickets.model.SeatAction;
import com.mytickets.model.SeatHoldInfo;
import com.mytickets.model.SeatLevel;
import com.mytickets.model.SeatLevelLocation;
import com.mytickets.model.SeatReservation;
import com.mytickets.model.SeatsByLevel;
import com.mytickets.service.api.SeatInfo;
import com.mytickets.utils.GsonUtils;

public class SeatDaoImpl implements SeatDao {

	private static final String GET_SEAT_COUNT_JQL = "select new com.mytickets.model.SeatsByLevel(l.levelId, "
			+ "(l.rows * l.numOfSeatsInRow) as totalSeats, (select count(sa.id) "
			+ "from SeatAction sa inner join sa.hold h left join h.reservation r "
			+ "where sa.seatLevel.levelId in :levelIds and (h.seatHoldEndTime < :holdStartTime or "
			+ "r.id is not null)) as reservedOrOnHoldSeats)"
			+ "from SeatLevel l where l.levelId in :levelIds group by l.levelId order by l.levelId asc";

	private static final String GET_SEATS_ON_HOLD = "select sa from SeatAction sa inner join fetch sa.seatLevel l left outer join sa.hold h "
			+ "left outer join h.reservation r where (h.seatHoldEndTime > :now and r.id is null) or r.id is not null "
			+ "and l.id in :levelIds";

	private static final String GET_ALL_LEVEL_JQL = "select l from SeatLevel l order by l.levelId asc";

	@Inject
	@Named("defaultVenueLevelsSetup")
	private String defaultLevelJson;

	@Inject
	private Provider<EntityManager> entityManagerProvider;

	@Override
	@Transactional
	public List<SeatsByLevel> getSeatsInLevel(Calendar currentTime, Optional<Set<Integer>> levelIds) {
		EntityManager em = entityManagerProvider.get();
		TypedQuery<SeatsByLevel> query = em.createQuery(GET_SEAT_COUNT_JQL, SeatsByLevel.class);
		Set<Integer> levels = levelIds
				.orElse(getAllLevels().stream().mapToInt(x -> x.getLevelId()).boxed().collect(Collectors.toSet()));
		query.setParameter("levelIds", levels);
		query.setParameter("holdStartTime", currentTime);
		return query.getResultList();
	}

	public List<SeatLevel> getAllLevels() {
		EntityManager em = entityManagerProvider.get();
		TypedQuery<SeatLevel> idsQuery = em.createQuery(GET_ALL_LEVEL_JQL, SeatLevel.class);
		return idsQuery.getResultList();
	}

	@Override
	@Transactional
	public SeatHoldInfo holdSeats(Map<SeatLevel, Collection<SeatAction>> seatsToHoldByLevel, String customerEmail,
			Calendar holdStartTime, Calendar holdEndTime) {
		SeatHoldInfo holdInfo = new SeatHoldInfo();
		holdInfo.setSeatInfo(new HashSet<>());
		;
		holdInfo.setSeatHoldStartTime(holdStartTime);
		holdInfo.setSeatHoldEndTime(holdEndTime);
		holdInfo.setCustomerEmail(customerEmail);
		List<SeatAction> holdSeats = new ArrayList<>();
		for (SeatLevel level : seatsToHoldByLevel.keySet()) {
			for (SeatAction seatAction : seatsToHoldByLevel.get(level)) {
				SeatLevelLocation location = SeatLevelLocation.seatLocationFor(level.getRows(),
						level.getNumOfSeatsInRow(), seatAction.getSeatLocationIndex());
				holdInfo.getSeatInfo().add(new SeatInfo(location.getRow(), location.getCol(), level.getLevelId()));
				seatAction.setHold(holdInfo);
			}
			holdSeats.addAll(seatsToHoldByLevel.get(level));
		}
		EntityManager em = entityManagerProvider.get();
		em.persist(holdInfo);

		for (SeatAction seatAction : holdSeats) {
			em.persist(seatAction);
		}

		return holdInfo;
	}

	@Override
	@Transactional
	public Map<Integer, List<Integer>> getTakenLocationsByLevel(Set<Integer> levels) {
		EntityManager em = entityManagerProvider.get();
		TypedQuery<SeatAction> takenSeatsQ = em.createQuery(GET_SEATS_ON_HOLD, SeatAction.class);
		takenSeatsQ.setParameter("levelIds", levels);
		List<SeatAction> takenSeats = takenSeatsQ.getResultList();
		Map<Integer, List<Integer>> takenLocations = new HashMap<>();
		for (SeatAction seatAction : takenSeats) {
			int levelId = seatAction.getSeatLevel().getLevelId();
			List<Integer> takenByLevel = takenLocations.get(levelId);
			if (takenByLevel == null) {
				takenByLevel = new ArrayList<>();
				takenLocations.put(levelId, takenByLevel);
			}
			takenByLevel.add(seatAction.getSeatLocationIndex());
		}
		return takenLocations;
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

	@Override
	@Transactional
	public List<SeatLevel> createDefaultSeatLevels() {
		EntityManager em = entityManagerProvider.get();
		JsonParser parser = new JsonParser();
		JsonElement el = parser.parse(defaultLevelJson);
		JsonArray arr = el.getAsJsonArray();
		List<SeatLevel> levels = new ArrayList<>();
		for (JsonElement jsonElement : arr) {
			JsonObject sl = jsonElement.getAsJsonObject();
			SeatLevel l = GsonUtils.createWithLowerNamingPolicy().fromJson(sl, SeatLevel.class);
			levels.add(l);
			em.persist(l);
		}
		return levels;
	}

}
