package org.example;

/**
 * Consumer 실행 애플리케이션
 * click-logs 토픽을 구독하여 메시지를 실시간으로 수신합니다.
 * <br>
 * 실행 방법: ./gradlew runConsumer
 */
public class ConsumerApp {
    
    private static final String BOOTSTRAP_SERVERS = "localhost:9092"; // Kafka 브로커 주소
    private static final String GROUP_ID = "click-log-consumer-group"; // Consumer Group ID
    
    public static void main(String[] args) {
        ClickLogConsumer consumer = new ClickLogConsumer(BOOTSTRAP_SERVERS, GROUP_ID);
        
        // Graceful Shutdown: Ctrl+C -> Consumer 안전하게 종료
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutdown signal received...");
            consumer.stop();
            System.out.println("Total messages processed: " + consumer.getMessageCount());
            System.out.println("Consumer application stopped.");
        }));
        
        try {
            consumer.startConsuming();
        } catch (Exception e) {
            System.err.println("Error in consumer application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}