package com.mytickets.model;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

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
	private int numOfSeatsOnHold;
	private transient Set<SeatInfo> seatInfo;
	private SeatLevel seatLevel;

}
