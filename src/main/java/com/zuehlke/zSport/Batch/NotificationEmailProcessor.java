package com.zuehlke.zSport.Batch;

import com.zuehlke.zSport.Model.Event;
import com.zuehlke.zSport.Model.Sport;
import com.zuehlke.zSport.Model.User;
import com.zuehlke.zSport.Repository.EventRepository;
import com.zuehlke.zSport.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class NotificationEmailProcessor implements ItemProcessor<User, NotificationEmail> {

    private final static Logger LOG = LoggerFactory.getLogger(NotificationBatchConfiguration.class);


    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Value("${application.url}")
    private String applicationUrl;

    public NotificationEmailProcessor(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Fetches the user data for the passed userId and creates the notification content
     *
     * @param userId - the passed object contains only the userId
     * @return the email to notify the user
     * @throws Exception
     */
    @Override
    public NotificationEmail process(User userId) throws Exception {

        User user = userRepository.findById(userId.getId());
        if (user.getLastLoginTime() != null && !user.getSubscribedSports().isEmpty()) {

            Map<Sport, List<Event>> eventsSportMap = new HashMap<>();
            Set<Sport> subscribedSports = user.getSubscribedSports();
            subscribedSports.stream().forEach(sport -> {
                List<Event> relevantEvents = collectEventsForUserAndSport(user, sport);
                if(!relevantEvents.isEmpty()) {
                    eventsSportMap.put(sport, relevantEvents);
                }
            });

            if (eventsSportMap != null && !eventsSportMap.isEmpty()) {
                LOG.debug("User " + user.getEmail() + " is interested in " + user.getSubscribedSports() + " and needs to be notified.");
                return createNotificationEmail(user.getEmail(), "zSport event notification", eventsSportMap);
            }
        }
        LOG.trace("No notification email for user <" + user.getId() + "> needed.");
        return null;
    }

    private List<Event> collectEventsForUserAndSport(User user, Sport sport) {
        //todo filter events not only by sports with database query
        List<Event> events = eventRepository.findBySport(sport);
        List<Event> relevantEvents = events.stream().filter(
                event ->
                        !event.getUser().equals(user) // if user is not creator
                                && (event.getParticipants() == null || !event.getParticipants().contains(user)) // if user not already participating
                                && (event.getDeadLine() == null || event.getDeadLine().isAfter(LocalDateTime.now())) // if event still subscribable
                                && (event.getDate() == null || event.getDate().isAfter(LocalDateTime.now())) // if event not done
                                && event.getCreationTime().isAfter(user.getLastLoginTime())// only events created after last login
                                && (user.getLastNotificationTime() == null || event.getCreationTime().isAfter(user.getLastNotificationTime()))// only if not already notified
        ).collect(Collectors.toList());
        return relevantEvents;
    }

    private NotificationEmail createNotificationEmail(String email, String subject, Map<Sport, List<Event>> eventMap) {
        NotificationEmail result = new NotificationEmail();
        result.setRecipient(email);
        result.setSubject(subject);
        StringBuilder content = new StringBuilder("Check out the following events:\n\n");
        for (Sport sport : eventMap.keySet()) {
            content.append(sport.getName() + ":\n");
            eventMap.get(sport).stream().map(event ->
                    event.getDate() + ":" + event.getDescription() + " -> " + applicationUrl + "event/" + event.getId() + "\n")
                    .forEach(eventString -> content.append(eventString));
            content.append("\n");
        }
        result.setContent(content.toString());
        return result;
    }
}
