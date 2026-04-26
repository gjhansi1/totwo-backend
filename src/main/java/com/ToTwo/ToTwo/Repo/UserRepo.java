package com.ToTwo.ToTwo.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ToTwo.ToTwo.model.User;

public interface UserRepo extends JpaRepository<User, Long> {


	Optional<User> findByEmail(String email);



    // ✅ signup counter
    long count();
}
