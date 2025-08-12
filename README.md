# Kafka 클릭 로그 처리 실습 프로젝트

가상의 클릭 로그 데이터를 Kafka를 통해 생산(Producer)하고 소비(Consumer)하는 기본적인 메시지 처리 시스템을 구현합니다.

## 🏗️ 프로젝트 구조

```
kafka-lab/
├── src/main/java/org/example/
│   ├── ClickLog.java           # 클릭 로그 데이터 모델
│   ├── ClickLogProducer.java   # Kafka Producer 구현체
│   ├── ClickLogConsumer.java   # Kafka Consumer 구현체
│   ├── ProducerApp.java        # Producer 실행 애플리케이션
│   └── ConsumerApp.java        # Consumer 실행 애플리케이션
├── scripts/
│   └── create-topic.sh         # 토픽 생성 스크립트
├── docker-compose.yml          # Kafka 로컬 환경 설정
├── build.gradle.kts            # 프로젝트 빌드 설정
└── README.md
```

## 📋 사전 요구사항

- Java 17 이상
- Docker Desktop

## 🚀 실행 방법

### 1. Kafka 환경 구성

먼저 Docker를 사용해 Kafka 클러스터를 시작합니다:

```bash
# Kafka 클러스터 시작 (백그라운드 실행)
docker-compose up -d

# 컨테이너 상태 확인
docker-compose ps
```

### 2. Kafka 토픽 생성

```bash
# 토픽 생성 스크립트 실행
./scripts/create-topic.sh
```

### 3. 애플리케이션 빌드

```bash
# Gradle 빌드
./gradlew build
```

### 4. Producer 실행

새 터미널에서 Producer를 실행합니다:

```bash
# Producer 애플리케이션 실행
./gradlew runProducer
```

Producer가 시작되면:
1. 자동으로 샘플 데이터 5개가 전송됩니다
2. 이후 직접 데이터를 입력할 수 있습니다
3. 형식: `userId,page,element` (예: `user123,homepage,login-button`)
4. `exit` 입력으로 종료합니다

### 5. Consumer 실행

다른 터미널에서 Consumer를 실행합니다:

```bash
# Consumer 애플리케이션 실행
./gradlew runConsumer
```

Consumer는 토픽의 메시지를 실시간으로 수신하여 콘솔에 출력합니다.

## 📊 Kafka UI 접속

웹 브라우저에서 `http://localhost:8080`에 접속하면 Kafka UI를 통해 토픽, 메시지, Consumer 그룹 등을 시각적으로 모니터링할 수 있습니다.

## 🧪 실습 시나리오

### 기본 실습
1. Producer를 실행하여 샘플 데이터가 전송되는 것을 확인
2. Consumer를 실행하여 메시지가 수신되는 것을 확인
3. Producer에서 직접 클릭 로그를 입력해보기

### 추가 실습 아이디어
1. **여러 Consumer 실행**: 같은 Consumer Group으로 여러 Consumer를 실행하여 파티션 분산 처리 확인
2. **다른 Consumer Group**: 다른 그룹 ID로 Consumer를 실행하여 메시지 중복 수신 확인
3. **대량 데이터 테스트**: Producer에서 많은 양의 데이터를 빠르게 전송해보기

## 📝 학습 포인트

### Kafka 핵심 개념
- **Topic**: 메시지가 저장되는 논리적 채널 (`click-logs`)
- **Producer**: 메시지를 토픽에 발행하는 애플리케이션
- **Consumer**: 토픽에서 메시지를 소비하는 애플리케이션
- **Consumer Group**: 여러 Consumer가 협력하여 메시지를 처리하는 그룹
- **Partition**: 토픽을 물리적으로 분할한 단위 (확장성 제공)

### 데이터 직렬화/역직렬화
- JSON 형태로 데이터 전송
- Jackson 라이브러리를 사용한 객체-JSON 변환
- 메시지 키(userId)를 통한 파티션 분배

### 예외 처리 및 모니터링
- 잘못된 메시지 형식 처리
- 전송/수신 성공/실패 로그
- 처리된 메시지 수 추적

## ✅ 실습 과제

### 과제 1: 기본 메시지 송수신
```
1. Producer를 실행하고 다음 3개의 클릭 로그를 전송하세요:
   - user001,homepage,login-button
   - user001,product-page,buy-button  
   - user001,cart-page,checkout-button

2. Consumer 로그에서 전송한 메시지 3개를 찾아 화면 캡쳐 후 첨부해 주세요
```

### 과제 2: 다중 Consumer 실험
```
1. 터미널 2개를 열어서 Consumer를 2개 동시에 실행하세요:
   - 터미널 1: ./gradlew runConsumer
   - 터미널 2: ./gradlew runConsumer

2. 다른 터미널의 Producer에서 메시지 5개를 전송하세요:
   - user002,homepage,banner-click
   - user003,search-page,keyword-input
   - user002,product-page,image-zoom
   - user003,cart-page,quantity-change
   - user002,payment-page,confirm-button

3. 각 Consumer 터미널에서 "📨 Message #X received:" 로그를 확인하고 기록하세요:
   - Consumer 1이 받은 메시지의 userId들: ____________
   - Consumer 2가 받은 메시지의 userId들: ____________
   - 같은 userId의 메시지가 같은 Consumer로 갔는지 확인하세요
```

### 과제 3: Kafka UI를 통한 모니터링
```
1. http://localhost:8080에 접속하여 다음 정보를 확인하고 화면 캡쳐 후 첨부해 주세요:
   - click-logs 토픽의 파티션 개수: __개
   - 현재 저장된 총 메시지 개수: __개  
   - Consumer Group 이름: ______________

2. Topics → click-logs → Messages에서 본인이 전송한 메시지를 찾아보세요

3. 마지막으로 전송된 메시지 내용 화면을 캡쳐 후 첨부해 주세요:
```

## 🛠️ 문제 해결

### Kafka 연결 실패
```bash
# Kafka 컨테이너 상태 확인
docker-compose ps

# Kafka 로그 확인
docker-compose logs kafka

# 컨테이너 재시작
docker-compose restart
```

### 토픽 관련 문제
```bash
# 토픽 목록 확인
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# 토픽 상세 정보 확인
docker exec kafka kafka-topics --describe --topic click-logs --bootstrap-server localhost:9092
```

### 포트 충돌
기본 포트가 사용 중인 경우 `docker-compose.yml`에서 포트를 변경하세요:
- Kafka: 9092 → 9093
- Kafka UI: 8080 → 8081
- Zookeeper: 2181 → 2182

## 🧹 정리

실습 완료 후 Docker 컨테이너를 정리합니다:

```bash
# 컨테이너 및 네트워크 삭제
docker-compose down

# 볼륨까지 삭제 (데이터 완전 초기화)
docker-compose down -v
```

## 📚 추가 학습 자료

- [Apache Kafka 공식 문서](https://kafka.apache.org/documentation/)
- [Kafka Clients for Java](https://docs.confluent.io/kafka-clients/java/current/overview.html)
- [Spring for Apache Kafka](https://spring.io/projects/spring-kafka)