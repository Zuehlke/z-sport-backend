/*
package com.zuehlke.zSport.Repository;

import com.zuehlke.zSport.Model.Event;
import com.zuehlke.zSport.Model.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EventRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testFindEventByCreator() {
        Event event = new Event(
                "Some sport event", "custom location", LocalDateTime.now(), 2.5,
                5, 2, LocalDateTime.now().plusDays(1));

        User eventCreator = new User("hans@muster.org");
        event.setUser(eventCreator);
        entityManager.persist(eventCreator);
        entityManager.persist(event);
        entityManager.flush();

        var foundEvents = eventRepository.findByUserId(eventCreator.getId());

        Assertions.assertThat(foundEvents)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("user", eventCreator);
    }
}
*/
