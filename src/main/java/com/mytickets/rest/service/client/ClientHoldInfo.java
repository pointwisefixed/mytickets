package com.mytickets.rest.service.client;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClientHoldInfo {
	private int numSeats; 
	private Integer minLevel; 
	private Integer maxLevel;
	private String customerEmail;
}
