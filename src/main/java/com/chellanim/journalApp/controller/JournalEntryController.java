package com.chellanim.journalApp.controller;

import com.chellanim.journalApp.dto.JournalEntryDTO;
import com.chellanim.journalApp.service.JournalEntryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
@Slf4j
public class JournalEntryController {

  @Autowired
  private JournalEntryService journalEntryService;

  /**
   * Get all journal entries for the authenticated user.
   */
  @GetMapping
  public ResponseEntity<List<JournalEntryDTO>> getAllEntriesOfUser() {
    String username = getAuthenticatedUsername();
    List<JournalEntryDTO> entries = journalEntryService.getAllEntriesByUser(username);
    return new ResponseEntity<>(entries, HttpStatus.OK);
  }

  /**
   * Create a new journal entry for the authenticated user.
   */
  @PostMapping
  public ResponseEntity<JournalEntryDTO> createJournalEntry(@Valid @RequestBody JournalEntryDTO myEntry) {
    String username = getAuthenticatedUsername();
    JournalEntryDTO savedEntry = journalEntryService.saveEntry(myEntry, username);
    return new ResponseEntity<>(savedEntry, HttpStatus.CREATED);
  }

  /**
   * Get a specific journal entry by ID, only if it belongs to the authenticated user.
   */
  @GetMapping("/id/{myid}")
  public ResponseEntity<JournalEntryDTO> getJournalEntryById(@PathVariable ObjectId myid) {
    String username = getAuthenticatedUsername();
    return journalEntryService.findByIdAndUser(myid, username)
        .map(entry -> new ResponseEntity<>(entry, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Delete a journal entry by ID, only if it belongs to the authenticated user.
   */
  @DeleteMapping("/id/{myid}")
  public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myid) {
    String username = getAuthenticatedUsername();
    boolean deleted = journalEntryService.deleteById(myid, username);

    if (deleted) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  /**
   * Update an existing journal entry by ID.
   * Ownership check is performed in the service layer.
   */
  @PutMapping("/id/{myid}")
  public ResponseEntity<JournalEntryDTO> updateJournalEntryById(
      @PathVariable ObjectId myid,
      @Valid @RequestBody JournalEntryDTO newEntry) {

    String username = getAuthenticatedUsername();
    return journalEntryService.updateEntry(myid, newEntry, username)
        .map(updated -> new ResponseEntity<>(updated, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Helper method to extract the username from the Security Context.
   */
  private String getAuthenticatedUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName();
  }
}