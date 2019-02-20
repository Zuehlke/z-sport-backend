/*
package com.zuehlke.zSport;

import com.zuehlke.zSport.Model.Event;
import com.zuehlke.zSport.Model.Sport;
import com.zuehlke.zSport.Model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest

public class ZSportModelTests {

    Event event;
    User user;
    Sport sport;

    @Before
    public void setUp() {
        user = new User();
        sport = new Sport();
        event = new Event();
        user.addCreatedEvent(event);
        user.addRegisteredEvent(event);
        sport.addEvent(event);
    }

    @Test
    public void getUserId(){
        long idValue = 4L;
        user.setId(idValue);
        assertEquals(idValue, user.getId());
    }
}
*/
