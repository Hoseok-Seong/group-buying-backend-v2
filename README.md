# group-buying-backend-v2 업데이트

* v2 업데이트 이유
  - v1 프로젝트 시 구현 실패 및 미흡했던 점 보완
  - 기존 AWS S3 Java SDK에서 Spring Cloud AWS로 구현(스프링 기반 모듈을 통합하여 AWS 서비스를 제공)
  - 리뷰 평점 기반 등급 시스템 추가
  - 클라이언트 기반 firestore 채팅에서 서버 기반 STOMP 채팅으로 구현

* v1 프로젝트
  - https://github.com/group-buying/group-buying-backend

<br>

## AWS S3

* AWS S3 적용 이유
  - 로컬 스토리지 용량의 한계를 보완하는 확장성
  - 백업 및 복원 문제 해결
  - 데이터 암호화를 통한 보안
  - 버전 관리 

* build.gradle 의존성 추가
  - implementation 'io.awspring.cloud:spring-cloud-aws-starter-s3:3.0.0'

* 파일 업로드 방식: Multipart File
  - 장점 1. 대용량 파일 전송을 작은 부분으로 나누어 전송이 가능
  - 장점 2. 이어받기 가능
  - 장점 3. 전체 파일 전송 시간을 단축

* 코드 구동 원리
  - CredentialsProviderAutoConfiguration 클래스가 application.yml에 등록된 access key와 secret key에 접근
  - Credential과 관련된 Provider 객체들을 생성하고, AWS 서비스에 접근
  - S3Operations 인터페이스를 사용하여 버킷 생성, 삭제, 객체 업로드, 다운로드 기능 구현

* Upload 코드
``` bash
@Transactional
    public ResponseEntity<?> upload(MultipartFile multipartFile, String key) throws IOException {
        if (!MediaType.IMAGE_PNG.toString().equals(multipartFile.getContentType()) &&
                !MediaType.IMAGE_JPEG.toString().equals(multipartFile.getContentType())) {
            return ResponseEntity.badRequest().body("사진 파일만 업로드 가능합니다");
        }

        try (InputStream is = multipartFile.getInputStream()) {
            s3Operations.upload(BUCKET, key, is,
                    ObjectMetadata.builder().contentType(multipartFile.getContentType()).build());
        }

        return ResponseEntity.accepted().build();
    }
```

* Download 코드
``` bash
@Transactional
    public ResponseEntity<?> download(@RequestParam String key) {
        S3Resource s3Resource = s3Operations.download(BUCKET, key);

        if (MediaType.IMAGE_PNG.toString().equals(s3Resource.contentType())) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(s3Resource);
        }

        if (MediaType.IMAGE_JPEG.toString().equals(s3Resource.contentType())) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(s3Resource);
        }

        return ResponseEntity.badRequest().body("사진 파일만 다운로드 가능합니다");
    }
```

* 기술 블로그
  - https://white-developer.tistory.com/75

<br>

## 리뷰 평점 기반 등급 시스템

* 비즈니스 로직
  - 리뷰 등록시 리뷰 등록한 유저, 공동구매 주최 유저 모두 포인트 1점 추가
  - 리뷰 등록시 0점-5점 평점 포인트는 공동구매 주최 유저에게 추가
  - 포인트 추가 로직 실행 시, 승급 로직 실행
  - 유저 포인트 기반으로 일정 포인트 초과 시 등급 업데이트

<br>

## STOMP 채팅 구현

* STOMP 프로토콜 적용 이유
  - Low-Level의 웹소켓의 단점 보완
  - Pub/Sub 기반으로 동작하여 그룹 채팅 구현 가능

* Pub/Sub
![pubsub](https://github.com/Hoseok-Seong/Spring-STOMP-RabbitMQ/assets/93416157/845b1550-32e8-400f-819e-451337166087)

* build.gradle 의존성 추가
  - implementation 'org.springframework.boot:spring-boot-starter-websocket'

* WebSocketConfig 코드
``` bash
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 테스트할 때만 setAllowedOrigins("*"); 허용
        registry.addEndpoint("/ws").setAllowedOrigins("*");
    }
}
```

* ChatController 코드
``` bash
@MessageMapping("/chatroom/{id}")
    public void sendMessage(@DestinationVariable("id") Long id, ChatMessageReq chatMessageReq) {
        simpMessageSendingOperations.convertAndSend
                ("/sub/chatroom/" + chatMessageReq.getChatroomId(), chatMessageReq);
        log.info("메세지 전송 성공");
        chatMessageService.saveMessage(chatMessageReq);
    }
```

* 기술 블로그
  - https://white-developer.tistory.com/78
  - https://white-developer.tistory.com/80

