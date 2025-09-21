package com.tripgg.schedule.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripgg.schedule.dto.AiScheduleRequest;
import com.tripgg.schedule.dto.AiScheduleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public AiScheduleResponse generateSchedule(AiScheduleRequest request) {
        try {
            String prompt = buildPrompt(request);

            Map<String, Object> geminiRequest = new HashMap<>();
            geminiRequest.put("contents", List.of(
                    Map.of("role", "user", "parts", List.of(Map.of("text", prompt)))
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(geminiRequest, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");

                if (candidates != null && !candidates.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    String text = (String) parts.get(0).get("text");

                    // JSON 파싱
                    return objectMapper.readValue(text, AiScheduleResponse.class);
                }
            }

            throw new RuntimeException("Gemini API 응답을 처리할 수 없습니다.");

        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생", e);
            throw new RuntimeException("AI 일정 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String buildPrompt(AiScheduleRequest request) {
        // GptApiService의 buildPrompt와 동일하게 구현
        // 필요시 Gemini에 맞게 프롬프트 조정
        // 예시로 GptApiService의 메서드를 복사해서 사용하거나, 공통 유틸로 분리 가능
        // 아래는 간단히 GptApiService의 프롬프트 생성 로직을 호출한다고 가정
        return new GptApiService(restTemplate, objectMapper).buildPrompt(request);
    }
}