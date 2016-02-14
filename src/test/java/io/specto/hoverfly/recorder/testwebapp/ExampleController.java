package io.specto.hoverfly.recorder.testwebapp;

import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.util.Collections.emptyList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/api")
public class ExampleController {

    @RequestMapping(value = "/flights", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableFlightResource> searchAvailability(@RequestParam("origin") final String origin, @RequestParam("destination") final String destination) {
        if (origin.equals("London") && destination.equals("Singapore")) {
            return newArrayList(
                    new AvailableFlightResource("1", "London", "Singpore", new BigDecimal(1002.99).setScale(2, ROUND_HALF_UP), LocalDateTime.of(2011, 9, 1, 12, 30)),
                    new AvailableFlightResource("2", "London", "Singpore", new BigDecimal(800.97).setScale(2, ROUND_HALF_UP), LocalDateTime.of(2011, 9, 1, 16, 30)));
        } else if (origin.equals("Berlin") && destination.equals("Munich")) {
            return newArrayList(
                    new AvailableFlightResource("3", "Berlin", "Munich", new BigDecimal(800.99).setScale(2, ROUND_HALF_UP), LocalDateTime.of(2011, 9, 1, 12, 30)));
        } else {
            return emptyList();
        }
    }

    @RequestMapping(value = "/bookings", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> makeBooking(@RequestBody CreateBookingCommand createBookingCommand) {
        if (newHashSet("1", "2", "3").contains(createBookingCommand.getFlightId())) {
            final URI location = linkTo(DummyInvocationUtils.methodOn(ExampleController.class).getBooking("1")).toUri();
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/bookings/{booking-id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBooking(@PathVariable("booking-id") String bookingId) {
        if (bookingId.equals("1")) {
            final BookingResource body = new BookingResource("1", "London", "Singapore", LocalDateTime.of(2011, 9, 1, 12, 30));
            body.add(linkTo(ControllerLinkBuilder.methodOn(ExampleController.class).getBooking(bookingId)).withSelfRel());
            return ResponseEntity.ok(body);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
