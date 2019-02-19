package com.zuehlke.zSport.Controller;

import com.zuehlke.zSport.Model.Event;
import com.zuehlke.zSport.Model.Sport;
import com.zuehlke.zSport.Model.User;
import com.zuehlke.zSport.Repository.EventRepository;
import com.zuehlke.zSport.Repository.SportRepository;
import com.zuehlke.zSport.Repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

@RunWith(SpringRunner.class)
@WebMvcTest(EventController.class)
public class EventControllerIntegrationTest {

    @Configuration
    @ComponentScan(basePackageClasses = EventController.class)
    public static class TestConf {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private SportRepository sportRepository;

    @MockBean
    private UserRepository userRepository;

    private Event mockedEvent1;
    private Event mockedEvent2;
    private Sport tennis;
    private Sport badminton;
    private User testUser;


    @Before
    public void setupObjects() {
        mockedEvent1 = new Event(
                "Some sport event", "custom location", LocalDateTime.now(), 2.5,
                5, 2, LocalDateTime.now().plusDays(1));

        mockedEvent2 = new Event(
                "Some other sport event", "secret location", LocalDateTime.now().plusDays(20), 2.5,
                10, 5, LocalDateTime.now().plusDays(22));

        tennis = new Sport("Tennis");
        badminton = new Sport("Badminton");
        mockedEvent1.setId(201);
        mockedEvent2.setId(202);
        mockedEvent1.setSport(tennis);
        mockedEvent2.setSport(badminton);

        testUser = new User("testUser@test.com");
        testUser.setId(101);

        setupMocks();
    }

    private void setupMocks() {
        Mockito.when(eventRepository.findAll()).thenReturn(new LinkedList<>(Arrays.asList(mockedEvent1, mockedEvent2)));

        var filterDate = LocalDateTime.of(2018, 10, 20, 0, 0);
        Mockito.when(eventRepository.findByDateBetween(filterDate, filterDate.plusDays(1)))
                .thenReturn(new LinkedList<>(Collections.singletonList(mockedEvent1)));
        Mockito.when(eventRepository.findById(mockedEvent1.getId())).thenReturn(mockedEvent1);
        Mockito.when(eventRepository.findById(mockedEvent2.getId())).thenReturn(mockedEvent2);

        Mockito.when(eventRepository.findBySport(tennis)).thenReturn(Collections.singletonList(mockedEvent1));
        Mockito.when(eventRepository.findBySport(badminton)).thenReturn(Collections.singletonList(mockedEvent2));

        Mockito.when(userRepository.findById(testUser.getId())).thenReturn(testUser);
    }

    @Test
    public void testGetEventsByFilterNoParameters() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/events"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void testGetEventsByFilterOnlyDateParameter() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/events").param("date", "2018-10-20"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$[0].id", Matchers.is((int) mockedEvent1.getId())));
    }

    @Test
    public void testGetEventsByFilterOnlySportParameter() throws Exception {
        var sportId = 1;
        Mockito.when(sportRepository.findById(sportId)).thenReturn(badminton);

        mockMvc.perform(MockMvcRequestBuilders.
                get("/api/v1/events").param("sportId", String.valueOf(sportId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$[0].id", Matchers.is((int) mockedEvent2.getId())));
    }

    @Test
    public void testGetEventsByFilterBothParameters() throws Exception {
        var sportId = 2;
        Mockito.when(sportRepository.findById(sportId)).thenReturn(tennis);

        mockMvc.perform(MockMvcRequestBuilders.
                get("/api/v1/events")
                .param("sportId", String.valueOf(sportId))
                .param("date", "2018-10-20"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.
                        jsonPath("$[0].id", Matchers.is((int) mockedEvent1.getId())));
    }

    @Test
    public void testGetEventsByFilterInvalidDateFormat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/events").param("date", "20181020"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testApplyToEvent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/events/201/apply")
                .param("userId", String.valueOf(testUser.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertThat(mockedEvent1.getParticipants())
                .hasSize(1)
                .containsOnly(testUser);

        Assertions.assertThat(testUser.getRegisteredEvents())
                .hasSize(1)
                .containsOnly(mockedEvent1);
    }

    @Test
    public void testApplyToEventInvalidUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/events/201/apply")
                .param("userId", String.valueOf(999)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void testApplyToEventInvalidEvent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/events/999/apply")
                .param("userId", String.valueOf(testUser.getId())))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

}
