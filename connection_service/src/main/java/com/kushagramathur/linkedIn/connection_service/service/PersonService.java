package com.kushagramathur.linkedIn.connection_service.service;

import com.kushagramathur.linkedIn.connection_service.entity.Person;
import com.kushagramathur.linkedIn.connection_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;

    public List<Person> getFirstDegreeConnectionsOfUser(Long userId) {
        log.info("Getting first degree connections for userId: {}", userId);
        return personRepository.getFirstDegreeConnections(userId);
    }

}
