package com.chellanim.journalApp.scheduler;

import com.chellanim.journalApp.entity.JournalEntry;
import com.chellanim.journalApp.entity.User;
import com.chellanim.journalApp.service.EmailService;
import com.chellanim.journalApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WeeklySentimentScheduler {

  @Autowired
  private UserService userService;

  @Autowired
  private EmailService emailService;

  /**
   * Runs every Sunday at midnight (00:00:00).
   * Cron expression: second minute hour day-of-month month day-of-week
   */
  @Scheduled(cron = "0 0 0 * * SUN")
  public void sendWeeklySentimentReport() {
    log.info("Starting weekly sentiment report generation...");

    List<User> users = userService.getAll();
    LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

    for (User user : users) {
      // Only send if the user has opted in and has an email registered
      if (user.isSentimentAnalysis() && user.getEmail() != null) {

        List<JournalEntry> recentEntries = user.getJournalEntries().stream()
            .filter(entry -> entry.getDate().isAfter(sevenDaysAgo))
            .collect(Collectors.toList());

        if (!recentEntries.isEmpty()) {
          String reportBody = generateMoodReport(recentEntries);
          emailService.sendEmail(
              user.getEmail(),
              "Your Weekly Mood Summary - Journal App",
              reportBody
          );
          log.info("Sent weekly report to user: {}", user.getUserName());
        }
      }
    }
  }

  private String generateMoodReport(List<JournalEntry> entries) {
    // Count the occurrences of each sentiment
    Map<String, Long> counts = entries.stream()
        .filter(e -> e.getSentiment() != null)
        .collect(Collectors.groupingBy(JournalEntry::getSentiment, Collectors.counting()));

    StringBuilder report = new StringBuilder("Here is your mood summary for the past week:\n\n");

    counts.forEach((sentiment, count) -> {
      report.append("- ").append(sentiment).append(": ").append(count).append(" entries\n");
    });

    report.append("\nKeep journaling to track your mental well-being!");
    return report.toString();
  }
}