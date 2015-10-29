package com.mytickets.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.mytickets.dao.SeatDao;
import com.mytickets.model.SeatLevel;
import com.mytickets.model.SeatsByLevel;
import com.mytickets.service.api.DateService;
import com.mytickets.service.api.SeatHold;
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
		Calendar currentTime = Calendar.getInstance();
		Mockito.when(dateService.getCurrentTime()).thenReturn(currentTime);
		List<SeatLevel> levels = new ArrayList<>();
		Set<Integer> filteredLevels = new HashSet<>();
		{
			SeatLevel l = new SeatLevel();
			l.setLevelId(1);
			l.setLevelName("level 1");
			l.setNumOfSeatsInRow(10);
			l.setRows(5);
			l.setLevelPrice(new BigDecimal("100.00"));
			levels.add(l);
		}
		{
			SeatLevel l = new SeatLevel();
			l.setLevelId(2);
			l.setLevelName("level 2");
			l.setNumOfSeatsInRow(5);
			l.setRows(5);
			l.setLevelPrice(new BigDecimal("40.00"));
			levels.add(l);
			filteredLevels.add(l.getLevelId());
		}
		{
			SeatLevel l = new SeatLevel();
			l.setLevelId(3);
			l.setLevelName("level 3");
			l.setNumOfSeatsInRow(3);
			l.setRows(3);
			l.setLevelPrice(new BigDecimal("20.00"));
			levels.add(l);
			filteredLevels.add(l.getLevelId());
		}
		Mockito.when(seatDao.getAllLevels()).thenReturn(levels);
		Map<Integer, List<Integer>> takenLocationsBylevel = new HashMap<>();
		Mockito.when(seatDao.getTakenLocationsByLevel(currentTime, filteredLevels)).thenReturn(takenLocationsBylevel);
		SeatHold seatHold = ticketService.findAndHoldSeats(50, Optional.of(2), Optional.empty(), "gary11@gary.com");

	}

	@Test
	public void testReserveSeats() {

	}

}
