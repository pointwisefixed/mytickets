package com.mytickets.model;

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

@Entity
@Table(name = "mt_seat_reservation")
@Data
@EqualsAndHashCode(callSuper = false)
public class SeatReservation extends Creatable {

	@Id
	@Column(name = "seat_reservation_id")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	private String id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "venue_seat_id", insertable = false, updatable = false)
	private VenueSeat venueSeat;

}
