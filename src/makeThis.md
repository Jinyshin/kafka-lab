아래는 Kafka 클릭 로그 처리 실습 프로젝트(Java, Gradle 기반)를 1주일 내에 완료할 수 있도록 결과를 우선시하고 구조를 가볍게 가져가는 방향으로 구체화한 세부 작업 리스트(Task List)입니다. 주니어 개발자들이 개별적으로 맡아 구현할 수 있도록 최대한 상세히 나누었으며, 대중적이고 안정적인 라이브러리를 사용하고, 우선순위 높은 항목부터 진행할 수 있도록 구성했습니다.


Kafka 클릭 로그 처리 실습 프로젝트 (Java, Gradle) 세부 작업 리스트
1. 환경 및 빌드 설정 (우선순위: 매우 높음)
   1.1 Gradle 프로젝트 생성 및 기본 구조 설정
   Java 17 버전 명시
   주요 의존성 추가 (org.apache.kafka:kafka-clients 최신 안정버전)
   JSON 처리를 위한 라이브러리 추가 (Jackson 또는 Gson 중 선택, 예: com.fasterxml.jackson.core:jackson-databind)
   실행 스크립트 및 테스트용 샘플 메시지 포함 설정
   1.2 개발 및 실행 환경 문서화
   JDK 설치 및 버전 확인 방법
   Kafka 로컬 또는 Docker 클러스터 실행 방법 (간단한 Docker Compose 예제 포함)
   Gradle 빌드 및 실행 명령 문서화
2. Kafka 토픽 관리 및 설정 (우선순위: 높음)
   2.1 Kafka 토픽 click-logs 생성 자동화 스크립트 작성 (bash 또는 bat)
   파티션 수 1~2, 복제 팩터 1로 설정
   간단한 토픽 생성 예외 처리 문서화
   2.2 토픽 권한 및 접근 제어 기본 설정 (실습 용이하게 최소화)
   접근 권한 간략 설명 및 가능하면 기본값 유지
3. Producer 구현 (우선순위: 매우 높음)
   3.1 클릭 로그 데이터 모델 정의 (Java 클래스)
   필드: userId, page, element, timestamp (ISO8601 UTC)
   3.2 JSON 직렬화 기능 구현 (Jackson 또는 Gson 활용)
   객체→JSON 변환 메서드
   3.3 Kafka 프로듀서 생성 및 설정 (kafka-clients API 사용)
   최소 설정(bootstrap.servers, key.serializer, value.serializer 등)
   메시지 전송 메서드 구현 (click-logs 토픽 대상)
   3.4 메시지 유효성 검사
   필수 필드 체크 및 유효하지 않은 메시지 로깅 후 전송 차단
   3.5 메시지 전송 성공/실패 로그 출력 및 간단한 예외 처리
   3.6 테스트용 Producer 샘플 애플리케이션 작성
   하드코딩 또는 간단한 입력값으로 클릭 로그 전송
4. Consumer 구현 (우선순위: 매우 높음)
   4.1 Kafka 컨슈머 생성 및 설정 (kafka-clients API 사용)
   최소 설정 (bootstrap.servers, group.id, key.deserializer, value.deserializer 등)
   4.2 메시지 수신 및 JSON 파싱 구현
   JSON → Java 객체 변환
   4.3 수신 메시지 콘솔 출력 기능 구현 (읽기 쉬운 포맷)
   4.4 메시지 처리 상태 기본 모니터링 (예: 소비된 메시지 수 로그)
   4.5 예외 상황(파싱 실패 등) 처리 및 로그 기록
   4.6 기본 재시도/스킵 로직 (커밋 전략 간단 구현)
   4.7 테스트용 Consumer 애플리케이션 작성