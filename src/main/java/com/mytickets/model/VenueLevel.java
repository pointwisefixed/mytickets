package com.mytickets.model;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mt_venue_level")
public class VenueLevel {

	@Id
	@Column(name = "venue_level_id", nullable = false)
	private int levelId;
	@Column(name = "venue_level_name", nullable = false)
	private String levelName;
	@Column(name = "venue_level_price", nullable = false)
	private BigDecimal levelPrice;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "seatLevel")
	private Set<VenueSeat> seatsInLevel;

}
