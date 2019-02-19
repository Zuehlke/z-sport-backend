package com.zuehlke.zSport.Repository;

import com.zuehlke.zSport.Model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {
    Sport findById(long sportId);

    @Query("SELECT sport FROM Sport sport  WHERE sport.name=(:name)")
    Sport findByName(@Param("name") String name);
}
