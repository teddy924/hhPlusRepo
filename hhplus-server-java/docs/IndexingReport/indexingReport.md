# 📊 성능 분석 보고서: 인덱스 적용 전후 비교

## 1. 개요

본 보고서는 `account_history`와 `orders` 테이블에 대해 **조회 성능 이슈**를 분석하고, **인덱스 적용 전후의 성능 차이**를 `EXPLAIN` 결과를 바탕으로 비교합니다.

- **분석 대상 기능**
  1. 잔액 변동 이력 조회
  2. 주문 목록 조회

- **공통 조건**: 사용자 ID를 기준으로 조회

---

## 2. 테스트 환경

- **DB**: MySQL 8.0 (Testcontainers 기반)
- **데이터량**:
  - `account_history`: 약 52,000건
  - `orders`: 약 1,000,000건
- **도구**: IntelliJ Query Console, DBeaver (EXPLAIN 사용)
- **평가 방법**:
  - `EXPLAIN`을 통한 실행 계획 분석
  - 실제 실행 시간(쿼리 실행 콘솔 기준)

---

## 3. 잔액 변동 이력 조회

### ✅ 쿼리

```sql
SELECT id, account_id, amount, status, sys_cret_dt
FROM account_history
WHERE account_id = 1;
```

---

### 🟥 인덱스 적용 전 `EXPLAIN` 결과

| 항목          | 값         |
|---------------|-------------|
| select_type   | SIMPLE      |
| table         | ah1_0       |
| type          | ALL         |
| possible_keys | NULL        |
| key           | NULL        |
| rows          | 52,104      |
| Extra         | Using where |
| Execution Time | 약 680 ms  |

- **전체 테이블 스캔** 발생
- `account_id` 조건이 있지만 인덱스가 없어 모두 순차 탐색
- 대용량 시 병목 발생 가능

---

### ✅ 인덱스 적용 후 `EXPLAIN` 결과

| 항목          | 값                              |
|---------------|----------------------------------|
| select_type   | SIMPLE                           |
| table         | ah1_0                            |
| type          | ref                              |
| possible_keys | idx_account_history_account_id   |
| key           | idx_account_history_account_id   |
| key_len       | 8                                |
| ref           | const                            |
| rows          | 3                                |
| Extra         | NULL                             |
| Execution Time | 약 1.2 ms                        |

- `account_id` 인덱스를 통해 즉시 조회
- **스캔 건수 52,104 ➝ 3건**으로 대폭 감소

---

## 4. 주문 목록 조회

### ✅ 쿼리

```sql
SELECT * FROM orders WHERE user_id = 1;
```

---

### 🟥 인덱스 적용 전 `EXPLAIN` 결과

| 항목          | 값         |
|---------------|-------------|
| select_type   | SIMPLE      |
| table         | orders      |
| type          | ALL         |
| possible_keys | NULL        |
| key           | NULL        |
| rows          | 1000000     |
| Extra         | Using where |
| Execution Time | 약 1.6 sec |

- `user_id` 조건이 있으나 인덱스가 없어 전부 순회
- 데이터 양이 많을수록 속도 급격히 저하됨

---

### ✅ 인덱스 적용 후 `EXPLAIN` 결과

| 항목          | 값                     |
|---------------|-------------------------|
| select_type   | SIMPLE                  |
| table         | orders                  |
| type          | ref                     |
| possible_keys | idx_orders_user_id      |
| key           | idx_orders_user_id      |
| key_len       | 8                       |
| ref           | const                   |
| rows          | 1                       |
| Extra         | NULL                    |
| Execution Time | 약 2.3 ms              |

- `user_id` 인덱스 덕분에 특정 유저의 주문만 즉시 탐색 가능
- 전체 데이터량과 무관하게 일정 성능 유지

---

## 5. 인덱스 적용 SQL

```sql
CREATE INDEX idx_account_history_account_id ON account_history(account_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);
```

---

## 6. 성능 비교 요약

| 기능               | 인덱스 적용 전        | 인덱스 적용 후       | 성능 개선 |
|--------------------|------------------------|------------------------|------------|
| 잔액 변동 이력 조회 | FULL SCAN (52,104 rows, 680ms) | Index Lookup (3 rows, 1.2ms) | ✅ 매우 큼 |
| 주문 목록 조회     | FULL SCAN (1M rows, 1.6s)      | Index Lookup (1 row, 2.3ms) | ✅ 매우 큼 |

---

## 7. 인덱스 누락 시 영향도 및 리스크

인덱스가 누락되었을 때는 단순히 조회 속도 저하를 넘어서 아래와 같은 **실질적인 운영 이슈**로 이어질 수 있습니다.

### 📌 영향도

| 항목 | 영향 |
|------|------|
| ⏱ 성능 저하 | 단순 조회 쿼리도 수 초 이상 지연될 수 있음 (e.g., 1건 조회에 수만 건 순회) |
| 🧮 자원 낭비 | CPU, 메모리 사용량 급증 → 전체 서비스 응답 속도 저하 |
| 🚨 스케일링 비용 증가 | DB 부하 해결을 위해 불필요한 인프라 확장 필요 |
| 💥 트랜잭션 병목 | 잦은 FULL SCAN으로 인해 **잠금 경합(lock contention)** 발생 가능성 증가 |
| 🔁 사용자 불편 | 사용자 대기 시간 증가 → UX 저하, 장애로 인식될 수 있음 |

---

### 💡 실무 사례

- `account_id`, `user_id`는 **조인에는 사용되지 않지만, 조회에 자주 쓰이는 필드**로서 인덱스 없이 방치되는 경우가 많음
- 외래키(FK)는 `FOREIGN KEY` 제약만 두고 인덱스를 수동 생성하지 않아 **조인 시 전체 테이블 스캔** 발생

---

### ✅ 요약

- 단일 조건 필터라 하더라도 **인덱스가 없으면 DB 전체 스캔**을 유발
- 대량 데이터에서는 응답 지연뿐 아니라 **전체 서비스 성능 저하**로 이어질 수 있으므로,
- **설계 시점부터 인덱스 전략 수립이 필수**

---

## 8. 향후 개선 전략

- **불필요한 컬럼 조회 방지**
  - `SELECT *` 대신 필요한 컬럼만 선택
- **정렬 조건 최적화**
  - 예: `ORDER BY sys_cret_dt DESC` → 해당 컬럼에 인덱스 포함 필요
- **대용량 데이터는 배치 처리 또는 커서 방식 고려**
  - 페이지 단위 처리 및 비동기 쿼리 구조 검토
- **복합 인덱스 고려**
  - 자주 함께 쓰이는 조건 (예: `user_id`, `status`)에 대한 복합 인덱스 고려
- **모든 FK 컬럼에 인덱스 설정 여부 점검**
  - 조인 및 WHERE 조건에서 자주 쓰이는 컬럼은 인덱스 강제 부여 필요

---

## 9. 결론

- 단일 WHERE 조건이더라도 인덱스 부재 시 성능 저하가 발생
- 특히 `account_history` 같이 누적형 테이블에서는 인덱스 필수
- 인덱스 적용을 통해 **쿼리 실행 시간이 최대 99.9% 감소**하는 효과
- 향후 신규 기능/쿼리 작성 시, 인덱스 적용 여부를 **설계 단계에서부터 고려**하는 것이 중요
```
