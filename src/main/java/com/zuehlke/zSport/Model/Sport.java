package com.zuehlke.zSport.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "sport")
public class Sport {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "sport_sequence")
    @SequenceGenerator(name = "sport_sequence", sequenceName = "sports_seq")
    private long id;

    @Column(name="name")
    private String name;

    public Sport() {
    }

    public Sport(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "sport", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JsonIgnore
    private Set<Event> sportEvents;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Event> getSportEvents() {
        return sportEvents;
    }

    public void setSportEvents(Set<Event> sportEvents) {
        this.sportEvents = sportEvents;
    }

    public void addEvent(Event tempEvent) {
        if (sportEvents == null) {
            sportEvents = new HashSet<>();
        }
        sportEvents.add(tempEvent);
        tempEvent.setSport(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sport sport = (Sport) o;
        return id == sport.id;
    }

    @Override
    public String toString() {
        return "Sport{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
