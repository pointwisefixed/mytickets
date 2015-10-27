package com.mytickets.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper =false)
@Entity
@Table(name = "mt_seat_hold")
public class SeatHold extends Creatable{

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
	@ManyToOne(optional = false)
	@JoinColumn(name = "venue_seat_id", insertable = false, updatable = false)
	private VenueSeat venueSeat;
}
