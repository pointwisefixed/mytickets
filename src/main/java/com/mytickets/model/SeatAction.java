package com.mytickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "mt_seat_action")
@Data
public class SeatAction {

	@Id
	@Column(name = "seat_action_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "seat_location", nullable = false)
	private int seatLocationIndex;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private SeatLevel seatLevel;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private SeatHoldInfo hold;
}
