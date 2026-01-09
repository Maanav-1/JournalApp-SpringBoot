package com.chellanim.journalApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryDTO {
  private String id; // String version of ObjectId for API
  @NotBlank(message = "Title is required")
  private String title;
  private String content;
  private LocalDateTime date;
  private String sentiment; // For Phase 5

}