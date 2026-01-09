package com.chellanim.journalApp.service;

import com.chellanim.journalApp.dto.JournalEntryDTO;
import com.chellanim.journalApp.entity.JournalEntry;
import com.chellanim.journalApp.entity.User;
import com.chellanim.journalApp.respository.JournalEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JournalEntryService {

  @Autowired
  private JournalEntryRepository journalEntryRepository;

  @Autowired
  private UserService userService;

  /**
   * Creates a new journal entry and links it to the authenticated user.
   */
  @Autowired
  private SentimentAnalysisService sentimentService;

  @Transactional
  public JournalEntryDTO saveEntry(JournalEntryDTO journalEntryDTO, String username) {
    User user = userService.findByUserName(username);
    JournalEntry entry = convertToEntity(journalEntryDTO);

    // Phase 5: AI Integration
    if (entry.getContent() != null && !entry.getContent().isBlank()) {
      String sentiment = sentimentService.analyzeSentiment(entry.getContent());
      entry.setSentiment(sentiment);
      log.info("Sentiment analyzed for entry: {}", sentiment);
    }

    entry.setDate(LocalDateTime.now());
    JournalEntry savedEntry = journalEntryRepository.save(entry);
    user.getJournalEntries().add(savedEntry);
    userService.saveEntry(user);

    return convertToDTO(savedEntry);
  }

  /**
   * Retrieves all entries belonging to a specific user.
   */
  public List<JournalEntryDTO> getAllEntriesByUser(String username) {
    User user = userService.findByUserName(username);
    if (user == null) {
      log.warn("Attempted to fetch entries for non-existent user: {}", username);
      return List.of();
    }

    return user.getJournalEntries().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  /**
   * Retrieves a specific entry only if it belongs to the authenticated user.
   */
  public Optional<JournalEntryDTO> findByIdAndUser(ObjectId id, String username) {
    User user = userService.findByUserName(username);
    boolean ownsEntry = user.getJournalEntries().stream()
        .anyMatch(entry -> entry.getId().equals(id));

    if (ownsEntry) {
      return journalEntryRepository.findById(id).map(this::convertToDTO);
    }

    log.warn("User {} attempted unauthorized access to entry ID: {}", username, id);
    return Optional.empty();
  }

  /**
   * Updates an existing entry after verifying ownership.
   */
  @Transactional
  public Optional<JournalEntryDTO> updateEntry(ObjectId id, JournalEntryDTO newEntryDTO, String username) {
    User user = userService.findByUserName(username);
    Optional<JournalEntry> existingEntryOpt = user.getJournalEntries().stream()
        .filter(entry -> entry.getId().equals(id))
        .findFirst();

    if (existingEntryOpt.isPresent()) {
      JournalEntry oldEntry = existingEntryOpt.get();

      if (newEntryDTO.getTitle() != null && !newEntryDTO.getTitle().isBlank()) {
        oldEntry.setTitle(newEntryDTO.getTitle());
      }
      if (newEntryDTO.getContent() != null) {
        oldEntry.setContent(newEntryDTO.getContent());
      }

      JournalEntry updated = journalEntryRepository.save(oldEntry);
      log.info("User {} updated journal entry ID: {}", username, id);
      return Optional.of(convertToDTO(updated));
    }

    return Optional.empty();
  }

  /**
   * Deletes an entry and removes the reference from the user's record.
   */
  @Transactional
  public boolean deleteById(ObjectId id, String username) {
    User user = userService.findByUserName(username);
    if (user != null) {
      boolean removed = user.getJournalEntries().removeIf(entry -> entry.getId().equals(id));
      if (removed) {
        userService.saveEntry(user);
        journalEntryRepository.deleteById(id);
        log.info("User {} deleted journal entry ID: {}", username, id);
        return true;
      }
    }
    log.warn("Failed delete attempt by user {} for entry ID: {}", username, id);
    return false;
  }

  // --- Helper Methods for Layer Separation ---

  private JournalEntryDTO convertToDTO(JournalEntry entity) {
    JournalEntryDTO dto = new JournalEntryDTO();
    dto.setId(entity.getId().toHexString());
    dto.setTitle(entity.getTitle());
    dto.setContent(entity.getContent());
    dto.setDate(entity.getDate());
    dto.setSentiment(entity.getSentiment());
    return dto;
  }

  private JournalEntry convertToEntity(JournalEntryDTO dto) {
    JournalEntry entity = new JournalEntry();
    if (dto.getId() != null) {
      entity.setId(new ObjectId(dto.getId()));
    }
    entity.setTitle(dto.getTitle());
    entity.setContent(dto.getContent());
    return entity;
  }
}