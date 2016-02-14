package io.specto.hoverfly.recorder.testwebapp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateBookingCommand {
    private String flightId;

    @JsonCreator
    public CreateBookingCommand(@JsonProperty("flightId") String flightId) {
        this.flightId = flightId;
    }

    public String getFlightId() {
        return flightId;
    }
}
