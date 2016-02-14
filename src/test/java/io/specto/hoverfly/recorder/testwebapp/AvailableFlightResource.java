package io.specto.hoverfly.recorder.testwebapp;

import org.springframework.hateoas.ResourceSupport;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AvailableFlightResource extends ResourceSupport {

    private final String idCode;
    private final String origin;
    private final String destination;
    private final BigDecimal cost;
    private final LocalDateTime departure;

    public AvailableFlightResource(final String id, final String origin, final String destination, final BigDecimal cost, final LocalDateTime departure) {
        this.idCode = id;
        this.origin = origin;
        this.destination = destination;
        this.cost = cost;
        this.departure = departure;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public LocalDateTime getDeparture() {
        return departure;
    }

    public String getIdCode() {
        return idCode;
    }
}
