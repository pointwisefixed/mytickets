package com.mytickets.service.api;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SeatHold {

	private Set<SeatInfo> seatsFound = new HashSet<>();
	private final String seatHoldId;
	private final String customerId;
	private final Calendar holdStartTime;
	private final Calendar holdEndTime;
	private String message;
}
