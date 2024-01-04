# 6조 브로셔

## 🧺 01. 프로젝트 <Pure Basket>소개

<aside>
💡왜 유기농 식품몰인가?

엔데믹 시대의 건강 트렌드에 발 맞춰 **헬시 플레저(Healthy Pleasure)문화**가 다가옴에 따라 단순히 식품을 판매하는 것이 아닌, 건강한 삶의 방식을 제안하고 고객이 자신의 **건강을 스스로 '관리'할 수 있는 환경**을 **조성**하는 것입니다. 

</aside>

헬시 플레저(Healthy Pleasure)는 **Healthy(건강한)와 Pleasure(기쁨)가 결합한 단어**로, **건강 관리의 즐거움**을 의미합니다.![image](https://github.com/Pure-Basket/Pure-Basket-BE/assets/35479166/b270b460-a34b-44ee-97d8-7c6cd82d39ca)


헬시 플레저(Healthy Pleasure)는 **Healthy(건강한)와 Pleasure(기쁨)가 결합한 단어**로, **건강 관리의 즐거움**을 의미합니다.

<details>
<summary><h2>📖 API & ERD 명세서</h2></summary>
    <details>
        <summary><h3>📗 API</h3></summary>
        <img src="https://github.com/Pure-Basket/Pure-Basket-BE/assets/35479166/a4465539-ec8d-4f7a-9c3b-34a7bb7978c0" alt="API Image">
    </details>
    <details>
        <summary><h3>📙 ERD</h3></summary>
        <img src="https://github.com/Pure-Basket/Pure-Basket-BE/assets/35479166/71b0a737-82ea-44ba-823b-14046492575d" alt="ERD Image">
    </details>
</details>

<br>
<br>

## 🎯 02. 프로젝트 목표

<aside>
💡 E-commerce 플랫폼의 주요 트래픽 발생 이벤트인 할인 이벤트를 가정하여 사용자들의 주문이 급격히 증가하였을 때 주문 오류 없는 안정적인 서비스가 가능한 서버를 구축하고자 하였습니다.

</aside>

1️⃣ **조회 성능 최적화**

- 대용량 트래픽 상황에서 유저의 조회 요청을 2초 이내로 응답 

2️⃣ **주문 성능 최적화**

- 대용량 트래픽 상황에서 유저의 주문 요청을 3초 이내로 응답 

3️⃣ **대용량 트래픽 상황에서 상품 주문 동시성 제어**

- 주문 시 실시간 재고 조회를 통한 주문 에러 발생율 0%

4️⃣ **실시간 서버 모니터링 및 로그 수집**

- 개발자가 발 뻗고 잘 수 있는 모니터링 시스템 구축

<br>
<br>

## 🏗️ 03. 서비스 아키텍처

![image](https://github.com/Pure-Basket/Pure-Basket-BE/assets/35479166/23c1746d-8575-482c-ac59-34563dc99c31)

<br>
<br>


## 🎢 04. 주요기능 (프론트 수정 후 gif)

[상품 조회 (1)](https://www.notion.so/1-f077751601634ad89e95a5dcc48e97c5?pvs=21)

[상품 주문 (1)](https://www.notion.so/1-9696260ea5714cf5b06a3bd9bf77b7d8?pvs=21)

<br>
<br>

## ✍️ 05. 기술적 선택 과정
<details>
<summary><strong>동시성 제어를 위해 비관적 락 적용</strong></summary>
<p>
💡 <strong>여러 유저가 같은 상품을 주문하는 경우 발생한 동시성 문제 해결을 위해 성능 비교를 통해 DB 비관적락 적용</strong>
</p>

<p><strong>의사 결정 과정</strong></p>

<p><strong>Lettuce을 이용한 분산락 VS Redisson을 이용한 분산락</strong></p>

<ul>
<li><strong>Lettuce를 이용한 분산락</strong>
    <ul>
    <li>spring-data-jpa의 기본 라이브러리를 이용해서 별도의 라이브러리 설치하지 않아도 됨</li>
    <li>spin lock 방식으로 동작해 동시에 많은 쓰레드가 락을 획득하려고 대기하는 경우엔 redis 서버에 부하가 갈 수 있다.</li>
    <li>retry 로직을 직접 구현해야 함</li>
    </ul>
</li>
<li><strong>Redisson을 이용한 분산락</strong>
    <ul>
    <li>spring에서 사용하기 위해 별도 라이브러리 설치 필요</li>
    <li>redis pub-sub 방식이라 lettuce에 비해 redis에 가해지는 부하가 적음</li>
    </ul>
</li>
</ul>

<p>⇒ 분산락을 사용한다면 Redisson을 사용하기로 결정</p>

<p><strong>Redis에 부하가 적은 redisson을 이용한 분산락과 비관적락을 사용한 경우 비교</strong></p>

<ul>
<li>10000개의 동시 요청이 들어오는 경우를 위한 test코드를 시행한 경우 Redis 분산락보다 비관적 락이 평균 1분 정도 더 빠르게 나옴.</li>
</ul>

<table>
    <thead>
    <tr>
        <th>Gradle 테스트 평균 속도</th>
        <th>redis 분산락(redisson)</th>
        <th>비관적락</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>동시에 10000개 요청</td>
        <td>5min 9sec</td>
        <td>3min 50sec</td>
    </tr>
    </tbody>
</table>

</details>
<details>
<summary><strong>CI/CD를 위해 Github Actions 적용</strong></summary>
<p>
💡 레퍼런스는 적지만, 빠르게 CI/CD를 구축할 수 있고, 현재 프로젝트 규모가 크지 않아서 Github Actions를 사용하기로 결정
</p>

<strong>Github Actions:</strong> 

<ul>
<li>별도의 서버 없이 Github에서 바로 실행 가능하고 초기 설정이 쉬움.</li>
<li>Jenkins에 비해 플러그인이나 레퍼런스가 적음.</li>
<li>작은 규모의 프로젝트 또는 간단한 워크플로우에 적합.</li>
</ul>

<strong>Jenkins:</strong> 

<ul>
<li>다양한 플러그인을 지원하고 자동화 테스트를 수행함.</li>
<li>대규모 프로젝트에서 빌드 파이프라인을 간단히 구성할 수 있음.</li>
<li>레퍼런스가 다양함.</li>
<li>초기 설정이 복잡하고, 별도의 서버를 구성해야 하며 러닝커브가 상대적으로 가파름.</li>
</ul>

</details>
<details>
<summary><strong>모니터링 툴로 Pinpoint와 prometheus/grafana 사용</strong></summary>
<p>
💡 초기 Prometheus/grafana를 사용하다가 어플리케이션의 병목 지점 파악을 위해 Pinpoint를 추가 도입
</p>

<strong>prometheus/grafana:</strong>

<ul>
<li>exporter로 모니터링 대상 시스템으로부터 pull 방식으로 메트릭을 받아오는 방식으로 동작</li>
<li>하나의 서비스만 모니터링하는 것이 아니라 연결된 다른 서비스들에 대한 모니터링</li>
<li>HA를 위한 이중화나 클러스터링이 불가능해서, thanos를 추가 설치해야함.</li>
<li>코드 레벨로 서비스의 병목 지점을 알려주지 않음.</li>
</ul>

<strong>Pinpoint:</strong>

<ul>
<li>어플리케이션의 병목 지점을 코드 레벨로 파악하여, 성능 저하 요소 및 문제 원인을 추적 가능함.</li>
<li>분산 시스템의 구성 맵과 노드 간의 트랜잭션 수를 한 눈에 파악 가능함.</li>
<li>특정 트랜잭션에서 실행된 메소드와 응답시간을 확인할 수 있고 어플리케이션의 오류나 예외 정보도 확인 가능</li>
</ul>

</details>
<details>
<summary><strong>이벤트 큐로 Kafka 사용</strong></summary>
<p>
💡 할인 이벤트 발생 시 이벤트 처리 API들의 서버 분리를 통하여 서버의 처리 성능을 개선하기 위해 Kafka 사용
</p>

<table>
<thead>
<tr>
<th></th>
<th>Kafka</th>
<th>RabbitMQ</th>
<th>Redis</th>
</tr>
</thead>
<tbody>
<tr>
<td>주문 내역 이벤트 보존 필요성</td>
<td>수신 확인 및 이벤트 별도 저장</td>
<td>수신 확인</td>
<td>수신 확인 없이 삭제</td>
</tr>
<tr>
<td>다수 API의 동시 처리 가능성</td>
<td>Pub/Sub 모델</td>
<td>기본적으로 단일 수신자 대상</td>
<td>Pub/Sub 모델</td>
</tr>
</tbody>
</table>

</details>
<details>
<summary><strong>Redis로 캐시 적용</strong></summary>
<p>
💡 랜딩 페이지의 조회 성능 개선을 위해 Redis의 caching 기능 사용
</p>

<table>
<thead>
<tr>
<th></th>
<th>Redis</th>
<th>Memcached</th>
</tr>
</thead>
<tbody>
<tr>
<td>사용목적 부합성 (Refresh Token, Caching)</td>
<td>사용 목적에 부합하며 다수의 활용 사례 있음</td>
<td>사용 목적에 부합하며 AWS Elasticache에서 지원</td>
</tr>
<tr>
<td>고가용성</td>
<td>Replication 구축 가능</td>
<td>Replication 불가</td>
</tr>
<tr>
<td>사용성</td>
<td>사용 경험 있음</td>
<td>사용 경험 없음, 스터디 필요</td>
</tr>
</tbody>
</table>

</details>
<details>
<summary><strong>서버 Scale out</strong></summary>
<p>
💡 트래픽 증가를 대비해 예비 서버 추가를 통한 Scale out시 로드밸런서로 AWS ALB 사용
</p>

<table>
<thead>
<tr>
<th></th>
<th>ALB</th>
<th>Nginx</th>
</tr>
</thead>
<tbody>
<tr>
<td>Scale-out 편의성</td>
<td>AWS의 Auto-scaling 활용 가능<br>Server 추가 시 target group에 추가로 간편하게 scale-out 가능</td>
<td>이벤트 시 수동 Scale-out 필요<br>server 추가 시에 설정 파일 직접 수정 필요</td>
</tr>
<tr>
<td>적정 리소스 할당성</td>
<td>트래픽에 따라 자원 할당</td>
<td>예상 트래픽과 다를 시 리소스 초과 또는 부족 발생 가능</td>
</tr>
<tr>
<td>HTTPS 적용</td>
<td>Certificate Manger로 HTTPS 적용 가능</td>
<td>HTTPS 적용을 위한 인증서 발금 및 설정 파일 직접 수정 필요</td>
</tr>
</tbody>
</table>

</details>

<br>
<br>

## 📈 06. 성능개선
<details>
<summary><strong>Redis 캐시로 조회 성능 개선</strong></summary>

랜딩페이지의 데이터를 불러올 때 Redis 캐시를 적용함으로써 홈페이지 조회 성능을 개선.

**테스트 환경:**

- **BE 서버:** t3.2xlarge
- **DB 서버:** db.t3.micro
- **Thread:** 1000명
- **Ramp-up time:** 1
- **Loop count:** 1

**테스트 결과:**

- **Redis Caching 적용 전:** 시나리오 테스트 결과 평균 응답시간 9445ms, TPS 59.31
- **Redis Caching 적용 후:** 시나리오 테스트 결과 평균 응답시간 1241ms, TPS 286.69

1000개의 쓰레드 요청에 대한 평균 응답시간 87% 개선됨.

<table>
<thead>
<tr>
<th></th>
<th>평균 응답시간</th>
<th>TPS</th>
</tr>
</thead>
<tbody>
<tr>
<td>Redis Cache X</td>
<td>9445ms</td>
<td>59.31</td>
</tr>
<tr>
<td>Redis Cache O</td>
<td>1241ms</td>
<td>286.69</td>
</tr>
</tbody>
</table>

</details>
<details>
<summary><strong>Kafka를 통한 주문 성능 개선</strong></summary>

카프카를 통해 주문을 받으면 우선적으로 재고를 차감하고, 주문 결과 저장, 장바구니 삭제 등의 작업은 비동기적으로 카프카 consumer에서 처리함으로 주문 로직에 대한 성능 개선.

**카프카 적용 전/후 성능 지표**

**테스트 환경:**

- **BE 서버:** t3.2xlarge
- **DB 서버:** db.t3.micro
- **Thread:** 1000명
- **Ramp-up time:** 1
- **Loop count:** 1

**테스트 결과:**

- **카프카 적용 전:** 시나리오 테스트 결과 평균 응답시간 5480ms
- **카프카 적용 후:** 시나리오 테스트 결과 평균 응답시간 2100ms

1000개의 쓰레드 요청에 대한 평균 응답시간 61% 개선됨.

<table>
<thead>
<tr>
<th></th>
<th>평균 응답시간</th>
<th>TPS</th>
</tr>
</thead>
<tbody>
<tr>
<td>카프카 적용 전</td>
<td>5480ms</td>
<td>32.54</td>
</tr>
<tr>
<td>카프카 적용 후</td>
<td>2100ms</td>
<td>68.1</td>
</tr>
</tbody>
</table>

<img src="https://github.com/Pure-Basket/Pure-Basket-BE/assets/35479166/aae597cb-f4e0-4773-b756-5a78829fbd11" alt="Kafka 성능 개선 이미지">

</details>
<details>
<summary><strong>N+1 문제 해결</strong></summary>
<p>
💡 Product 테이블 조회 시 연관된 Image, Purchase, Cart 테이블에서 N+1 문제 발생
</p>

<details>
<summary><strong>Product - Image</strong></summary>
<p>
💡 <strong>@BatchSize를 이용하여 N+1 문제 해결</strong>
</p>

- Product와 Image는 1:N 양방향 관계
- 상품들을 조회하는 페이지에서 조회된 상품 수만큼 연관된 image를 조회하는 N+1 문제 발생

**해결 방법**

- Fetch Join, @BatchSize, @Fetch(FetchMode.SUBSELECT) 비교

|  | 테스트 평균 응답속도(1번) | 특이 사항 |
| --- | --- | --- |
| fetch join | 617ms | 기존 Pagination 에서 사용하던 LIMIT 구문이 등장하지 않음<br>firstResult/maxResults specified with collection fetch; applying in memory 경고 로그 발생<br>Fetch join 하는 image가 collection 데이터라 조회하는 데이터가 매번 달라지기 때문에 limit 구문을 사용할 수 없고 모든 데이터를 조회한 후에 memory에서 페이지 처리하기 때문에 메모리 문제 발생 가능 |
| @BatchSize() | 652ms | 항상 @BatchSize(size = n)에서 설정한 숫자만큼의 product와 관련된 image를 조회함.<br>purebasket 서버스에서는 렌딩 페이지나 레시피 페이지 등에서 보여주는 상품의 개수가 달라서, 항상 n개의 상품에 대한 image를 조회하는 것은 적합하지 않을 수도 있다. |
| @Fetch(FetchMode.SUBSELECT) | 622ms | Subquery를 포함하면 8개의 쿼리로 @BatchSize를 사용했을 때보다 쿼리가 많음. |

- FetchJoin은 collection을 fetch join 하는 경우 메모리 성능 이슈가 발생할 수 있으므로 @BatchSize나 @Fetch(FetchMode.SUBSELECT)를 이용하기로 함.

- @BatchSize()와 @Fetch(FetchMode.SUBSELECT) 비교

| 평균 응답 속도(ms) | 100 명 동시 요청 | 500명 동시 요청 | 1000명 동시 요청 |
| --- | --- | --- | --- |
| @BatchSize() | 534.2 | 2544.0 | 6309.39 |
| @Fetch(FetchMode.SUBSELECT) | 651.7 | 3144.4 | 6724.22 |

- 페이지마다 조회해야 하는 상품의 개수가 달라서 @BatchSize를 사용하면 필요 이상의 상품 조회가 있을 수 있지만, 테스트 결과 @Fetch(FetchMode.SUBSELECT)보다 성능이 좋아서 @BatchSize를 이용하기로 함.

```java
@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @BatchSize(size = 21)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
}
```
</p>
</details>
<details>
<summary><strong>Product - Purchase</strong></summary>
<p>
- Product와 Purchase는 1:N 단방향 관계(Purchase에서만 Product 조회 가능)
- 유저 별 주문 내역을 조회하는 페이지에서 조회된 주문 내역 수만큼 연관된 product를 조회하는 N+1 문제 발생

**해결 방법**

- Fetch Join

    purchase 서비스는 pagination을 사용하고 있지만, fetch join 되는 대상인 Purchase가 collection이 아니기 때문에 Fetch Join을 사용해도 문제 없음(OutOfMemoryError 발생하지 않음)

```java
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p JOIN FETCH p.product WHERE p.member = :member")
    Page<Purchase> findAllByMember(Member member, Pageable pageable);

}
```
</p>
</details>
<details>
<summary><strong>Product - Cart</strong></summary>
<p>
- Product와 Cart는 1:N 단방향 관계(Cart에서만 Product 조회 가능)
- 유저 별 장바구니 내역을 조회하는 페이지에서 조회된 장바구니 내역 수만큼 연관된 product를 조회하는 N+1 문제 발생

**해결 방법**

- Fetch Join
    
    cart 서비스는 pagination을 사용하고 있지 않음
    
    pagination을 사용한다고 해도, fetch join 되는 대상인 Product가 collection이 아니기 때문에 Fetch Join을 사용해도 문제 없음(OutOfMemoryError 발생하지 않음)
    
```java
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

  @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.member = :member")
  List<Cart> findAllByMember(Member member);
  
}
```
</p>
</details>
</details>

<br>
<br>

## 😵‍💫 07. 트러블 슈팅
<details>
<summary><strong>DB Connection Pool의 대기 시간이 전체 응답 시간의 90% 이상을 점유</strong></summary>

<p>
💡 초당 1000개의 주문 요청 테스트 결과 병목구간이 HikariCP의 getConnection() 메서드인 것을 발견하고 병목구간 해소 시도
</p>

- **테스트 환경:**
    - **BE 서버:** 1EA x t3.xlarge
    - **DB 서버:** 1EA x db.t3.micro (connection pool = 60)
    - **Thread:** 1000명 / Ramp-up time: 1s / loop count: 1
    
    ![image](https://github.com/Pure-Basket/Pure-Basket-BE/assets/35479166/5aa75571-6252-44e5-b9ac-d68fae5c28bf)
    
- **트러블 슈팅 과정:**
    - 스프링의 기본 설정으로 max pool size와 minimum idle이 10인 것을 확인
    - AWS RDS 서버 확인 결과 DB의 최대 connection은 60인 것을 확인
        
        ![image](https://github.com/Pure-Basket/Pure-Basket-BE/assets/35479166/2b3c21a3-9dc0-4b64-b90d-51a63016dd2d)
        
    - BE 서버의 connection pool 사이즈 증가에 대한 trade-off는 메모리 점유인 것을 확인
    - BE 서버 시스템의 메모리는 여유가 있는 것을 확인하고 connection pool 사이즈 확장
    
    ```bash
    spring.datasource.hikari.maximum-pool-size=60
    spring.datasource.hikari.minimum-idle=60
    ```
    
- **결론:**
    - HikariCP에서 병목구간 지속됨
    - 현재 서버 구성의 한계인 60개의 connection으로는 초당 1000개의 주문을 처리할 수 없음을 인식 → BE/DB 서버 Scale up 결정

</details>
<details>
<summary><strong>Scale-up / Scale-out 해도 주문 성능 향상 안 되는 문제</strong></summary>

<aside>
💡 **DB 서버 scale-up 및 BE 서버를 두 대로 scale-out했지만 주문 성능이 크게 향상되지 않음**
</aside>

**scale up 전후 테스트 결과**

테스트 환경: 

- BE 서버 - 2EA x t3.2xlarge
- DB 서버 - 1EA x db.t3.xlarge (connection pool = 1600)
- Thread : 2000명 / Ramp-up time : 1s / loop count : 1

테스트 결과

|  | 평균 응답시간 | TPS |
| --- | --- | --- |
| BE 서버 1대 | 4410ms | 70.62 |
| BE 서버 2대 | 3010ms | 83.16 |

- 현재 재고 관리는 카프카에 이벤트를 전달하기 전에 처리하고 재고 차감을 위해 비관적 락 사용중
- Pinpoint 확인 결과 HikariCP 의 getConnection() 메서드 실행 시간이 2~3초 정도 걸림
- Connection pool 사이즈 조정하면서 test를 수행했지만 connection pool 커지면 getConnection() 에 걸리는 시간은 줄지만, 락을 얻기 위해 대기하는 시간이 증가해서 성능이 크게 향상 되지 않음

**HikariCP Connection Pool size 별 테스트 결과**

테스트 환경: 

- 서버 구성 - 2EA t3.2xlarge | rds - db.t3.xlarge
- Thread : 2000명 / Ramp-up time : 1s / loop count : 1

| Connection Pool size | 10 | 50 | 100 | 150 | 200 |
| --- | --- | --- | --- | --- | --- |
| TPS | 86.76 | 75.11 | 70.89 | 80.86 | 69.24 |
| 평균 응답시간 | 3.00sec | 3.38sec | 3.41sec | 2.81sec | 3.18sec |

**Connection Pool이 10인 경우**

DB Connection Pool의 대기 시간은 4684ms로 전체 실행 시간의 97%, 락을 얻기 위한 stock을 조회하는 sql을 실행할 때 걸리는 시간이 154ms로 전체의 3%를 차지함.

![Connection Pool 10](https://github.com/Pure-Basket/Pure-Basket-BE/assets/35479166/b0651c88-f910-47c9-9d39-54d24653d150)

**Connection Pool이 100인 경우**

DB Connection Pool의 대기 시간은 2730ms로 전체의 57%, 락을 얻기 위한 stock을 조회하는 sql을 실행할 때 걸리는 시간이 2014ms로 42%를 차지함.

![Connection Pool 100](https://github.com/Pure-Basket/Pure-Basket-BE/assets/35479166/fed820de-bf7a-45dc-987e-df485621b2dd)

**Connection Pool이 200인 경우**

DB Connection Pool의 대기 시간은 없지만, 락을 얻기 위한 stock을 조회하는 sql을 실행할 때 걸리는 시간이 3933ms로 전체의 99%를 차지함.

![Connection Pool 200](https://github.com/Pure-Basket/Pure-Basket-BE/assets/35479166/ef2fc433-fd97-4fcb-a296-f626cebc9d1f)

- 결론
    Connection Pool을 증가시키면 BE 서버의 DB Connection 대기시간은 0ms가 되었으나, 비관적락이 적용된 stock 테이블 조회 쿼리의 응답 시간 증가함.
    connection pool이 충분해도 비관적 락을 얻기 위해 대기하는 시간이 병목의 원인임.
    해결하기 위해선 재고 관리를 위해 락을 얻는 로직을 consumer에서 구현해야 함.
</details>
<details>
<summary><strong>Redis Cache 적용 시 SerializationException</strong></summary>
랜딩 페이지 상품 조회 API에 redis cache를 적용하고 redis 에 저장된 값을 불러올 때 Serialization 문제 발생
    
```java
Could not read JSON:Cannot construct instance of `org.springframework.data.domain.PageImpl` (no Creators, like default constructor,      exist): cannot deserialize from Object value
```
    
문제가 발생하는 이유는 Jackson이나 Gson 같은 직렬화/역직렬화 라이브러리들이 보통 기본 생성자나 getter/settter를 기반으로 직렬화/역직렬화를 수행하는데, PageImpl에 Creator가 없어서 Object로 deserialize할 수 없어서 생김

@JsonCreator로 PageImpl에 대한 Constructor를 만들어 역직렬화시에 사용할 수 있도록 해서 해결

```java
@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
public class RestPageImpl<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    private RestPageImpl(@JsonProperty("content") List<T> content,
                         @JsonProperty("number") int number,
                         @JsonProperty("size") int size,
                         @JsonProperty("totalElements") Long totalElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }

    private RestPageImpl(Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    public static<T> RestPageImpl<T> from(Page<T> page) {
        return new RestPageImpl<T>(page);
    }
}
```
</details>
<details>
    <summary><strong>Kafka 적용 시 ClassNotFoundException</strong></summary>
    카프카 컨슈머 서버를 분리한 후 컨슈머에서 ClassNotFoundException 발생

```java
failed to resolve class name. Class not found 
[com.example.purebasketbe.domain.purchase.dto.KafkaPurchaseDto]
```
직렬화/역직렬화 과정에서는 package 이름까지 포함하기 때문에, producer에서 serialize할 때 class의 fullname을 사용하므로 consumer에서 deserialize할 때 class is not in the trusted packages 에러가 발생하는 것

해결방법

JsonDeserializer에 setRemoveTypeHeaders 추가

카프카에서 메시지를 전송할 때 headers에 metadata를 담아서 보내는데, 이 때 담기는 metadata에 target type을 포함함.(target type은 전송하고자하는 객체의 패키지명)

consumerFactory에서 useHeadersIfPresent 값을 false로 지정하여 역직렬화에서 header에 담긴 패키지명을 사용하지 않도록 설정하여 해결
```java
@Bean
public ConsumerFactory<String, KafkaPurchaseDto> consumerFactory() {
    Map<String, Object> configs = new HashMap<>();

    configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(),
            new JsonDeserializer<>(KafkaPurchaseDto.class, false)
    );
}
```
</details>

<br>
<br>

## ⚡ 08. 기술스택

### 🖥️ Backend

**Tech Stack**

- Spring Boot
- Spring JPA
- Spring Security
- Kafka
- Elastic Search

**TEST**

- Junit5
- Jmeter

**CI/CD**

- Github Action

### 📺 Frontend

**Tech Stack**

- React

**DB**

- AWS RDS (MySQL)
- Redis

**DevOps**

- Docker
- AWS EC2
- AWS S3

**Logging & Monitoring**

- Logstash/Kibana
- Prometheus/Grafana
- Pinpoint

<br>
<br>

## 👪 09. 팀원 소개

| 역할 | 이름 | email | github | 기술 블로그 |
| --- | --- | --- | --- | --- |
| 팀장 | 홍준영 | hjunyoung10@gmail.com | https://github.com/hjunyoung | https://hjunyoung.github.io/ |
| 팀원 | 이상휴 | hyouoo@gmail.com | https://github.com/hyouoo | https://devlog1921.tistory.com/ |
| 팀원 | 박서윤 | wideskyinme@gmail.com | https://github.com/SeoYoonP | https://velog.io/@wideskyinme/posts |
