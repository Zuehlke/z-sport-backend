package com.zuehlke.zSport.Controller;

import com.zuehlke.zSport.Model.Event;
import com.zuehlke.zSport.Model.User;
import com.zuehlke.zSport.Repository.EventRepository;
import com.zuehlke.zSport.Repository.SportRepository;
import com.zuehlke.zSport.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class EventController {

    private final EventRepository eventRepository;
    private final SportRepository sportRepository;
    private final UserRepository userRepository;

    private final static Logger LOG = LoggerFactory.getLogger(EventController.class);

    @Autowired
    public EventController(EventRepository eventRepository, SportRepository sportRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.sportRepository = sportRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/v1/events")
    public List<Event> getEventsByFilter(
            @RequestParam(value = "sportId", required = false) Integer sportId,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<Event> events;
        if (date != null) {
            events = eventRepository.findByDateBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        } else {
            events = eventRepository.findAll();
        }

        if (sportId != null) {
            var sport = sportRepository.findById(sportId);
            if (sport != null) {
                events.retainAll(eventRepository.findBySport(sport));
            }
        }
        return events;
    }

    @PostMapping("/api/v1/events")
    public ResponseEntity<Object> createEvent(@Valid @RequestBody Event event) {
        try {
            validateDateAndDeadline(event);
        }
        catch(ValidationException error){
            LOG.error(error.getMessage());
            return new ResponseEntity<>(error.getMessage(), HttpStatus.BAD_REQUEST);
        }
        event.addParticipant(event.getUser());
        eventRepository.saveAndFlush(event);
        LOG.info("New event added: " + event.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param event to validate
     * @throws ValidationException if date or deadline in past or deadline after date
     */
    private void validateDateAndDeadline(Event event) throws ValidationException {
        String errorString = "";
        // safety buffer to prevent errors in edge cases
        LocalDateTime localDateNow = LocalDateTime.now().minusMinutes(5);

        if (!event.getDate().isAfter(localDateNow)){
            errorString += "Event date cannot be set in the past. ";
        }
        if(!event.getDeadLine().isBefore(event.getDate())){
            errorString += "Deadline must be set before event date. ";
        }
        if(!event.getDeadLine().isAfter(localDateNow)){
            errorString += "Deadline cannot be set in the past.";
        }
        if(!errorString.isBlank()) {
            throw new ValidationException(errorString);
        }
    }

    @GetMapping("/api/v1/users/{userId}/events")
    public List<Event> getEventsByUserId(@PathVariable Long userId) {
        List<Event> allEvents = eventRepository.findByDateAfter(LocalDateTime.now().minusDays(1));
        Set<Event> events = new HashSet<>();
        if (userId != null) {
            for (Event tempEvent : allEvents) {
                Set<User> participants = new HashSet<>();
                for (User tempUser : tempEvent.getParticipants()) {
                    participants.add(tempUser);
                    if (tempUser.getId() == userId) {
                        events.add(tempEvent);
                    }
                }
                Set<User> tempList = new HashSet<>();
                tempList.addAll(participants);
                tempEvent.setParticipants(tempList);
            }
        }
        List<Event> result = new ArrayList<>();
        result.addAll(events);
        return result;
    }

    @PostMapping("/api/v1/events/{eventId}/apply")
    public ResponseEntity<Object> applyToEvent(@PathVariable long eventId,
                                       @RequestParam("userId") long userId) {
        var event = eventRepository.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }
        var user = userRepository.findById(userId);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
        if (event.getParticipants().contains(user)) {
            return new ResponseEntity<>("User already subscribed", HttpStatus.CONFLICT);
        }
        if (event.getParticipants().size() == event.getMaxParticipants()) {
            return new ResponseEntity<>("Max Participants reached", HttpStatus.FORBIDDEN);
        }
        user.addRegisteredEvent(event);
        userRepository.saveAndFlush(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/api/v1/events/{eventId}/unsubscribe")
    public ResponseEntity<Object> unsubscribeFromEvent(@PathVariable long eventId,
                                               @RequestParam("userId") long userId) {
        var event = eventRepository.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }
        var user = userRepository.findById(userId);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
        user.removeRegisteredEvent(event);
        userRepository.saveAndFlush(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/v1/events/{eventId}")
    public Event getEventByEventId(@PathVariable long eventId) {
        return eventRepository.findById(eventId);
    }
}
