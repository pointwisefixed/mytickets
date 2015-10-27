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

	private int numOfSeatsReserved;

	private SeatLevel seatLevel;

	private transient Set<SeatInfo> seatInfo;

}
