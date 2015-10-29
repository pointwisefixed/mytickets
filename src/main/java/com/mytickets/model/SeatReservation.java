package com.mytickets.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Data
@Entity
@Table(name = "mt_seat_reservation")
public class SeatReservation {

	@Id
	@Column(name = "seat_reservation_id")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	private String id;

	@Column(name = "customer_email", nullable = false)
	private String customerEmail;

	@Column(name = "created_date")
	private Calendar createdDate;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "seat_hold_info_id")
	private SeatHoldInfo seatHoldInfo;
}
