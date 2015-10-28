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
	private String seatHoldId;
	private String customerId;
	private Calendar holdStartTime;
	private Calendar holdEndTime;
	private String message;
}
