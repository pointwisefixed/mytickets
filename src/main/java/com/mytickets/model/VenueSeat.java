package com.mytickets.model;

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
@Table(name = "mt_venue_seat")
@Data
@ToString
public class VenueSeat {

	@Id
	@Column(name = "venue_seat_id")
	private int seatId;

	@Column(name = "venue_seat_row", nullable = false)
	private int seatRow;
	@Column(name = "venue_seat_column", nullable = false)
	private int seatColumn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venue_level_id", nullable = false, updatable = false, insertable = false)
	private VenueLevel seatLevel;

}
