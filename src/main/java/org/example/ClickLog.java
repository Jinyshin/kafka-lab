package org.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * 클릭 로그 데이터 모델
 * Kafka 메시지로 전송될 데이터를 정의합니다.
 * <br>
 * - 메시지 직렬화/역직렬화: Jackson을 사용하여 객체 <-> JSON 변환
 * - 메시지 키: userId를 키로 사용하여 같은 사용자의 메시지가 같은 파티션으로 전송
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Consumer에서 알 수 없는 JSON 필드 무시
public class ClickLog {
    
    @JsonProperty("userId") // JSON 필드명 지정
    private String userId;  // 메시지 키로 사용됨 - 파티션 분배의 기준
    
    @JsonProperty("page")
    private String page;
    
    @JsonProperty("element")
    private String element;
    
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") // ISO-8601 UTC 형식
    private LocalDateTime timestamp;

    public ClickLog() {}

    public ClickLog(String userId, String page, String element, LocalDateTime timestamp) {
        this.userId = userId;
        this.page = page;
        this.element = element;
        this.timestamp = timestamp;
    }
    
    public String getUserId() {
        return userId;
    }

    public String getPage() {
        return page;
    }

    public String getElement() {
        return element;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Producer에서 메시지 전송 전 데이터 유효성 검사
    public boolean isValid() {
        return userId != null && !userId.trim().isEmpty() &&
               page != null && !page.trim().isEmpty() &&
               element != null && !element.trim().isEmpty() &&
               timestamp != null;
    }
    
    @Override
    public String toString() {
        return String.format("ClickLog{userId='%s', page='%s', element='%s', timestamp=%s}", 
                           userId, page, element, timestamp);
    }
}