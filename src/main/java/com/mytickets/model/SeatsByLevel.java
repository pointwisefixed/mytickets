package com.mytickets.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatsByLevel {

	private int levelId;
	private int reservedOrOnHoldSeats;
	private int availableSeats;
}
