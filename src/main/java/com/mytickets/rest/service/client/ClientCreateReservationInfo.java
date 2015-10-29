package com.mytickets.rest.service.client;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClientCreateReservationInfo {

	private int seatHoldId;
	private String customerEmail;
}
