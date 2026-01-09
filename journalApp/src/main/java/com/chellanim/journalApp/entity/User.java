package com.chellanim.journalApp.entity;



import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection="users")
@Data
@NoArgsConstructor
public class User {

  @Id
  @JsonSerialize(using = ToStringSerializer.class)
  private ObjectId id;

  @Indexed(unique=true)
  @NonNull
  private String userName;

  @NonNull
  private String password;

  @DBRef  // acts as a foreign key - basically links the JournalEntry to the User
  private List<JournalEntry> journalEntries = new ArrayList<>();

  private List<String> roles;

  private String email;
  private boolean sentimentAnalysis;



}
