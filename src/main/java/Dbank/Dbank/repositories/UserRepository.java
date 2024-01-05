package Dbank.Dbank.repositories;

import Dbank.Dbank.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findUserByDocument(String user);

    Optional<User> findUserById(Long id);
}
