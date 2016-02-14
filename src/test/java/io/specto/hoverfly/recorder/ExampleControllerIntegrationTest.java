package io.specto.hoverfly.recorder;

import io.specto.hoverfly.recorder.testwebapp.Application;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class ExampleControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    private static HoverflyFilter hoverflyFilter = new HoverflyFilter("www.my-test.com", "generated/hoverfly.json");

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(hoverflyFilter)
                .build();
    }

    @Test
    public void shouldBeAbleToGetAvailableFlightsBetweenLondonAndSingapore() throws Exception {
        mockMvc.perform(get("/api/flights?origin=London&destination=Singapore"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{" +
                        "\"idCode\": \"1\"," +
                        "\"origin\": \"London\"," +
                        "\"destination\": \"Singpore\"," +
                        "\"cost\": 1002.99," +
                        "\"departure\": \"2011-09-01T12:30\"" +
                        "}, {" +
                        "\"idCode\": \"2\"," +
                        "\"origin\": \"London\"," +
                        "\"destination\": \"Singpore\"," +
                        "\"cost\": 800.97," +
                        "\"departure\": \"2011-09-01T16:30\"" +
                        "}]"));
    }

    @Test
    public void shouldBeAbleToGetAvailableFlightsBetweenBerlinAndMunich() throws Exception {
        mockMvc.perform(get("/api/flights?origin=Berlin&destination=Munich"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{" +
                        "\"idCode\": \"3\"," +
                        "\"origin\": \"Berlin\"," +
                        "\"destination\": \"Munich\"," +
                        "\"cost\": 800.99," +
                        "\"departure\": \"2011-09-01T12:30\"" +
                        "}]"));
    }

    @Test
    public void shouldBeAbleToCreateABookingForAValidFlightId() throws Exception {
        for (int i = 1; i <= 3; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{" +
                            "\"flightId\": \"1\"" +
                            "}"))
                    .andExpect(status().isCreated())
                    .andExpect(MockMvcResultMatchers.header().string("Location", "http://localhost/api/bookings/1"));
        }
    }

    @Test
    public void shouldGetABadRequestWhenCreatingABookingForAnInvalidFlight() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldBeAbleToRetrieveABookingIfItExists() throws Exception {
        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"bookingId\": \"1\"," +
                        "\"origin\": \"London\"," +
                        "\"destination\": \"Singapore\"," +
                        "\"time\": \"2011-09-01T12:30\"," +
                        "\"_links\": {" +
                        "\"self\": {" +
                        "\"href\": \"http://localhost/api/bookings/1\"" +
                        "}" +
                        "}" +
                        "}"));
    }

    @Test
    public void shouldReceiveNotFoundWhenRetrievingABookingThatDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/bookings/9")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"_links\": {" +
                        "\"self\": {" +
                        "\"href\": \"http://localhost/api/bookings/1\"" +
                        "}" +
                        "}" +
                        "}"))
                .andExpect(status().isNotFound());
    }

    @AfterClass
    public static void afterClass() throws IOException {
        final String actual = new String(Files.readAllBytes(Paths.get("src/test/resources/hoverfly.json")));
        final String expected = new String(Files.readAllBytes(Paths.get("generated/hoverfly.json")));
        assertThatJson(actual).isEqualTo(expected);
    }
}
