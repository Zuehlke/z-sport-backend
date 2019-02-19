package com.zuehlke.zSport.Controller;

import com.zuehlke.zSport.Model.Sport;
import com.zuehlke.zSport.Repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class SportController {

    private final SportRepository sportRepository;

    @Autowired
    public SportController(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    @GetMapping("/api/v1/sports")
    public List<Sport> getSports() {
        return sportRepository.findAll();
    }

    @PostMapping("/api/v1/sports")
    public Sport createSport(@Valid @RequestBody Sport sport) {
        return sportRepository.save(sport);
    }


}