package com.zuehlke.zSport.Controller;

import com.zuehlke.zSport.Model.Sport;
import com.zuehlke.zSport.Model.User;
import com.zuehlke.zSport.Repository.SportRepository;
import com.zuehlke.zSport.Repository.UserRepository;
import com.zuehlke.zSport.ZSportApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class UserController {

    private final static Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    private final SportRepository sportRepository;

    @Autowired
    public UserController(UserRepository userRepository, SportRepository sportRepository) {
        this.userRepository = userRepository;
        this.sportRepository = sportRepository;
    }

    @PostMapping("/api/v1/users")
    public User createUser(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/api/v1/users/{id}")
    public Optional<User> getUserByUserId(@PathVariable Long id) {
        Optional<User> result = userRepository.findById(id);
        if(result.isPresent()){
            // todo move this code to the actual authentication business logic as soon as available
            User user = result.get();
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.saveAndFlush(user);
        }
        return result;
    }

    @PostMapping("/api/v1/users/{id}/subscribe")
    public ResponseEntity<Object> subscribeSport(@PathVariable long id,
                                               @RequestParam("sportId") long sportId) {
        LOG.info("subscribe user "+id+" to sport "+sportId);
        Sport sport = sportRepository.findById(sportId);
        if (sport == null) {
            return new ResponseEntity<>("Sport not found", HttpStatus.NOT_FOUND);
        }
        User user = userRepository.findById(id);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
        user.getSubscribedSports().add(sport);
        userRepository.saveAndFlush(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/api/v1/users/{id}/unsubscribe")
    public ResponseEntity<Object> unsubscribeSport(@PathVariable long id,
                                               @RequestParam("sportId") long sportId) {
        LOG.info("unsubscribe user "+id+" to sport "+sportId);
        Sport sport = sportRepository.findById(sportId);
        if (sport == null) {
            return new ResponseEntity<>("Sport not found", HttpStatus.NOT_FOUND);
        }
        User user = userRepository.findById(id);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
        if (user.getSubscribedSports().remove(sport)) {
            LOG.debug("Sport <" + sportId + "> successfully unsubscribed for user <" + id + ">.");
        } else {
            LOG.debug("Sport <" + sportId + "> already was not subscribed for user <" + id + ">.");
        }
        userRepository.saveAndFlush(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
