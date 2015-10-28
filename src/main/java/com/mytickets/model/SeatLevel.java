package com.mytickets.model;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
@Cacheable(true)
public class SeatLevel {

	@Id
	@Column(name = "seat_level_id", nullable = false)
	private int levelId;
	@Column(name = "seat_level_name", nullable = false)
	private String levelName;
	@Column(name = "seat_level_rows", nullable = false)
	private int rows;
	@Column(name = "num_of_seats_in_row", nullable = false)
	private int numOfSeatInRows;
	@Column(name = "seat_level_price", nullable = false)
	private BigDecimal levelPrice;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "seatLevel")
	private Set<SeatAction> seatActions;
}
