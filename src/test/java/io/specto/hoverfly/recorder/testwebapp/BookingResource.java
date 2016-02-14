package io.specto.hoverfly.recorder.testwebapp;

import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDateTime;

public class BookingResource extends ResourceSupport{
    private final String bookingId;
    private final String origin;
    private final String destination;
    private final LocalDateTime time;

    public BookingResource(final String bookingId, final String origin, final String destination, final LocalDateTime time) {

        this.bookingId = bookingId;
        this.origin = origin;
        this.destination = destination;
        this.time = time;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
