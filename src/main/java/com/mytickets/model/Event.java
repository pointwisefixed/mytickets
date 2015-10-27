package com.mytickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "mt_event")
@Entity
public class Event {

	@Id
	@Column(name = "event_id", nullable = false)
	private int eventId;
}
