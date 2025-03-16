package com.company.enroller.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/participants")
public class ParticipantRestController {

	@Autowired
	ParticipantService participantService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getParticipants() {
		Collection<Participant> participants = participantService.getAll();
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getParticipant(@PathVariable("id") String login) {
		Participant participant = participantService.findByLogin(login);
		if (participant == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Participant>(participant, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> registerParticipant(@RequestBody Participant participant){
		if (participantService.findByLogin(participant.getLogin()) != null){
			return new ResponseEntity("Unable to create. A participant with login " + participant.getLogin() +
					" already exist.", HttpStatus.CONFLICT);
		}
		participantService.addParticipant(participant);
		return new ResponseEntity(HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteParticipant(@PathVariable("id") String login) {
		Participant participant = participantService.findByLogin(login);
		if (participant != null){
			participantService.removeParticipant(participant);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value="/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateParticipant(@PathVariable("id") String login, @RequestBody Participant participant) {
		Participant updatedParticipant = participantService.findByLogin(login);
		updatedParticipant.setLogin(participant.getLogin());
		updatedParticipant.setPassword(participant.getPassword());
		if (participant == null){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		participantService.updateParticipant(updatedParticipant);
        return new ResponseEntity<>(HttpStatus.OK);
    }

	@RequestMapping(value = "/sort", method = RequestMethod.GET)
	public ResponseEntity<?> getSortedParticipants(@RequestParam String sortBy, @RequestParam(defaultValue="ASC", required = false) String sortOrder) {
		Collection<Participant> participants = participantService.getAll();
		Collection<String> logins = new ArrayList<>();
		logins = participants.stream().map(Participant::getLogin).sorted().collect(Collectors.toList());
		return new ResponseEntity<Collection<String>>(logins, HttpStatus.OK);
	}
}


