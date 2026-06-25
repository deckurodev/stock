# 재고 관리 시스템

상품 재고 조회 / 입고 / 출고 API. 입·출고 동시성 제어 포함.

- Java 21 / Spring Boot, Gradle
- PostgreSQL 16, JPA(커맨드) + jOOQ(쿼리)
- 스키마는 `db/schema.sql`이 소유 (`ddl-auto=none`), 상품/재고 테이블 분리(1:1)

## 테스트

```bash
./gradlew test
```

- Docker 데몬만 있으면 됨. 통합/동시성 테스트는 Testcontainers가 `postgres:16`을 자동 기동·종료한다(compose 불필요).
- 초기 데이터(`db/data.sql`)는 테스트에선 적재되지 않는다.

동시성 테스트: `StockConcurrencyTest`(입고/출고 각각), `StockInboundOutboundConcurrencyTest`(입·출고 혼합).

## 실행

```bash
docker compose up -d --build   # postgres + 앱 함께 기동 (앱 :8080)
docker compose down            # 중지 (데이터 유지, -v 로 초기화)
```

기동 시 `db/schema.sql`·`db/data.sql` 자동 실행. data.sql은 `ON CONFLICT`로 멱등(재기동 안전).
초기 상품: 아메리카노(100), 카페라떼(50), 콜드브루(0).

## API

| 기능 | Method / Path | Request | Response |
|------|---------------|---------|----------|
| 입고 | `POST /api/v1/stocks/inbound` | `{ "name": "아메리카노", "quantity": 10 }` | `{ id, name, quantity }` |
| 출고 | `POST /api/v1/stocks/outbound` | `{ "productId": 1, "quantity": 5 }` | `{ id, name, quantity }` |
| 단건 조회 | `GET /api/v1/products/{id}` | - | `{ id, name, quantity }` |
| 목록 조회 | `GET /api/v1/products` | - | `{ count, products: [...] }` |

- 입고는 상품명 기준, 미등록이면 신규 등록 후 입고. 출고는 상품ID 기준.
- 재고 부족 409, 미등록 상품 404, 잘못된 요청 400.
# stock
