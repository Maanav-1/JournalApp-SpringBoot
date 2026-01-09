package com.chellanim.journalApp.respository;

import com.chellanim.journalApp.entity.User;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor // Lombok generates the constructor for final fields i.e mongotemplate
public class UserRepositoryImpl {

  private final MongoTemplate mongoTemplate; // Marking it final is safer

  //  SA - sentiment analysis
  public List<User> getUsersForSA() {
    Query query = new Query();
    query.addCriteria(Criteria.where("email").regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"));
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));

        return mongoTemplate.find(query, User.class);
  }
}
