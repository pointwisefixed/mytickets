package com.mytickets.rest.service;

import static spark.Spark.get;

import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.mytickets.dao.SeatDao;
import com.mytickets.service.api.TicketService;

import spark.servlet.SparkApplication;

public class ApiServer implements SparkApplication {

	@Inject
	private SeatDao seatDao;
	@Inject
	private TicketService ticketService;

	@Override
	public void init() {
		seatDao.createDefaultSeatLevels();
		doRouting();
	}

	private void doRouting() {
		doGetNumOfSeatsAvailableRouting();
	}

	private void doGetNumOfSeatsAvailableRouting() {
		get("/mytickets/availability", (req, resp) -> {
			String levelVal = req.queryParams("level");
			Integer level = null;
			if (levelVal != null) {
				try {
					level = Integer.parseInt(levelVal);
				} catch (NumberFormatException ex) {

				}
			}
			Optional<Integer> venueLevel = Optional.ofNullable(level);
			int result = ticketService.numSeatsAvailable(venueLevel);
			resp.status(200);
			resp.type("application/json");
			JsonObject jo = new JsonObject();
			jo.addProperty("num_of_seats_available", result);
			return jo.toString();
		});
	}

	@Override
	public void destroy() {
		SparkApplication.super.destroy();
	}

}
