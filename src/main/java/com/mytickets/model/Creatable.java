package com.mytickets.model;

import java.util.Calendar;

import javax.persistence.Column;

import lombok.Data;

@Data
public class Creatable {

	@Column(name = "created_date", nullable = false)
	private Calendar createdDate;

}
