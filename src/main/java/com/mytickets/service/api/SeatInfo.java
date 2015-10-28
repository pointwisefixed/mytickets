package com.mytickets.service.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SeatInfo {

	private int row;
	private int seatInRow;
	private int venueLevelId;
}
