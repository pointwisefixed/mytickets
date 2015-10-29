# mytickets

A simple online reservation system built on an on-memory db

Originally I want it to use this to sharp up my reactjs skills for a front-end, but given my time constraints (work and other stuff) I'll just leave it to be a simple rest-api

# To run tests

mvn clean test


# To run rest api

mvn clean jetty:run

# to get number of seats available for a venue level

GET http://localhost:9080/mytickets/availability?level=1

# to get a hold on a number of seats

POST http://localhost:9080/mytickets/holds

with a body containing json in this format

	{'num_seats': 40, 'min_level': 1, 'max_level': 4, 'customer_email': 'me@me.com'}

returns a json with the seat hold information
	
# to create a reservation on a hold

POST http://localhost:9080/mytickets/reservations

with a body containing json in this format:

	{'seat_hold_id': 1, 'customer_email': 'me@me.com'}
