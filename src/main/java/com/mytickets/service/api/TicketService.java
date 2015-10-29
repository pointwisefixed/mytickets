package com.mytickets.service.api;

import java.util.Optional;

import com.google.inject.ImplementedBy;
import com.mytickets.service.impl.TicketServiceImpl;

/**
 * @author pwfixed
 * </br>
 * This API assumes that seats are reserved on a best available basis. That is, if there are 50
 * seats reserved for level 1 (which has 200 seats in 4 rows), then the next one to be on hold is 
 * seat 51 (second row, first column), and the next one will be 52 (second row, second column)
 * if the holds expire for 51 and 52 holds and seat 53 has been reserved already by somebody else, 
 * when a user uses the service they should get seat 51 again. The point is that we don't have a
 * representation of an actual seat intentionally. Creating one could take a lot of records and 
 * maintenance for a simple homework assignment (for the sample venue, there would be 6000 seats
 * for instance). For a larger system, you may want to do that as you are able to create more of 
 * a trace for a seat:</br>
 * A proposed model (and my initial thought) would have been:</br>
 * <pre>
 * Naive Model
 * =======
 * Venue - A venue with many seats. When you create a venue, you also create the seats.
 * Seat - An actual seat with seat information. A seat also has a level, a row, and a column. Created when a venue record is created.
 * SeatReservation - has many seats, with seat reservation info
 * SeatHoldInformation - has many seats, with seat hold info (hold start time, hold end time)
 * 
 * relationships -> seat has many-to-many to seatholdinfo and seat reservation
 * queries would be easier this way, but if you have n seats, you would have as many as n reservations and more than holds
 * for a full venue (which the requirements mentions this place is supposed to be)
 * </pre>
 * So instead I use a different model taking into consideration the provided interface</br>
 * <pre>
 * Non-Naive Model
 * ================
 * Venue - with venue metadata - rows, seats in row
 * SeatAction - a hold action performed on a seat, it's tied to a hold so it can be purged for expired holds.
 * SeatHold - a hold has many seat actions, it also has an id, a hold start and end date.
 * SeatReservation - a reservation which HAS to be done after a hold has been done. The assumption I make
 * -given that seatHoldId is a primitive- is that a reservation can only be done after a hold exists.
 * so seatReservation needs a hold info. and if you want to look up the seats for a reservation you go:
 * seatReservation -> SeatHold -> SeatAction.
 * </pre>
 */
@ImplementedBy(TicketServiceImpl.class)
public interface TicketService {
	/**
	 * The number of seats in the requested level that are neither held nor
	 * reserved
	 *
	 * @param venueLevel
	 *            a numeric venue level identifier to limit the search
	 *            if no venueLevel is given, then it returns the count
	 *            of all seats available for the venue.
	 * @return the number of tickets available on the provided level
	 */
	int numSeatsAvailable(Optional<Integer> venueLevel);

	/**
	 * Find and hold the best available seats for a customer
	 * @param numSeats
	 *            the number of seats to find and hold
	 * @param minLevel
	 *            the minimum venue level
	 * @param maxLevel
	 *            the maximum venue level
	 * @param customerEmail
	 *            unique identifier for the customer
	 * @return a SeatHold object identifying the specific seats and related
	 *         information
	 */
	SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
			String customerEmail);

	/**
	 * Commit seats held for a specific customer
	 * If the hold has expired or the seat hold cannot be found, then we return null
	 * @param seatHoldId
	 *            the seat hold identifier
	 * @param customerEmail
	 *            the email address of the customer to which the seat hold is
	 *            assigned
	 * @return a reservation confirmation code
	 */
	String reserveSeats(int seatHoldId, String customerEmail);
}
