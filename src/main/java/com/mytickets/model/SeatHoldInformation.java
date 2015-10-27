package com.mytickets.model;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.mytickets.service.api.SeatHold;
import com.mytickets.service.api.SeatInfo;

import lombok.Data;

@Data
@Entity
@Table(name = "mt_seat_hold")
public class SeatHoldInformation {

	@Id
	@Column(name = "seat_hold_id")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	private String id;
	@Column(name = "customer_email", nullable = false)
	private String customerEmail;
	@Column(name = "hold_start_time", nullable = false)
	private Calendar seatHoldStartTime;
	@Column(name = "hold_end_time", nullable = false)
	private Calendar seatHoldEndTime;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "holdInformation")
	private Set<Seat> heldSeats;

	public SeatHold toSeatHold() {
		SeatHold hold = new SeatHold(getId(), getCustomerEmail(), getSeatHoldStartTime(), getSeatHoldEndTime());
		hold.setSeatsFound(new HashSet<>());
		heldSeats.forEach(seat -> {
			SeatLevel vl = seat.getSeatLevel();
			hold.getSeatsFound().add(new SeatInfo(seat.getSeatRow(), 
					seat.getSeatColumn(), vl.getLevelName(),
					vl.getLevelId(), seat.getSeatName()));
		});
		return hold;
	}
}
