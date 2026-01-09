package com.chellanim.journalApp.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection="journal_entries")
@Data  // Lombok automatically adds the getter setter , constructores, etc during compile time to the .class file
@NoArgsConstructor // need this because @NonNull exists
public class JournalEntry {
  @Id
  @JsonSerialize(using = ToStringSerializer.class)
  private ObjectId id;
  @NonNull  // this prevents auto no-args constructor
  private String title;

  private String content;

  private LocalDateTime date;

  private String sentiment;

}
