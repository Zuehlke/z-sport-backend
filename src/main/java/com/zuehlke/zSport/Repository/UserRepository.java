package com.zuehlke.zSport.Repository;

import com.zuehlke.zSport.Model.Event;
import com.zuehlke.zSport.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findById(long id);

    @Query("SELECT user FROM User user  WHERE user.email=(:email)")
    User findByEmail(@Param("email") String email);
}
