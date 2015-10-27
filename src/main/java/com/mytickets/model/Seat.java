package com.mytickets.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "mt_seat")
@Data
@ToString
@Cacheable(true)
public class Seat {

	@Id
	@Column(name = "seat_id")
	private int seatId;

	@Column(name = "seat_row", nullable = false)
	private int seatRow;
	@Column(name = "seat_column", nullable = false)
	private int seatColumn;
	@Column(name = "seat_name", nullable = false)
	private String seatName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seat_level_id", nullable = false, updatable = false, insertable = false)
	private SeatLevel seatLevel;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "seat_hold_id")
	private SeatHoldInformation holdInformation;
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "seat_reservation_id")
	private SeatReservation reservationInformation;

}
