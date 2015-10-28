package com.mytickets.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatsByLevel {

	private int levelId;
	private int totalSeats;
	private long reservedOrOnHoldSeats;

	public int getAvailableSeats() {
		return totalSeats - (int) reservedOrOnHoldSeats;
	}
}
