package com.mytickets.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.mytickets.dao.SeatDao;
import com.mytickets.model.SeatLevel;
import com.mytickets.model.SeatsByLevel;

import junit.framework.Assert;

public class TicketServiceTest {

	private Injector injector;
	private SeatDao seatDao;

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

	@Test
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
	
	@Test
	public void testAvailableOnHold(){
		
	}
}
