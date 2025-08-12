package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Kafka Consumer 구현체
 * click-logs 토픽에서 메시지를 수신하고 처리합니다.
 * <br>
 * - Consumer: 토픽에서 메시지를 소비하는 클라이언트
 * - Consumer Group: 여러 Consumer가 협력하여 메시지를 분산 처리
 * - 파티션 분배: Consumer Group 내에서 파티션을 Consumer들에게 분배
 * - 오프셋: 각 파티션에서 Consumer가 읽은 위치
 */
public class ClickLogConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(ClickLogConsumer.class);
    private static final String TOPIC_NAME = "click-logs"; // 구독할 토픽명
    
    private final KafkaConsumer<String, String> consumer; // Kafka Consumer 인스턴스
    private final ObjectMapper objectMapper; // JSON 역직렬화용
    private final AtomicInteger messageCount = new AtomicInteger(0); // 처리된 메시지 수 카운트
    private volatile boolean running = false;
    
    public ClickLogConsumer(String bootstrapServers, String groupId) {
        this.consumer = createConsumer(bootstrapServers, groupId);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    // Kafka Consumer 설정 및 생성
    private KafkaConsumer<String, String> createConsumer(String bootstrapServers, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // Kafka 브로커 주소
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); // Consumer Group ID
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // 키 역직렬화
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // 값 역직렬화
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 오프셋이 없을 때 처음부터 읽기
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        
        return new KafkaConsumer<>(props);
    }
    
    // 메시지 소비 시작 - Kafka의 핵심 Consumer 로직
    public void startConsuming() {
        consumer.subscribe(Collections.singletonList(TOPIC_NAME)); // 토픽 구독
        running = true;
        
        logger.info("Starting to consume messages from topic: {}", TOPIC_NAME);
        System.out.println("=== Kafka Click Log Consumer Started ===");
        System.out.println("Listening for messages... (Press Ctrl+C to stop)");
        System.out.println();
        
        try {
            while (running) {
                // Kafka에서 메시지를 폴링(가져오기) - 1초 타임아웃
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                // 받은 메시지들을 하나씩 처리
                for (ConsumerRecord<String, String> record : records) {
                    processMessage(record);
                }
                
                // 메시지 처리 완료 시 오프셋 커밋 (처리했다고 표시)
                if (records.count() > 0) {
                    consumer.commitSync();
                }
            }
        } catch (Exception e) {
            logger.error("Error while consuming messages: {}", e.getMessage(), e);
        } finally {
            consumer.close();
            logger.info("Consumer closed");
        }
    }
    
    // 개별 메시지 처리 - JSON을 객체로 변환하고 로그 출력
    private void processMessage(ConsumerRecord<String, String> record) {
        try {
            ClickLog clickLog = objectMapper.readValue(record.value(), ClickLog.class); // JSON → 객체 역직렬화
            int count = messageCount.incrementAndGet();
            
            System.out.println("📨 Message #" + count + " received:");
            System.out.println("   Key: " + record.key());
            System.out.println("   Partition: " + record.partition());
            System.out.println("   Offset: " + record.offset());
            System.out.println("   Click Log: " + clickLog);
            System.out.println("   Raw JSON: " + record.value());
            System.out.println("   ---");
            
            logger.info("Processed message #{} - User: {}, Page: {}, Element: {}", 
                       count, clickLog.getUserId(), clickLog.getPage(), clickLog.getElement());
            
        } catch (Exception e) {
            logger.error("Failed to process message: {}", e.getMessage(), e);
            System.out.println("❌ Failed to parse message: " + record.value());
            System.out.println("   Error: " + e.getMessage());
            System.out.println("   ---");
        }
    }
    
    public void stop() {
        running = false;
        logger.info("Stop signal received");
    }
    
    public int getMessageCount() {
        return messageCount.get();
    }
}