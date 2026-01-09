package com.chellanim.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;

@Service
@Slf4j
public class SentimentAnalysisService {

  @Value("${gemini.api.key}")
  private String apiKey;

  @Value("${gemini.api.url}")
  private String apiUrl;

  private final RestTemplate restTemplate = new RestTemplate();

  public String analyzeSentiment(String text) {
    try {
      String fullUrl = apiUrl + "?key=" + apiKey;

      // Prepare request body for Gemini
      Map<String, Object> requestBody = Map.of(
          "contents", List.of(Map.of(
              "parts", List.of(Map.of(
                  "text", "Analyze the sentiment of this text and return only ONE word (POSITIVE, NEGATIVE, or NEUTRAL): " + text
              ))
          ))
      );

      Map<String, Object> response = restTemplate.postForObject(fullUrl, requestBody, Map.class);

      // Navigate the Gemini response JSON structure
      if (response != null && response.containsKey("candidates")) {
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        String sentiment = (String) parts.get(0).get("text");

        return sentiment.trim().toUpperCase();
      }
    } catch (Exception e) {
      log.error("AI Sentiment Analysis failed: {}", e.getMessage());
    }
    return "UNKNOWN";
  }
}