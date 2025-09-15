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
                Map.of("role", "system", "content", buildSystemPrompt(request.getLanguage())),
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
        String language = request.getLanguage();
        
        // 언어별 프롬프트 시작
        String promptStart = switch (language) {
            case "ko" -> "다음 조건에 맞는 여행 일정을 생성해주세요:\n\n";
            case "en" -> "Please create a travel itinerary that meets the following conditions:\n\n";
            case "ja" -> "以下の条件に合う旅行スケジュールを作成してください:\n\n";
            case "zh-CN" -> "请根据以下条件创建旅游行程:\n\n";
            case "zh-TW" -> "請根據以下條件創建旅遊行程:\n\n";
            case "vi" -> "Vui lòng tạo lịch trình du lịch phù hợp với các điều kiện sau:\n\n";
            default -> "다음 조건에 맞는 여행 일정을 생성해주세요:\n\n";
        };
        
        prompt.append(promptStart);
        prompt.append(getLocalizedLabel("title", language)).append(": ").append(request.getTitle()).append("\n");
        prompt.append(getLocalizedLabel("description", language)).append(": ").append(request.getDescription()).append("\n");
        prompt.append(getLocalizedLabel("period", language)).append(": ").append(request.getStartDate()).append(" ~ ").append(request.getEndDate()).append("\n");
        prompt.append(getLocalizedLabel("region", language)).append(": ").append(convertRegion(request.getRegion(), language)).append("\n");
        prompt.append(getLocalizedLabel("keywords", language)).append(": ").append(convertKeywords(request.getKeywords(), language)).append("\n");
        prompt.append(getLocalizedLabel("companion", language)).append(": ").append(convertCompanion(request.getCompanion(), language)).append("\n");
        prompt.append(getLocalizedLabel("transportation", language)).append(": ").append(convertTransportation(request.getTransportation(), language)).append("\n");
        prompt.append(getLocalizedLabel("travelStyle", language)).append(": ").append(convertTravelStyle(request.getTravelStyle(), language)).append("\n");
        prompt.append(getLocalizedLabel("language", language)).append(": ").append(convertLanguage(request.getLanguage())).append("\n\n");
        
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
        prompt.append("      \"memo\": \"조선왕조의 대표 궁궐 방문\",\n");
        prompt.append("      \"startDate\": \"").append(request.getStartDate()).append("\",\n");
        prompt.append("      \"startTime\": \"09:00:00\",\n");
        prompt.append("      \"endTime\": \"12:00:00\",\n");
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
        prompt.append("1. 반드시 실제 존재하는 상호명과 정확한 주소를 사용하세요 (예: '스타벅스 강남점', '맥도날드 명동점', '롯데월드타워')\n");
        prompt.append("2. 음식점, 카페, 쇼핑몰 등은 구체적인 상호명을 명시하세요\n");
        prompt.append("3. 관광지는 정확한 공식 명칭을 사용하세요 (예: '경복궁', '남산타워', '한강공원')\n");
        prompt.append("4. 카테고리는 반드시 다음 중 하나만 사용하세요: '카페', '관광', '식사', '기타', '쇼핑', '숙소'\n");
        prompt.append("5. 여행 일정답게 다양한 카테고리가 적절히 섞이도록 구성하세요 (관광-식사-카페-쇼핑 순서 등)\n");
        prompt.append("6. 키워드에 맞는 실제 장소를 선택하세요\n");
        prompt.append("7. 일정은 현실적이고 체계적으로 구성하세요\n");
        prompt.append("8. 각 장소마다 적절한 체류 시간을 설정하세요\n");
        prompt.append("9. JSON 형식을 정확히 지켜주세요\n");
        
        return prompt.toString();
    }
    
    // 언어별 시스템 프롬프트 생성
    private String buildSystemPrompt(String language) {
        return switch (language) {
            case "ko" -> "당신은 한국 여행 전문가입니다. 반드시 실제 존재하는 상호명과 정확한 주소를 사용하여 상세한 여행 일정을 JSON 형태로 생성해주세요. 음식점, 카페, 쇼핑몰은 구체적인 상호명을, 관광지는 공식 명칭을 사용하세요. 카테고리는 '카페', '관광', '식사', '기타', '쇼핑', '숙소' 중 하나만 사용하고, 여행 일정답게 다양한 카테고리가 적절히 섞이도록 구성하세요. 모든 응답은 한국어로 작성해주세요.";
            
            case "en" -> "You are a Korean travel expert. Create detailed travel itineraries in JSON format using only real existing business names and accurate addresses. Use specific business names for restaurants, cafes, and shopping malls, and official names for tourist attractions. Use only one category from '카페', '관광', '식사', '기타', '쇼핑', '숙소' and mix various categories appropriately for a travel itinerary. Write all responses in English.";
            
            case "ja" -> "あなたは韓国旅行の専門家です。実際に存在する店舗名と正確な住所を使用して、詳細な旅行スケジュールをJSON形式で作成してください。レストラン、カフェ、ショッピングモールには具体的な店舗名を、観光地には正式名称を使用してください。カテゴリは'카페', '관광', '식사', '기타', '쇼핑', '숙소'の中から1つだけ使用し、旅行スケジュールらしく様々なカテゴリが適切に混在するように構成してください。すべての回答は日本語で書いてください。";
            
            case "zh-CN" -> "您是韩国旅游专家。请使用真实存在的商家名称和准确地址，以JSON格式创建详细的旅游行程。餐厅、咖啡厅、购物中心使用具体的商家名称，旅游景点使用官方名称。类别只能使用'카페', '관광', '식사', '기타', '쇼핑', '숙소'中的一个，并适当混合各种类别以构成旅游行程。请用简体中文撰写所有回复。";
            
            case "zh-TW" -> "您是韓國旅遊專家。請使用真實存在的商家名稱和準確地址，以JSON格式創建詳細的旅遊行程。餐廳、咖啡廳、購物中心使用具體的商家名稱，旅遊景點使用官方名稱。類別只能使用'카페', '관광', '식사', '기타', '쇼핑', '숙소'中的一個，並適當混合各種類別以構成旅遊行程。請用繁體中文撰寫所有回覆。";
            
            case "vi" -> "Bạn là chuyên gia du lịch Hàn Quốc. Hãy tạo lịch trình du lịch chi tiết ở định dạng JSON bằng cách sử dụng tên cửa hàng thực tế và địa chỉ chính xác. Sử dụng tên cửa hàng cụ thể cho nhà hàng, quán cà phê và trung tâm mua sắm, và tên chính thức cho các điểm du lịch. Chỉ sử dụng một danh mục từ '카페', '관광', '식사', '기타', '쇼핑', '숙소' và kết hợp các danh mục khác nhau một cách phù hợp cho lịch trình du lịch. Viết tất cả phản hồi bằng tiếng Việt.";
            
            default -> "당신은 한국 여행 전문가입니다. 반드시 실제 존재하는 상호명과 정확한 주소를 사용하여 상세한 여행 일정을 JSON 형태로 생성해주세요. 음식점, 카페, 쇼핑몰은 구체적인 상호명을, 관광지는 공식 명칭을 사용하세요. 카테고리는 '카페', '관광', '식사', '기타', '쇼핑', '숙소' 중 하나만 사용하고, 여행 일정답게 다양한 카테고리가 적절히 섞이도록 구성하세요. 모든 응답은 한국어로 작성해주세요.";
        };
    }
    
    // 언어별 라벨 가져오기
    private String getLocalizedLabel(String key, String language) {
        return switch (key) {
            case "title" -> switch (language) {
                case "ko" -> "제목";
                case "en" -> "Title";
                case "ja" -> "タイトル";
                case "zh-CN" -> "标题";
                case "zh-TW" -> "標題";
                case "vi" -> "Tiêu đề";
                default -> "제목";
            };
            case "description" -> switch (language) {
                case "ko" -> "설명";
                case "en" -> "Description";
                case "ja" -> "説明";
                case "zh-CN" -> "描述";
                case "zh-TW" -> "描述";
                case "vi" -> "Mô tả";
                default -> "설명";
            };
            case "period" -> switch (language) {
                case "ko" -> "여행 기간";
                case "en" -> "Travel Period";
                case "ja" -> "旅行期間";
                case "zh-CN" -> "旅行期间";
                case "zh-TW" -> "旅行期間";
                case "vi" -> "Thời gian du lịch";
                default -> "여행 기간";
            };
            case "region" -> switch (language) {
                case "ko" -> "지역";
                case "en" -> "Region";
                case "ja" -> "地域";
                case "zh-CN" -> "地区";
                case "zh-TW" -> "地區";
                case "vi" -> "Khu vực";
                default -> "지역";
            };
            case "keywords" -> switch (language) {
                case "ko" -> "키워드";
                case "en" -> "Keywords";
                case "ja" -> "キーワード";
                case "zh-CN" -> "关键词";
                case "zh-TW" -> "關鍵詞";
                case "vi" -> "Từ khóa";
                default -> "키워드";
            };
            case "companion" -> switch (language) {
                case "ko" -> "동반자";
                case "en" -> "Companion";
                case "ja" -> "同行者";
                case "zh-CN" -> "同行者";
                case "zh-TW" -> "同行者";
                case "vi" -> "Người đồng hành";
                default -> "동반자";
            };
            case "transportation" -> switch (language) {
                case "ko" -> "교통수단";
                case "en" -> "Transportation";
                case "ja" -> "交通手段";
                case "zh-CN" -> "交通方式";
                case "zh-TW" -> "交通方式";
                case "vi" -> "Phương tiện";
                default -> "교통수단";
            };
            case "travelStyle" -> switch (language) {
                case "ko" -> "여행 스타일";
                case "en" -> "Travel Style";
                case "ja" -> "旅行スタイル";
                case "zh-CN" -> "旅行风格";
                case "zh-TW" -> "旅行風格";
                case "vi" -> "Phong cách du lịch";
                default -> "여행 스타일";
            };
            case "language" -> switch (language) {
                case "ko" -> "언어";
                case "en" -> "Language";
                case "ja" -> "言語";
                case "zh-CN" -> "语言";
                case "zh-TW" -> "語言";
                case "vi" -> "Ngôn ngữ";
                default -> "언어";
            };
            default -> key;
        };
    }
    
    // 프론트엔드 값들을 언어별로 변환하는 메서드들
    private String convertRegion(String region, String language) {
        return switch (region) {
            case "seoul-all" -> switch (language) {
                case "ko" -> "서울 전역";
                case "en" -> "All Seoul";
                case "ja" -> "ソウル全域";
                case "zh-CN" -> "首尔全域";
                case "zh-TW" -> "首爾全域";
                case "vi" -> "Toàn bộ Seoul";
                default -> "서울 전역";
            };
            case "seoul-gg" -> switch (language) {
                case "ko" -> "서울 강남구";
                case "en" -> "Gangnam, Seoul";
                case "ja" -> "ソウル江南区";
                case "zh-CN" -> "首尔江南区";
                case "zh-TW" -> "首爾江南區";
                case "vi" -> "Gangnam, Seoul";
                default -> "서울 강남구";
            };
            case "gg-popular" -> switch (language) {
                case "ko" -> "경기도 인기 지역";
                case "en" -> "Popular Gyeonggi Areas";
                case "ja" -> "京畿道人気地域";
                case "zh-CN" -> "京畿道热门地区";
                case "zh-TW" -> "京畿道熱門地區";
                case "vi" -> "Khu vực nổi tiếng Gyeonggi";
                default -> "경기도 인기 지역";
            };
            case "capital-area" -> switch (language) {
                case "ko" -> "수도권";
                case "en" -> "Capital Area";
                case "ja" -> "首都圏";
                case "zh-CN" -> "首都圈";
                case "zh-TW" -> "首都圈";
                case "vi" -> "Khu vực thủ đô";
                default -> "수도권";
            };
            default -> region;
        };
    }
    
    private String convertKeywords(List<String> keywords, String language) {
        return keywords.stream()
                .map(keyword -> convertKeyword(keyword, language))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }
    
    private String convertKeyword(String keyword, String language) {
        return switch (keyword) {
            case "kpop" -> switch (language) {
                case "ko" -> "K-POP";
                case "en" -> "K-POP";
                case "ja" -> "K-POP";
                case "zh-CN" -> "K-POP";
                case "zh-TW" -> "K-POP";
                case "vi" -> "K-POP";
                default -> "K-POP";
            };
            case "history" -> switch (language) {
                case "ko" -> "역사";
                case "en" -> "History";
                case "ja" -> "歴史";
                case "zh-CN" -> "历史";
                case "zh-TW" -> "歷史";
                case "vi" -> "Lịch sử";
                default -> "역사";
            };
            case "squidgame" -> switch (language) {
                case "ko" -> "오징어게임";
                case "en" -> "Squid Game";
                case "ja" -> "イカゲーム";
                case "zh-CN" -> "鱿鱼游戏";
                case "zh-TW" -> "魷魚遊戲";
                case "vi" -> "Trò chơi con mực";
                default -> "오징어게임";
            };
            case "kpopdemons" -> switch (language) {
                case "ko" -> "K-POP 데뷔";
                case "en" -> "K-POP Debut";
                case "ja" -> "K-POPデビュー";
                case "zh-CN" -> "K-POP出道";
                case "zh-TW" -> "K-POP出道";
                case "vi" -> "K-POP Debut";
                default -> "K-POP 데뷔";
            };
            case "koreanfood" -> switch (language) {
                case "ko" -> "한국음식";
                case "en" -> "Korean Food";
                case "ja" -> "韓国料理";
                case "zh-CN" -> "韩国料理";
                case "zh-TW" -> "韓國料理";
                case "vi" -> "Ẩm thực Hàn Quốc";
                default -> "한국음식";
            };
            case "koreanwave" -> switch (language) {
                case "ko" -> "한류";
                case "en" -> "Korean Wave";
                case "ja" -> "韓流";
                case "zh-CN" -> "韩流";
                case "zh-TW" -> "韓流";
                case "vi" -> "Làn sóng Hàn Quốc";
                default -> "한류";
            };
            case "koreanbeauty" -> switch (language) {
                case "ko" -> "K-뷰티";
                case "en" -> "K-Beauty";
                case "ja" -> "K-ビューティー";
                case "zh-CN" -> "K-美容";
                case "zh-TW" -> "K-美容";
                case "vi" -> "K-Beauty";
                default -> "K-뷰티";
            };
            case "koreandrama" -> switch (language) {
                case "ko" -> "한국드라마";
                case "en" -> "Korean Drama";
                case "ja" -> "韓国ドラマ";
                case "zh-CN" -> "韩剧";
                case "zh-TW" -> "韓劇";
                case "vi" -> "Phim truyền hình Hàn Quốc";
                default -> "한국드라마";
            };
            default -> keyword;
        };
    }
    
    private String convertCompanion(String companion, String language) {
        return switch (companion) {
            case "family" -> switch (language) {
                case "ko" -> "가족";
                case "en" -> "Family";
                case "ja" -> "家族";
                case "zh-CN" -> "家庭";
                case "zh-TW" -> "家庭";
                case "vi" -> "Gia đình";
                default -> "가족";
            };
            case "couple" -> switch (language) {
                case "ko" -> "연인";
                case "en" -> "Couple";
                case "ja" -> "カップル";
                case "zh-CN" -> "情侣";
                case "zh-TW" -> "情侶";
                case "vi" -> "Cặp đôi";
                default -> "연인";
            };
            case "alone" -> switch (language) {
                case "ko" -> "혼자";
                case "en" -> "Alone";
                case "ja" -> "一人";
                case "zh-CN" -> "独自";
                case "zh-TW" -> "獨自";
                case "vi" -> "Một mình";
                default -> "혼자";
            };
            default -> companion;
        };
    }
    
    private String convertTransportation(String transportation, String language) {
        return switch (transportation) {
            case "car" -> switch (language) {
                case "ko" -> "자동차";
                case "en" -> "Car";
                case "ja" -> "車";
                case "zh-CN" -> "汽车";
                case "zh-TW" -> "汽車";
                case "vi" -> "Ô tô";
                default -> "자동차";
            };
            case "public" -> switch (language) {
                case "ko" -> "대중교통";
                case "en" -> "Public Transportation";
                case "ja" -> "公共交通";
                case "zh-CN" -> "公共交通";
                case "zh-TW" -> "公共交通";
                case "vi" -> "Giao thông công cộng";
                default -> "대중교통";
            };
            case "walking" -> switch (language) {
                case "ko" -> "도보";
                case "en" -> "Walking";
                case "ja" -> "徒歩";
                case "zh-CN" -> "步行";
                case "zh-TW" -> "步行";
                case "vi" -> "Đi bộ";
                default -> "도보";
            };
            default -> transportation;
        };
    }
    
    private String convertTravelStyle(String travelStyle, String language) {
        return switch (travelStyle) {
            case "leaf" -> switch (language) {
                case "ko" -> "휴식";
                case "en" -> "Relaxation";
                case "ja" -> "休息";
                case "zh-CN" -> "休闲";
                case "zh-TW" -> "休閒";
                case "vi" -> "Thư giãn";
                default -> "휴식";
            };
            case "activity" -> switch (language) {
                case "ko" -> "액티비티";
                case "en" -> "Activity";
                case "ja" -> "アクティビティ";
                case "zh-CN" -> "活动";
                case "zh-TW" -> "活動";
                case "vi" -> "Hoạt động";
                default -> "액티비티";
            };
            case "pic" -> switch (language) {
                case "ko" -> "포토존";
                case "en" -> "Photo Spots";
                case "ja" -> "フォトスポット";
                case "zh-CN" -> "拍照点";
                case "zh-TW" -> "拍照點";
                case "vi" -> "Điểm chụp ảnh";
                default -> "포토존";
            };
            case "art" -> switch (language) {
                case "ko" -> "예술";
                case "en" -> "Art";
                case "ja" -> "芸術";
                case "zh-CN" -> "艺术";
                case "zh-TW" -> "藝術";
                case "vi" -> "Nghệ thuật";
                default -> "예술";
            };
            case "eat" -> switch (language) {
                case "ko" -> "맛집";
                case "en" -> "Food";
                case "ja" -> "グルメ";
                case "zh-CN" -> "美食";
                case "zh-TW" -> "美食";
                case "vi" -> "Ẩm thực";
                default -> "맛집";
            };
            case "emotion" -> switch (language) {
                case "ko" -> "감성";
                case "en" -> "Emotional";
                case "ja" -> "感性";
                case "zh-CN" -> "感性";
                case "zh-TW" -> "感性";
                case "vi" -> "Cảm xúc";
                default -> "감성";
            };
            default -> travelStyle;
        };
    }
    
    private String convertLanguage(String language) {
        return switch (language) {
            case "ko" -> "한국어";
            case "en" -> "English";
            case "ja" -> "日本語";
            case "zh-CN" -> "中文(简体)";
            case "zh-TW" -> "中文(繁體)";
            case "vi" -> "Tiếng Việt";
            default -> language;
        };
    }
}
