package edu.ucsb.cs156.team02.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;

import java.util.Optional;

@Repository
public interface UCSBRequirementRepository extends CrudRepository<UCSBRequirement, Long> {
    Iterable<UCSBRequirement> findByRequirementCode(String RequirementCode);
}

