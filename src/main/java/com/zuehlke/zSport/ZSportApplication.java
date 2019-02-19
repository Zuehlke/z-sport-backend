package com.zuehlke.zSport;

import com.zuehlke.zSport.Model.Sport;
import com.zuehlke.zSport.Model.User;
import com.zuehlke.zSport.Model.Event;
import com.zuehlke.zSport.Repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class ZSportApplication {

    private final static Logger LOG = LoggerFactory.getLogger(ZSportApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ZSportApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(UserRepository repoUser, SportRepository repoSport, EventRepository repoEvent) {
        return args -> {
            LOG.info("Add demo data...");

            Sport football = findOrCreateSport(repoSport, "Fussball");
            Sport squash = findOrCreateSport(repoSport, "Squash");
            Sport tennis = findOrCreateSport(repoSport, "Tennis");
            Sport climbing = findOrCreateSport(repoSport, "Klettern");
            repoSport.save(football);
            repoSport.save(squash);
            repoSport.save(tennis);
            repoSport.save(climbing);
            repoEvent.flush();
            LOG.info("Add demo data: Sports added.");

            User max = findOrCreateUser(repoUser, "max.mustermann@zuehlke.com");
            User petra = findOrCreateUser(repoUser, "petra.heidemann@zuehlke.com");
            User lukas = findOrCreateUser(repoUser, "lukas.marti@zuehlke.com");
            User gian = findOrCreateUser(repoUser, "gian.schneider@zuehlke.com");
            User andrea = findOrCreateUser(repoUser, "andrea.schuler@zuehlke.com");

            if(max.getSubscribedSports() == null || max.getSubscribedSports().isEmpty()){
                Set<Sport> sports = new HashSet<>();
                sports.add(football);
                sports.add(tennis);
                max.setSubscribedSports(sports);
            }

            max.setLastLoginTime(LocalDateTime.now());
            petra.setLastLoginTime(LocalDateTime.now());
            lukas.setLastLoginTime(LocalDateTime.now());
            gian.setLastLoginTime(LocalDateTime.now());
            andrea.setLastLoginTime(LocalDateTime.now());

            repoUser.save(max);
            repoUser.save(petra);
            repoUser.save(lukas);
            repoUser.save(gian);
            repoUser.save(andrea);
            LOG.info("Add demo data: Users added.");



            Event footballEvent = new Event("[DEMO] Hallenfussball", "Linth Arena SGU, Näfels", LocalDateTime.now().minusHours(20), 2.5, 10, 4, LocalDateTime.now().minusDays(2));
            Event squashEvent = new Event("[DEMO] Squash Mittag", "Vitis, Schlieren", LocalDateTime.now().minusHours(10), 2.0, 4, 2, LocalDateTime.now().minusDays(1));
            Event tennisEvent = new Event("[DEMO] Tennis", "Vitis, Schlieren", LocalDateTime.now().plusHours(8), 1.5, 4, 2, LocalDateTime.now().plusHours(2));
            Event boulderEvent = new Event("[DEMO] Bouldern", "Boulderhalle Seedamm, Rapperswil", LocalDateTime.now().plusDays(3), 1.5, 10, 2, LocalDateTime.now().plusDays(1));
            footballEvent.setUser(max);
            squashEvent.setUser(petra);
            boulderEvent.setUser(lukas);
            tennisEvent.setUser(gian);
            footballEvent.addParticipant(max);
            squashEvent.addParticipant(max);
            squashEvent.addParticipant(petra);
            boulderEvent.addParticipant(lukas);
            tennisEvent.addParticipant(gian);
            tennisEvent.addParticipant(andrea);
            footballEvent.setSport(football);
            squashEvent.setSport(squash);
            boulderEvent.setSport(climbing);
            tennisEvent.setSport(tennis);
            repoEvent.save(footballEvent);
            repoEvent.save(squashEvent);
            repoEvent.save(boulderEvent);
            repoEvent.save(tennisEvent);
            LOG.info("Add demo data: Events added.");

            LOG.info("Add demo finished...");
        };
    }

    private User findOrCreateUser(UserRepository repoUser, String email) {
        User user = repoUser.findByEmail(email);
        if (user == null) user = new User(email);
        return user;
    }

    private Sport findOrCreateSport(SportRepository repo, String name) {
        Sport sport = repo.findByName(name);
        if (sport == null) sport = new Sport(name);
        return sport;
    }

}
