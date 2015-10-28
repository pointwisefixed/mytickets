package com.mytickets.service.api;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class SeatHold {

	private Set<SeatInfo> seatsFound = new HashSet<>();
	private int seatHoldId;
	private String customerId;
	private Calendar holdStartTime;
	private Calendar holdEndTime;
}
