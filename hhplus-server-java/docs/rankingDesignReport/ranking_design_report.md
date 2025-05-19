# 📊 Ranking Design Report

## ✅ 개요

기존의 **"최근 3일 상위 상품 조회"** 기능은 1시간마다 스케줄러가 DB에 스냅샷 데이터를 저장하고, 이 스냅샷 데이터를 기반으로 랭킹을 조회하는 방식이었다.

하지만 **실시간으로 상품의 판매량 랭킹을 조회**하고자 할 경우, 매번 DB에 접근한다면 다음과 같은 문제가 발생할 수 있다:

- ❌ 리소스 낭비 및 DB 부하 증가
- ❌ 다른 기능(주문 생성, 취소 등)에 의해 락이 발생할 경우, 실시간성 보장 불가

따라서 위 문제를 해결하고자, **Redis를 활용한 실시간 랭킹 조회 기능**을 별도로 구현하게 되었다.

---

## 🚀 캐시 전략

### 🔸 사용한 전략

- **캐싱 시점**: 상품 판매량 변화가 발생하는 **주문 생성/취소 시점**
- **조회 시점**: 실시간 랭킹 조회 **API 요청 시**

### 🔸 캐시 키 구성

- **카테고리 단위로 랭킹 관리**
  - 예시: `zset:rank:TENT`, `zset:rank:ALL`
- **ZSet의 score 값**: 판매량
- **ZSet의 value 값**: 상품 ID

---

## 🧱 사용한 자료구조

### Redis Sorted Set (ZSet)

#### 🔹 선택 이유

- ✅ **자동 정렬 기능 제공** → 별도 정렬 로직 없이 높은 판매량 순으로 정렬 가능
- ✅ **score 기반 데이터 관리** → 판매량 증감 시 `score` 조정만으로 랭킹 관리 가능

---

## 🔧 동작 방식

### 📌 주문 생성 시

판매된 상품 ID에 대해 `ZINCRBY` 명령을 통해 판매량(score)을 증가시킨다.

```java
redisTemplate.opsForZSet().incrementScore(key, productId.toString(), quantity);
```

---

### 📌 주문 취소 시

취소된 상품 ID에 대해 `ZINCRBY` 명령으로 판매량(score)을 감소시킨다.

```java
redisTemplate.opsForZSet().incrementScore(key, productId.toString(), -quantity);
```

---

### 📌 실시간 랭킹 조회 API

ZSet에서 score 기준으로 내림차순 정렬된 데이터를 조회하고, 상위 N개를 추출한다.

```java
Set<ZSetOperations.TypedTuple<Object>> rank = 
    redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, TOP_N - 1);
```

- 조회 결과는 상품 ID와 판매량(score) 쌍으로 반환된다.

---

## 📌 결론

Redis 기반 실시간 랭킹 조회 기능은 다음과 같은 **명확한 장점**을 가진다:

- ✅ DB 접근 최소화로 성능 향상
- ✅ 락 회피 가능 (비관락 시스템에 독립적)
- ✅ 높은 응답 속도
- ✅ 판매량 기반 정확한 실시간 랭킹 반영

---

## 🔮 향후 활용 가능성

이 구조는 다음과 같은 방향으로 확장 및 응용될 수 있다:

- 🔸 **주문량이 많은 서비스에서 인기 상품 실시간 노출**
- 🔸 **카테고리별 트렌드 분석 시스템** 구축
- 🔸 **실시간 모니터링 및 리더보드 시스템** 구현
