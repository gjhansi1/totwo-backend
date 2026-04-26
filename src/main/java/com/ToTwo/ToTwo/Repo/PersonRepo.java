package com.ToTwo.ToTwo.Repo;
import com.ToTwo.ToTwo.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PersonRepo extends JpaRepository<Person, Long> {
    
}
