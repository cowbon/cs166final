# cs166final
We built the cruise database and provide the web application interface for the user to manipulate it. It provides the service for customer and staff and can show the searhing result of the query

## The rule of reservation status:
When customers add booking every time we check if there is a seat, and if there are extra seats, if not, set all the booking to ‘wait’ status. We will automatically check if there are new available seats for the customers in the same cruise, the definition here is strictly defined by us, all the number of reservations for a specific cruise id is smaller than the number of seats for that cruise, then it is available. As a result, we will change all of the customers in ‘W’ to ‘C’. Moreover, we provide the delete(cancel) booking for customers who make a reservation.

