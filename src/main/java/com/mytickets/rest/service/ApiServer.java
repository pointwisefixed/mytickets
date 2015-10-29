package com.mytickets.rest.service;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.mytickets.dao.SeatDao;
import com.mytickets.rest.service.client.ClientCreateReservationInfo;
import com.mytickets.rest.service.client.ClientHoldInfo;
import com.mytickets.rest.service.exceptions.HoldExpiredException;
import com.mytickets.service.api.SeatHold;
import com.mytickets.service.api.TicketService;
import com.mytickets.utils.GsonUtils;

import spark.servlet.SparkApplication;

public class ApiServer implements SparkApplication {

	private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
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
		doPostHoldSeatRouting();
		doPostCreateReservationRouting();
	}

	private void doPostHoldSeatRouting() {
		post("/mytickets/holds", "json", (req, resp) -> {
			try {
				String requestBody = req.body();
				Gson gson = GsonUtils.createWithLowerNamingPolicy();
				ClientHoldInfo holdInfo = gson.fromJson(requestBody, ClientHoldInfo.class);
				int numSeats = holdInfo.getNumSeats();
				if (numSeats <= 0) {
					resp.status(400);
					return "Bad Request - num_seats has to be greater than 0";
				}
				Optional<Integer> minLevel = Optional.ofNullable(holdInfo.getMinLevel());
				Optional<Integer> maxLevel = Optional.ofNullable(holdInfo.getMaxLevel());
				String customerEmail = holdInfo.getCustomerEmail();
				SeatHold seatHold = ticketService.findAndHoldSeats(numSeats, minLevel, maxLevel, customerEmail);
				resp.status(200);
				resp.type(APPLICATION_JSON_CONTENT_TYPE);
				return gson.toJson(seatHold);
			} catch (Exception ex) {
				resp.status(400);
				return "Bad Request - num_seats and customer_email are required.";
			}
		});
	}

	private void doPostCreateReservationRouting() {
		post("/mytickets/reservations", "json", (req, resp) -> {
			try {
				String requestBody = req.body();
				Gson gson = GsonUtils.createWithLowerNamingPolicy();
				ClientCreateReservationInfo reservationInfo = gson.fromJson(requestBody,
						ClientCreateReservationInfo.class);
				int seatHoldId = reservationInfo.getSeatHoldId();
				if (seatHoldId <= 0) {
					resp.status(400);
					return "Bad Request - seat_hold_id has to be greater than 0";
				}
				String customerEmail = reservationInfo.getCustomerEmail();
				String reservationId = ticketService.reserveSeats(seatHoldId, customerEmail);
				if (reservationId == null) {
					throw new HoldExpiredException("hold has expired");
				}
				resp.status(200);
				resp.type(APPLICATION_JSON_CONTENT_TYPE);
				JsonObject result = new JsonObject();
				result.addProperty("reservation_id", reservationId);
				return result.toString();
			} catch (HoldExpiredException ex) {
				resp.status(400);
				return "Hold has expired";
			}
		});
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
			try {
				int result = ticketService.numSeatsAvailable(venueLevel);
				resp.status(200);
				resp.type(APPLICATION_JSON_CONTENT_TYPE);
				JsonObject jo = new JsonObject();
				jo.addProperty("num_of_seats_available", result);
				return jo.toString();
			} catch (Exception ex) {
				resp.status(400);
				return "Bad Request - level must be a number";
			}
		});
	}

	@Override
	public void destroy() {
		SparkApplication.super.destroy();
	}

}
