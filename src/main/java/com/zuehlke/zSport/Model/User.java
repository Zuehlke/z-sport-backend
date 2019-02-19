package com.zuehlke.zSport.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.zuehlke.zSport.CustomJsonDateDeserializer;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "users_seq")
    private long id;

    @Column(name = "email")
    private String email;

    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "lastLogin")
    private LocalDateTime lastLoginTime;

    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "lastNotification")
    private LocalDateTime lastNotificationTime;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JsonIgnore
    private List<Event> createdEvents;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "user_registeredevent", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    @JsonIgnore
    private List<Event> registeredEvents;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Sport> subscribedSports;

    public User() {
    }

    public User(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public LocalDateTime getLastNotificationTime() {
        return lastNotificationTime;
    }

    public void setLastNotificationTime(LocalDateTime lastNotificationTime) {
        this.lastNotificationTime = lastNotificationTime;
    }

    public List<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void setCreatedEvents(List<Event> createdEvents) {
        this.createdEvents = createdEvents;
    }

    public List<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    public void setRegisteredEvents(List<Event> registeredEvents) {
        this.registeredEvents = registeredEvents;
    }

    public Set<Sport> getSubscribedSports() {
        return subscribedSports;
    }

    public void setSubscribedSports(Set<Sport> subscribedSports) {
        this.subscribedSports = subscribedSports;
    }

    public void addCreatedEvent(Event tempEvent) {
        if (createdEvents == null) {
            createdEvents = new ArrayList<>();
        }
        createdEvents.add(tempEvent);
        tempEvent.setUser(this);
    }

    public void addRegisteredEvent(Event tempEvent) {
        if (registeredEvents == null) {
            registeredEvents = new ArrayList<>();
        }
        if (!registeredEvents.contains(tempEvent)) {
            registeredEvents.add(tempEvent);
        }
    }

    public void removeRegisteredEvent(Event tempEvent) {
        if (registeredEvents != null) {
            registeredEvents.remove(tempEvent);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", lastNotificationTime=" + lastNotificationTime +
                '}';
    }
}
