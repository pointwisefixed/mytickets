package com.mytickets.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.mytickets.dao.SeatDao;
import com.mytickets.model.SeatAction;
import com.mytickets.model.SeatHoldInfo;
import com.mytickets.model.SeatLevel;
import com.mytickets.model.SeatReservation;
import com.mytickets.model.SeatsByLevel;

import junit.framework.Assert;

public class SeatDaoTest {

	private Injector injector;
	private SeatDao seatDao;
	private int seatHoldId;

	@BeforeTest
	private void setup() {
		injector = Guice.createInjector(new JpaPersistModule("myTicketsInMemTest"), new TestConfigModule());
		injector.getInstance(PersistServiceInitializer.class);
		seatDao = injector.getInstance(SeatDao.class);
		seatDao.createDefaultSeatLevels();
	}

	@Test
	public void testAllLevelsAreThere() {
		List<SeatLevel> levels = seatDao.getAllLevels();
		Assert.assertEquals(4, levels.size());
		SeatLevel l = levels.get(0);
		Assert.assertEquals(1, l.getLevelId());
		Assert.assertEquals("Orchestra", l.getLevelName());
		Assert.assertEquals(new BigDecimal("100.00"), l.getLevelPrice());
		Assert.assertEquals(25, l.getRows());
		Assert.assertEquals(50, l.getNumOfSeatsInRow());
		Assert.assertTrue(!seatDao.getSeatHoldInfo(1).isPresent());
	}

	@Test(dependsOnMethods = { "testAllLevelsAreThere" })
	public void testAllAvailable() {
		List<SeatsByLevel> seatsByLevel = seatDao.getSeatsInLevel(Calendar.getInstance(), Optional.empty());
		SeatsByLevel l1 = seatsByLevel.get(0);
		SeatsByLevel l2 = seatsByLevel.get(1);
		SeatsByLevel l3 = seatsByLevel.get(2);
		SeatsByLevel l4 = seatsByLevel.get(3);
		Assert.assertEquals((25 * 50), l1.getAvailableSeats());
		Assert.assertEquals((20 * 100), l2.getAvailableSeats());
		Assert.assertEquals((15 * 100), l3.getAvailableSeats());
		Assert.assertEquals((15 * 100), l4.getAvailableSeats());
	}

	@Test(dependsOnMethods = { "testAllAvailable" })
	public void testAvailableOnHold() {
		List<SeatLevel> level = seatDao.getAllLevels();
		SeatLevel l1 = level.get(0);
		SeatLevel l2 = level.get(1);
		SeatLevel l3 = level.get(2);
		SeatLevel l4 = level.get(3);
		Map<SeatLevel, Collection<SeatAction>> locsByLevel = new HashMap<>();
		locsByLevel.put(l1, new ArrayList<>());
		locsByLevel.put(l2, new ArrayList<>());
		locsByLevel.put(l3, new ArrayList<>());
		locsByLevel.put(l4, new ArrayList<>());
		Calendar startTime = Calendar.getInstance();
		Calendar endTime = (Calendar) startTime.clone();
		endTime.add(Calendar.SECOND, 70);
		int level1HoldCount = 15;
		int level2HoldCount = 14;
		int level3HoldCount = 3;
		int level4HoldCount = 2;
		locsByLevel.get(l1).addAll(l1.getSeatActionsToHold(new ArrayList<>(), level1HoldCount));
		locsByLevel.get(l2).addAll(l2.getSeatActionsToHold(new ArrayList<>(), level2HoldCount));
		locsByLevel.get(l3).addAll(l3.getSeatActionsToHold(new ArrayList<>(), level3HoldCount));
		locsByLevel.get(l4).addAll(l4.getSeatActionsToHold(new ArrayList<>(), level4HoldCount));

		SeatHoldInfo info = seatDao.holdSeats(locsByLevel, "gary@garyrosales.com", startTime, endTime);
		Assert.assertNotNull(info);
		Assert.assertNotNull(info.getId());
		Assert.assertEquals("gary@garyrosales.com", info.getCustomerEmail());
		Assert.assertNull(info.getReservation());
		Assert.assertTrue(info.getSeatHoldEndTime().compareTo(endTime) == 0);
		Assert.assertTrue(info.getSeatHoldStartTime().compareTo(startTime) == 0);
		seatHoldId = info.getId();

		Calendar futureTime = (Calendar) endTime.clone();
		futureTime.add(Calendar.SECOND, 70);

		List<SeatsByLevel> seatsByLevel = seatDao.getSeatsInLevel(futureTime, Optional.empty());
		SeatsByLevel sl1 = seatsByLevel.get(0);
		SeatsByLevel sl2 = seatsByLevel.get(1);
		SeatsByLevel sl3 = seatsByLevel.get(2);
		SeatsByLevel sl4 = seatsByLevel.get(3);
		Assert.assertEquals((25 * 50) - level1HoldCount, sl1.getAvailableSeats());
		Assert.assertEquals((20 * 100) - level2HoldCount, sl2.getAvailableSeats());
		Assert.assertEquals((15 * 100) - level3HoldCount, sl3.getAvailableSeats());
		Assert.assertEquals((15 * 100) - level4HoldCount, sl4.getAvailableSeats());
	}

	@Test(dependsOnMethods = { "testAvailableOnHold" })
	public void testCreateReservation() {
		Calendar pastTime = Calendar.getInstance();
		pastTime.add(Calendar.SECOND, -100);
		SeatReservation reservation = seatDao.createReservation(Calendar.getInstance(),seatHoldId, "gary1@garyrosales.com");
		Assert.assertNotNull(reservation);
		Assert.assertNotNull(reservation.getId());
		Assert.assertEquals("gary1@garyrosales.com", reservation.getCustomerEmail());
		List<SeatsByLevel> seatsByLevel = seatDao.getSeatsInLevel(pastTime, Optional.empty());
		SeatsByLevel sl1 = seatsByLevel.get(0);
		SeatsByLevel sl2 = seatsByLevel.get(1);
		SeatsByLevel sl3 = seatsByLevel.get(2);
		SeatsByLevel sl4 = seatsByLevel.get(3);
		int level1HoldCount = 15;
		int level2HoldCount = 14;
		int level3HoldCount = 3;
		int level4HoldCount = 2;
		Assert.assertEquals((25 * 50) - level1HoldCount, sl1.getAvailableSeats());
		Assert.assertEquals((20 * 100) - level2HoldCount, sl2.getAvailableSeats());
		Assert.assertEquals((15 * 100) - level3HoldCount, sl3.getAvailableSeats());
		Assert.assertEquals((15 * 100) - level4HoldCount, sl4.getAvailableSeats());
	}
}
