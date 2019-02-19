package com.zuehlke.zSport.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.zuehlke.zSport.CustomJsonDateDeserializer;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_sequence")
    @SequenceGenerator(name = "event_sequence", sequenceName = "events_seq")
    private long id;

    @CreationTimestamp
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationTime;

    @Column(name = "description")
    private String description;

    @Column(name = "location")
    private String location;

    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "duration")
    private Double duration; //LOL

    @Column(name = "max_participants")
    private int maxParticipants;

    @Column(name = "min_participants")
    private int minParticipants;

    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "deadline")
    private LocalDateTime deadLine;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "creator_id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "user_registeredEvent", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> participants;

    public Event() {
    }

    public Event(String description, String location, LocalDateTime date, Double duration, int maxParticipants, int minParticipants, LocalDateTime deadLine) {
        this.description = description;
        this.location = location;
        this.date = date;
        this.duration = duration;
        this.maxParticipants = maxParticipants;
        this.minParticipants = minParticipants;
        this.deadLine = deadLine;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreationTime() { return creationTime; }

    public void setCreationTime(LocalDateTime creationTime) { this.creationTime = creationTime; }

    public LocalDateTime getDate() {
        return date;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public long getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public long getMinParticipants() {
        return minParticipants;
    }

    public void setMinParticipants(int minParticipants) {
        this.minParticipants = minParticipants;
    }

    public LocalDateTime getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(LocalDateTime deadLine) {
        this.deadLine = deadLine;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User creator) {
        this.user = creator;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public void addParticipant(User user) {
        if (participants == null) {
            participants = new HashSet<>();
        }
        if (!participants.contains(user)) {
            participants.add(user);
        }
    }

    public void removeParticipant(User user) {
        if (participants != null) {
            participants.remove(user);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", creationTime=" + creationTime +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", date=" + date +
                ", duration=" + duration +
                ", maxParticipants=" + maxParticipants +
                ", minParticipants=" + minParticipants +
                ", deadLine=" + deadLine +
                ", sport=" + sport +
                ", user=" + user +
                ", participants=" + participants +
                '}';
    }
}
