package com.ToTwo.ToTwo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ToTwo.ToTwo.Repo.PersonRepo;
import com.ToTwo.ToTwo.model.Person;

@RestController
@RequestMapping("/person")
@CrossOrigin // allows requests from any origin
public class PersonController {

    @Autowired
    private PersonRepo repo;

    @PostMapping
    public Person addPerson(@RequestBody Person person) {
        return repo.save(person);
    }

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @GetMapping
    public List<Person> getAllPersons() {
        return repo.findAll();
    }

    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable Long id) {
        repo.deleteById(id);
        return "Person deleted with id " + id;
    }

    @PutMapping("/{id}")
    public Person updatePerson(@PathVariable Long id, @RequestBody Person person) {
        person.setId(id);
        return repo.save(person);
    }
}
