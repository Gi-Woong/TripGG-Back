package com.tripgg.schedule.service;

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
public class GptApiService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${gpt.api.key}")
    private String apiKey;
    
    @Value("${gpt.api.url}")
    private String apiUrl;
    
    public AiScheduleResponse generateSchedule(AiScheduleRequest request) {
        try {
            String prompt = buildPrompt(request);
            
            Map<String, Object> gptRequest = new HashMap<>();
            gptRequest.put("model", "gpt-3.5-turbo");
            gptRequest.put("messages", List.of(
                Map.of("role", "system", "content", "당신은 한국 여행 전문가입니다. 주어진 조건에 맞는 상세한 여행 일정을 JSON 형태로 생성해주세요."),
                Map.of("role", "user", "content", prompt)
            ));
            gptRequest.put("max_tokens", 2000);
            gptRequest.put("temperature", 0.7);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(gptRequest, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                
                if (choices != null && !choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    
                    // JSON 파싱
                    return objectMapper.readValue(content, AiScheduleResponse.class);
                }
            }
            
            throw new RuntimeException("GPT API 응답을 처리할 수 없습니다.");
            
        } catch (Exception e) {
            log.error("GPT API 호출 중 오류 발생", e);
            throw new RuntimeException("AI 일정 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    private String buildPrompt(AiScheduleRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("다음 조건에 맞는 여행 일정을 생성해주세요:\n\n");
        prompt.append("제목: ").append(request.getTitle()).append("\n");
        prompt.append("설명: ").append(request.getDescription()).append("\n");
        prompt.append("여행 기간: ").append(request.getStartDate()).append(" ~ ").append(request.getEndDate()).append("\n");
        prompt.append("지역: ").append(request.getRegion()).append("\n");
        prompt.append("키워드: ").append(String.join(", ", request.getKeywords())).append("\n");
        prompt.append("동반자: ").append(request.getCompanion()).append("\n");
        prompt.append("교통수단: ").append(request.getTransportation()).append("\n");
        prompt.append("언어: ").append(request.getLanguage()).append("\n\n");
        
        prompt.append("응답은 반드시 다음 JSON 형식으로만 제공해주세요:\n");
        prompt.append("{\n");
        prompt.append("  \"schedule\": {\n");
        prompt.append("    \"id\": 456,\n");
        prompt.append("    \"userId\": ").append(request.getUserId()).append(",\n");
        prompt.append("    \"title\": \"").append(request.getTitle()).append("\",\n");
        prompt.append("    \"description\": \"").append(request.getDescription()).append("\",\n");
        prompt.append("    \"isAiGenerated\": true,\n");
        prompt.append("    \"startDate\": \"").append(request.getStartDate()).append("\",\n");
        prompt.append("    \"endDate\": \"").append(request.getEndDate()).append("\",\n");
        prompt.append("    \"createdAt\": \"").append(java.time.LocalDateTime.now()).append("\"\n");
        prompt.append("  },\n");
        prompt.append("  \"scheduleItems\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"id\": 1001,\n");
        prompt.append("      \"scheduleId\": 456,\n");
        prompt.append("      \"placeId\": 2001,\n");
        prompt.append("      \"day\": 1,\n");
        prompt.append("      \"orderInDay\": 1,\n");
        prompt.append("      \"memo\": \"조선왕조의 대표 궁궐\",\n");
        prompt.append("      \"startDate\": \"").append(request.getStartDate()).append("\",\n");
        prompt.append("      \"startTime\": \"09:00:00\",\n");
        prompt.append("      \"endTime\": \"10:00:00\",\n");
        prompt.append("      \"place\": {\n");
        prompt.append("        \"id\": 2001,\n");
        prompt.append("        \"name\": \"경복궁\",\n");
        prompt.append("        \"category\": \"관광\",\n");
        prompt.append("        \"description\": \"조선왕조의 대표 궁궐\",\n");
        prompt.append("        \"address\": \"서울특별시 종로구 사직로 161\",\n");
        prompt.append("        \"latitude\": 37.5796,\n");
        prompt.append("        \"longitude\": 126.9770\n");
        prompt.append("      }\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n\n");
        
        prompt.append("주의사항:\n");
        prompt.append("1. 실제 존재하는 장소명과 주소를 사용하세요\n");
        prompt.append("2. 키워드에 맞는 장소를 선택하세요\n");
        prompt.append("3. 일정은 현실적이고 체계적으로 구성하세요\n");
        prompt.append("4. 각 장소마다 적절한 체류 시간을 설정하세요\n");
        prompt.append("5. JSON 형식을 정확히 지켜주세요\n");
        
        return prompt.toString();
    }
}
