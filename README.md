# lemontree-assginment
> 레몬트리 과제입니다.


### 목차
1. [과제 설명](#과제-설명)
2. [요건 정리](#요건-정리)
3. [과제 결과물](#과제-결과물)


## 과제 설명
### 과제 내용
레몬트리에서 게시판에 Drag & Drop으로 글의 순서를 변경하는 기능을 구현하고자 한다. 
이 때 필요한 API를 설계하고 구현하시오. 레몬트리는 DB는 mysql 을 사용한다.

### 필요 게시판 구성 항목(구현에 필요한 항목은 추가 필요)
- title : String, 255자 이내
- contents : String, 255자 이상 가능
- nick_name : String, 작성자는 다른 데이터와 연결되지 않고 표시용
- created_at : 생성시간

### 필요 결과물
- 필요 테이블 정의(생성 가능한 mysql)
- 필요 api 정의 : 글 추가, 수정, 삭제, 리스트, 순서 이동
- api 구현 코드

## 요건 정리
### 요구 조건 정리
- UI개발은 제외하며, API만 개발
- 페이징을 한다는 가정(극한으로 10,000번의 게시물 -> 1번으로 이동 X)
- 페이징 개수 제한 확인(없어서 10으로 제한)
- Drag & Drop 파라미터 정의 : Drag & Drop으로 UI가 구현되어 있다고 가정


### 프로젝트 환경
- Build Tool : Gradle
- Spring Boot : 2.6.4
- Packaging : Jar
- Java : 11
- port : 9192

## 과제 결과물
### jar파일 생성
```java
./gradlew bootJar
```

### docker image 생성
```java
./gradlew bootBuildImage
```

### docker compse 실행
```java
docker-compose up
```

----
### 필요 테이블 정의
- 테이블 생성
```sql
-- 추가 컬럼)
-- updated_at : 게시글 수정 시간
-- order_id : 게시글 순서
CREATE TABLE `board` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `contents` text NOT NULL,
  `nick_name` varchar(25) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `order_id` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
)
``` 

### API 정의
<details>
  <summary> POST /v1/api/boards </summary>
  
  - Resource : POST /v1/api/boards
  - Description : 게시글 추가 API
  - Parameters :
  <div markdown="1">
    
  |parameter|Desc|type|
  |---|---|---|
  |title|게시글 제목|String|
  |contents|게시글 내용|String|
  |nick_name|작성자|String|
  </div>
  - return : 게시글 id
  
</details>  
<details>
  <summary> GET /v1/api/boards?page={page} </summary>
  
  - Resource : GET /v1/api/boards?page={page}
  - Description : 게시글 리스트 조회 API
  - Parameters :
  <div markdown="1">
    
  |parameter|Desc|
  |---|---|
  |page|리스트 페이지 넘버|
  </div>
  - return : 게시글 리스트(10개씩 페이징)
  
  ```java
  [
    {
        "id": 3,
        "title": "3번",
        "contents": "3번 내용",
        "nick_name": "test2",
        "created_at": "2022-03-20T00:06:19",
        "updated_at": "2022-03-20T00:10:10"
    },
    {
        "id": 2,
        "title": "2번",
        "contents": "2번 내용",
        "nick_name": "test2",
        "created_at": "2022-03-20T00:06:12",
        "updated_at": "2022-03-20T00:06:12"
    }
  ...
  ]
  ```
  
</details> 
<details>
  <summary> PUT /v1/api/boards/{id} </summary>
  
  - Resource : PUT /v1/api/boards/{id}
  - Description : 게시글 수정 API
  - Parameters :
  <div markdown="1">
    
  |parameter|Desc|type|
  |---|---|---|
  |title|게시글 제목|String|
  |contents|게시글 내용|String|
  |nick_name|작성자|String|
  </div>
  
  - return : 수정된 게시글 id
  
</details> 
<details>
  <summary> DELETE /v1/api/boards/{id} </summary>
  
  - Resource : DELETE /v1/api/boards/{id}
  - Description : 게시글 삭제 API
  - return : 게시글 id
  
</details> 
<details>
  <summary> POST /v1/api/board/{id}/order </summary>
  
  - Resource : POST /v1/api/board/{id}/order
  - Description : 게시글 순서 변경 API
  - Parameters : body
  <div markdown="1">
    
  |parameter|Desc|type|
  |---|---|---|
  |target_order_id|옮기려는 곳 순번|Integer|
  </div>
  
  - return : 게시글 id
  
</details> 

### Drag & Drop에 대한 설명
* 이번 과제 해결안) 
  * **Double** 타입의 **order_id**를 추가하여 순서를 변경할 수 있도록 한다.
  * mysql double 타입의 경우 소수점 **71번째** 짜리까지 들어간다.
  * 순서 변경 시, 옮기려는 순번에 사이 order_id값을 가져와 **중간값**으로 입력할 수 있도록한다.
  * 가장 마지막 리스트로 옮기는 경우, 마지막 order_id+1을 하고, 가장 처음으로 리스트를 옮기는 경우 0과 1번째 order_id의 중간값으로 입력한다.
  * 조회가 많을 것으로 예상되어 order_id에 인덱스를 걸어둔다.
* 문제점)
  * 소수점은 결국 한계가 있다. 이 부분을 해결하기 위해 생각해 본 방안은 아래와 같다.
* 방안)
  * 소수점의 길이가 기준이되는 자릿수(ex. 소수점 65번째라 가정)를 넘어가는 순간을 탐지한다.
  * 탐지가 된 순간, 배치잡을 통해 전체 리스트의 order_id를 초기화 할 수 있도록 한다.
  * 그동안의 클라이언트 단의 Drag & Drop은 잠시 멈춘다.

