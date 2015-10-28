package com.mytickets.model;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.mytickets.service.api.SeatHold;
import com.mytickets.service.api.SeatInfo;

import lombok.Data;

@Data
@Entity
@Table(name = "mt_seat_hold")
public class SeatHoldInfo {

	@Id
	@Column(name = "seat_hold_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "customer_email", nullable = false)
	private String customerEmail;
	@Column(name = "hold_start_time", nullable = false)
	private Calendar seatHoldStartTime;
	@Column(name = "hold_end_time", nullable = false)
	private Calendar seatHoldEndTime;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hold")
	private Set<SeatAction> heldSeats;
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "seatHoldInfo")
	private SeatReservation reservation;

	private transient Set<SeatInfo> seatInfo;

	public SeatHold toSeatHold() {
		return new SeatHold(getSeatInfo(), id, customerEmail, seatHoldStartTime, seatHoldEndTime);
	}
}
