package com.mytickets.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.mytickets.dao.SeatDao;
import com.mytickets.model.SeatAction;
import com.mytickets.model.SeatHoldInfo;
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

	/**
	 * I probably could break this unit test into parts for maintanability and I
	 * would, but I am in a rush
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAndHoldSeats() {
		Calendar currentTime = Calendar.getInstance();
		Mockito.when(dateService.getCurrentTime()).thenReturn(currentTime);
		Calendar expectedEndTime = (Calendar) currentTime.clone();
		expectedEndTime.add(Calendar.SECOND, secondsToHold);
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
			filteredLevels.add(l.getLevelId());
		}
		{
			SeatLevel l = new SeatLevel();
			l.setLevelId(2);
			l.setLevelName("level 2");
			l.setNumOfSeatsInRow(15);
			l.setRows(2);
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
		{
			SeatLevel l = new SeatLevel();
			l.setLevelId(4);
			l.setLevelName("level 4");
			l.setNumOfSeatsInRow(10);
			l.setRows(3);
			l.setLevelPrice(new BigDecimal("10.00"));
			levels.add(l);
		}
		Mockito.when(seatDao.getAllLevels()).thenReturn(levels);
		Map<Integer, List<Integer>> takenLocationsBylevel = new HashMap<>();
		takenLocationsBylevel.put(1, Arrays.asList(3, 4, 8));
		takenLocationsBylevel.put(2, Arrays.asList(1, 15, 22, 2, 3));
		takenLocationsBylevel.put(3, Arrays.asList(0, 3, 5, 1, 8));
		List<SeatsByLevel> bestSeatsInLevels = new ArrayList<>();
		{
			SeatsByLevel sbl = new SeatsByLevel(1, levels.get(0).getNumOfSeatsInRow() * levels.get(0).getRows(),
					takenLocationsBylevel.get(1).stream().count());
			bestSeatsInLevels.add(sbl);
		}
		{
			SeatsByLevel sbl = new SeatsByLevel(2, levels.get(1).getNumOfSeatsInRow() * levels.get(1).getRows(),
					takenLocationsBylevel.get(2).stream().count());
			bestSeatsInLevels.add(sbl);
		}
		{
			SeatsByLevel sbl = new SeatsByLevel(3, levels.get(2).getNumOfSeatsInRow() * levels.get(2).getRows(),
					takenLocationsBylevel.get(3).stream().count());
			bestSeatsInLevels.add(sbl);
		}
		final String customerEmailExpected = "gary11@gary.com";
		Mockito.when(seatDao.getTakenLocationsByLevel(currentTime, filteredLevels)).thenReturn(takenLocationsBylevel);
		Mockito.when(seatDao.getSeatsInLevel(currentTime, Optional.of(filteredLevels))).thenReturn(bestSeatsInLevels);
		Mockito.when(seatDao.holdSeats(Mockito.anyMap(), Mockito.anyString(), Mockito.any(Calendar.class),
				Mockito.any(Calendar.class))).then(new Answer<SeatHoldInfo>() {
					@Override
					public SeatHoldInfo answer(InvocationOnMock invocation) throws Throwable {
						Map<SeatLevel, Collection<SeatAction>> seatsToHoldByLevel = new HashMap<>();
						seatsToHoldByLevel = invocation.getArgumentAt(0, seatsToHoldByLevel.getClass());
						String customerEmail = invocation.getArgumentAt(1, String.class);
						Calendar startTime = invocation.getArgumentAt(2, Calendar.class);
						Calendar endTime = invocation.getArgumentAt(3, Calendar.class);
						Collection<SeatAction> takenSeats1 = seatsToHoldByLevel.get(levels.get(0));
						Collection<SeatAction> takenSeats2 = seatsToHoldByLevel.get(levels.get(1));
						Collection<SeatAction> takenSeats3 = seatsToHoldByLevel.get(levels.get(2));
						Assert.assertEquals(takenSeats1.size(), 47);
						Assert.assertEquals(takenSeats2.size(), 25);
						Assert.assertEquals(takenSeats3.size(), 2);
						{
							List<Integer> takenLevel1Locations = takenSeats1.stream()
									.mapToInt(x -> x.getSeatLocationIndex()).boxed().collect(Collectors.toList());
							List<Integer> freeSeats = new ArrayList<>();
							for (int i = 0; i < 50; i++) {
								if (!takenLevel1Locations.contains(i) && !takenLocationsBylevel.get(1).contains(i)) {
									freeSeats.add(i);
								}
							}
							Assert.assertEquals(freeSeats.size(), 0);
						}
						{
							List<Integer> takenLevel2Locations = takenSeats2.stream()
									.mapToInt(x -> x.getSeatLocationIndex()).boxed().collect(Collectors.toList());
							List<Integer> freeSeats = new ArrayList<>();
							for (int i = 0; i < 30; i++) {
								if (!takenLevel2Locations.contains(i) && !takenLocationsBylevel.get(2).contains(i)) {
									freeSeats.add(i);
								}
							}
							Assert.assertEquals(freeSeats.size(), 0);
						}
						{
							List<Integer> takenLevel3Locations = takenSeats3.stream()
									.mapToInt(x -> x.getSeatLocationIndex()).boxed().collect(Collectors.toList());
							List<Integer> freeSeats = new ArrayList<>();
							for (int i = 0; i < 9; i++) {
								if (!takenLevel3Locations.contains(i) && !takenLocationsBylevel.get(3).contains(i)) {
									freeSeats.add(i);
								}
							}
							Assert.assertEquals(freeSeats.size(), 2);
						}
						Assert.assertEquals(customerEmail, customerEmailExpected);
						Assert.assertTrue(startTime.compareTo(currentTime) == 0);
						Assert.assertTrue(endTime.compareTo(expectedEndTime) == 0);
						SeatHoldInfo si = new SeatHoldInfo();
						si.setId(1);
						si.setSeatHoldStartTime(startTime);
						si.setSeatHoldEndTime(endTime);
						si.setCustomerEmail(customerEmail);
						return si;
					}
				});
		SeatHold seatHold = ticketService.findAndHoldSeats(74, Optional.of(1), Optional.of(3), customerEmailExpected);
		Assert.assertNotNull(seatHold);
		Assert.assertEquals(seatHold.getSeatHoldId(), 1);
		Assert.assertEquals(seatHold.getCustomerId(), customerEmailExpected);
	}

	@Test
	public void testReserveSeats() {

	}

}
