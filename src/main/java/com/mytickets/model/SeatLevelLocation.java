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

}
