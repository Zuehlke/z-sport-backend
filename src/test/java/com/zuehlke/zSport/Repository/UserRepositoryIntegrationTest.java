/*
package com.zuehlke.zSport.Repository;

import com.zuehlke.zSport.Model.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateUser() {
        User newUser = new User("axel@test.com");
        userRepository.save(newUser);
    }

    @Test
    public void testFindUserById() {
        User user = new User("alex@test.com");
        entityManager.persist(user);

        entityManager.persist(user);
        var userId = user.getId();
        entityManager.flush();

        User foundUser = userRepository.findById(userId);

        Assertions.assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }
}
*/
