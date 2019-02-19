package com.zuehlke.zSport.Repository;

import com.zuehlke.zSport.Model.Event;
import com.zuehlke.zSport.Model.Sport;
import com.zuehlke.zSport.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserId(long userId);
    Event findById(long id);
    List<Event> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Event> findByDateAfter(LocalDateTime startDate);
    List<Event> findBySport(Sport sport);
}
