package com.mytickets.service.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatInfo {

	private int row;
	private int seatInRow;
	private int venueLevelId;
}
