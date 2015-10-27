package com.mytickets.model;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reservationInformation")
	private Set<Seat> reservedSeats;

	@Column(name = "created_date")
	private Calendar createdDate;
}
