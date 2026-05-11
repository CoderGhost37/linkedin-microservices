package com.kushagramathur.linkedIn.connection_service.controller;

import com.kushagramathur.linkedIn.connection_service.entity.Person;
import com.kushagramathur.linkedIn.connection_service.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
public class ConnectionController {

    private final PersonService personService;

    @GetMapping("/{userId}/first-degree")
    public ResponseEntity<List<Person>> getFirstDegreeConnections(@PathVariable Long userId) {
        List<Person> connections = personService.getFirstDegreeConnectionsOfUser(userId);
        return ResponseEntity.ok(connections);
    }

}