--------
그 외 생각했던 방법 및 문제점)
* 아래 방법들을 고민해보다가 가장 부하가 적고 빠르게 응답할 수 있다고 판단하여 위 방법으로 진행하게 되었습니다.
* (1) 순번 컬럼 추가, 다음에 오는 게시글 id(next_id)를 저장하는 방법
* |id|title|contents|next_id|
  |---|---|---|---|
  |1|test1|test11|2|
  |2|test2|test22|3|
  |3|test3|test33|null|
  
  * 문제점 : 리스트 조회 및 정렬 시 각 게시글의 다음 순번을 찾아 리스팅 필요하며 구현이 복잡해진다.
  * 게시글이 추가, 삭제가 되었을 때 가지고 있는 컬럼들을 찾아 같이 update를 해 줄 필요가 있다.


* (2) 정렬 컬럼을 100씩 추가 (ex. 100, 200, 300...)
  * 문제점 : 100씩 추가 시 100 ~ 200 범위에만 넣을 수 있기 때문에 빠른 시간내에 한계에 도달할 수 있다.


* (3) 옮기려는 순번까지의 게시글 모두 update를 한다.
* id 5 -> 2번째, id 5의 order_id는 2로 수정, order_id 2~4까지 모두 update를 해준다. 
* |id|title|contents|order_id|
  |---|---|---|---|
  |1|test1|test11|1|
  |2|test2|test22|2|
  |3|test3|test33|3|
  |4|test4|test44|4|
  |5|test5|test55|5|
  
  * 문제점 : 최악의 경우(ex.1번 게시글 삭제 등) 전체 게시글을 update해야하는 경우 발생한다.
  * 리스트가 수십만개의 경우, O(N)번 update가 필요하다.
  


* (4) order_id, depth 컬럼을 추가한다.
* 게시글이 앞 순서로 옮겨지는 경우, depth는 -1, -2로 update되며, 뒷 순서로 옮겨지는 경우, +1, +2로 update된다.
* 예외상황 : id 5 -> 2번으로 이동, id 1 삭제, id 3 -> 2번으로 이동
* |id|title|contents|order_id|depth|
  |---|---|---|---|---|
  |1|test1|test11|1|0|
  |2|test2|test22|2|0|
  |3|test3|test33|3|0|
  |4|test4|test44|4|0|
  |5|test5|test55|5|0|
  
  * 문제점 : 계속해서 같은 순번으로 옮기고자 할 때 order_id와 depth가 동일하게 겹치는 경우가 발생한다. 
  * 해결을 위해서는 옮겨지는 곳의 중간 값을 구하여 넣어야 한다.
  * 또는 depth 컬럼 자체를 추가하는 방법이 존재한다.
  * ex. depth1, depth2, depth3,,,


* (5) updated_at(시간단위)컬럼으로 순서를 
  * 문제점 : 옮겨지는 곳의 사이 시간값을 구하여 넣어야한다.
  * '시간'이란 컬럼에 순서 사이값을 넣는다면 컬럼의 의미가 불분명해진다고 생각되었다.
  * 또한 mysql micro초의 경우, 최대 6자리까지 비교가 가능하며 동시에 옮겨지는 경우에 대한 처리가 필요하다.
