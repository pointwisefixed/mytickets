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
	private String venueLevelName;
	private int venueLevelId;
	private String seatName;
}
