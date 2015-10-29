package com.mytickets.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.mytickets.dao.SeatDao;
import com.mytickets.model.SeatsByLevel;
import com.mytickets.service.api.DateService;
import com.mytickets.service.api.TicketService;
import com.mytickets.service.impl.TicketServiceImpl;

public class TicketServiceTest {

	private TicketService ticketService = new TicketServiceImpl();
	private SeatDao seatDao = Mockito.mock(SeatDao.class);
	private DateService dateService = Mockito.mock(DateService.class);
	private int secondsToHold = 50;

	@BeforeTest
	public void setup() {
		Whitebox.setInternalState(ticketService, "dateService", dateService);
		Whitebox.setInternalState(ticketService, "seatDao", seatDao);
		Whitebox.setInternalState(ticketService, "seatHoldTimeInSeconds", secondsToHold);
	}

	@Test
	public void testNumSeatsAvailable() {
		Calendar currentTime = Calendar.getInstance();
		Mockito.when(dateService.getCurrentTime()).thenReturn(currentTime);
		List<SeatsByLevel> seatsByLevel = new ArrayList<>();
		int maxLevel = 3;
		for (int i = 0; i <= maxLevel; ++i) {
			SeatsByLevel sl = new SeatsByLevel(i, 100, i * 20);
			seatsByLevel.add(sl);
		}
		Mockito.when(seatDao.getSeatsInLevel(currentTime, Optional.empty())).thenReturn(seatsByLevel);
		int seatsForLevelThree = ticketService.numSeatsAvailable(Optional.of(maxLevel));
		int seatsForAll = ticketService.numSeatsAvailable(Optional.empty());
		Assert.assertEquals(40, seatsForLevelThree);
		Assert.assertEquals(280, seatsForAll);
	}

	@Test
	public void testFindAndHoldSeats() {
		
	}

	@Test
	public void testReserveSeats() {

	}

}
