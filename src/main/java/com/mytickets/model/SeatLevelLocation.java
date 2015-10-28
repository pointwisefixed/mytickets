package com.mytickets.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class SeatLevelLocation {

	private int row;
	private int col;

	public static SeatLevelLocation seatLocationFor(int levelRows, int seatsInRow, int seatIndex) {
		int row = seatIndex / seatsInRow;
		int col = seatIndex % seatsInRow;
		return new SeatLevelLocation(row, col);
	}
}
