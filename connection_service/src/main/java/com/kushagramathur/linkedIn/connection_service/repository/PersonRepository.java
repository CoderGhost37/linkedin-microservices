package com.kushagramathur.linkedIn.connection_service.repository;

import com.kushagramathur.linkedIn.connection_service.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, Long> {

    Optional<Person> findByUserId(Long userId);

    @Query("MATCH (personA:Person) -[:CONNECTED_TO]- (personB:Person) " +
            "WHERE personA.userId = $userId " +
            "RETURN personB")
    List<Person> getFirstDegreeConnections(Long userId);

     @Query("MATCH (personA:Person) -[:CONNECTED_TO]- (personB:Person) -[:CONNECTED_TO]- (personC:Person) " +
            "WHERE personA.userId = $userId " +
            "RETURN DISTINCT personC")
    List<Person> getSecondDegreeConnections(Long userId);

}
