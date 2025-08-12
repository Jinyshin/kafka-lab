package org.example;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Producer 실행 애플리케이션
 * 사용자 입력을 받아 클릭 로그 메시지를 Kafka로 전송합니다.
 * <br>
 * 실행 방법: ./gradlew runProducer
 */
public class ProducerApp {
    
    private static final String BOOTSTRAP_SERVERS = "localhost:9092"; // Kafka 브로커 주소
    
    public static void main(String[] args) {
        ClickLogProducer producer = new ClickLogProducer(BOOTSTRAP_SERVERS);
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Kafka Click Log Producer ===");
        System.out.println("Enter click log data (type 'exit' to quit)");
        System.out.println("Format: userId,page,element");
        System.out.println("Example: user123,homepage,login-button");
        System.out.println();
        
        try {
            // 시작 시 샘플 데이터 자동 전송으로 Kafka 연결 테스트
            sendSampleData(producer);
            
            // Interactive input
            while (true) {
                System.out.print("Enter click log data: ");
                String input = scanner.nextLine().trim();
                
                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }
                
                if (input.isEmpty()) {
                    System.out.println("Please enter valid data or 'exit' to quit");
                    continue;
                }
                
                try {
                    String[] parts = input.split(",");
                    if (parts.length != 3) {
                        System.out.println("Invalid format. Use: userId,page,element");
                        continue;
                    }
                    
                    ClickLog clickLog = new ClickLog(
                        parts[0].trim(),
                        parts[1].trim(), 
                        parts[2].trim(),
                        LocalDateTime.now()
                    );
                    
                    boolean success = producer.sendClickLog(clickLog);
                    if (success) {
                        System.out.println("✅ Click log sent successfully!");
                    } else {
                        System.out.println("❌ Failed to send click log");
                    }
                    
                } catch (Exception e) {
                    System.out.println("❌ Error processing input: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error in producer application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            producer.close();
            scanner.close();
            System.out.println("Producer application closed");
        }
    }
    
    private static void sendSampleData(ClickLogProducer producer) {
        System.out.println("Sending sample click logs...");
        
        ClickLog[] sampleLogs = {
            new ClickLog("user001", "homepage", "login-button", LocalDateTime.now()),
            new ClickLog("user002", "product-page", "add-to-cart", LocalDateTime.now()),
            new ClickLog("user001", "cart-page", "checkout-button", LocalDateTime.now()),
            new ClickLog("user003", "homepage", "search-box", LocalDateTime.now()),
            new ClickLog("user002", "checkout-page", "payment-button", LocalDateTime.now())
        };
        
        int successCount = 0;
        for (ClickLog log : sampleLogs) {
            boolean success = producer.sendClickLog(log);
            if (success) {
                successCount++;
            }
            try {
                Thread.sleep(100); // Small delay between messages
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("Sample data sent: " + successCount + "/" + sampleLogs.length + " messages");
        System.out.println();
    }
}