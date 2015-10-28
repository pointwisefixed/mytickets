package com.mytickets.model;

public class SeatLocationUtils {

	public static SeatLevelLocation getSeatLocation(int levelRows, int seatsInRow, int seatIndex) {
		int row = seatIndex / seatsInRow;
		int col = seatIndex % seatsInRow;
		return new SeatLevelLocation(row, col);
	}

}
