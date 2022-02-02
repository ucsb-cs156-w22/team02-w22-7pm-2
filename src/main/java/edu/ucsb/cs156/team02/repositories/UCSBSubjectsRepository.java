package edu.ucsb.cs156.team02.repositories;

import edu.ucsb.cs156.team02.entities.UCSBSubjects;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UCSBSubjectsRepository extends CrudRepository<UCSBSubjects, Long> {
    Iterable<UCSBSubjects> findBySubject(String subject);
}