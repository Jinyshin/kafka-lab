package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Kafka Producer 구현체
 * 클릭 로그 데이터를 Kafka 토픽으로 전송합니다.
 * <br>
 * - Producer: 메시지를 Kafka 토픽에 발행하는 클라이언트
 * - 토픽: 메시지가 저장되는 논리적 채널 (여기서는 "click-logs")
 * - 파티셔닝: userId를 키로 사용하여 같은 사용자의 메시지를 같은 파티션에 저장
 */
public class ClickLogProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(ClickLogProducer.class);
    private static final String TOPIC_NAME = "click-logs"; // 메시지를 발행할 토픽명
    
    private final KafkaProducer<String, String> producer; // Kafka Producer 인스턴스
    private final ObjectMapper objectMapper; // JSON 직렬화용
    
    public ClickLogProducer(String bootstrapServers) {
        this.producer = createProducer(bootstrapServers);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    // Kafka Producer 설정 및 생성
    private KafkaProducer<String, String> createProducer(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // Kafka 브로커 주소
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // 키 직렬화
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // 값 직렬화
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // 모든 브로커 확인 후 응답
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // 실패 시 재시도 횟수
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        
        return new KafkaProducer<>(props);
    }
    
    // 클릭 로그 메시지를 Kafka 토픽으로 전송
    public boolean sendClickLog(ClickLog clickLog) {
        if (!clickLog.isValid()) {
            logger.warn("Invalid click log data: {}", clickLog);
            return false;
        }
        
        try {
            String jsonValue = objectMapper.writeValueAsString(clickLog); // 객체 → JSON 직렬화
            String key = clickLog.getUserId(); // 메시지 키 = userId (파티션 분배 기준)
            
            // ProducerRecord: Kafka로 전송할 메시지 (토픽, 키, 값)
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, key, jsonValue);
            
            // 메시지 전송 (비동기) + 콜백으로 결과 처리
            Future<RecordMetadata> future = producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.error("Failed to send message: {}", exception.getMessage(), exception);
                } else {
                    // 전송 성공: 토픽, 파티션, 오프셋 정보 로그
                    logger.info("Message sent successfully - Topic: {}, Partition: {}, Offset: {}", 
                              metadata.topic(), metadata.partition(), metadata.offset());
                }
            });

            RecordMetadata metadata = future.get();
            return true;
            
        } catch (Exception e) {
            logger.error("Error sending click log: {}", e.getMessage(), e);
            return false;
        }
    }
    
    public void close() {
        if (producer != null) {
            producer.close();
            logger.info("Producer closed successfully");
        }
    }
}