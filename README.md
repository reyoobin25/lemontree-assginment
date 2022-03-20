# lemontree-assginment
레몬트리 과제 패키지 입니다.

목차는 아래와 같습니다.
[1. 과제 설명](#1. 과제 설명)
[2. 요건 정리](#2. 요건 정리)
- [요구 조건 정리](#요구 조건 정리)
- [프로젝트 환경](#프로젝트 환경)
[3. 과제 결과물](#3. 과제 결과물)
- [필요 테이블 정의](#필요 테이블 정의)
- [API 정의](#API 정의)
- [Drag & Drop에 대한 설명](#Drag & Drop에 대한 설명)
- 구현 코드

## 1. 과제 설명
### 배경
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

## 2. 요건 정리
### 요구 조건 정리
- UI개발은 제외하며, API만 개발
- 페이징을 한다는 가정(극한으로 10,000번의 게시물 -> 1번으로 이동 X)
- 페이징 개수 제한 확인(없어서 10으로 제한)
- Drag & Drop 파라미터 정의 : Drag & Drop으로 UI가 구현되어 있다고 가정


### 프로젝트 환경
- Build Tool : Gradle
- Spring Boot : 2.6.4
- Pakaging : Jar
- Java : 11

## 3. 과제 결과물
### 필요 테이블 정의
- 테이블 생성 쿼리
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
- Board APIs (url)
- GET /v1/api/boards
- POST /v1/api/boards?page={page}
- PUT /v1/api/boards/{id}
- DELETE /v1/api/boards/{id}
- POST /v1/api/board/{id}/order

### Drag & Drop에 대한 설명
order_id 컬럼을 double로 두어 순서를 변경할 수 있도록 한다.
mysql double의 경우 소수점 71번째 짜리까지 들어간다.
순서 변경 시, 옮기려는 순번에 사이 order_id값을 가져와 중간값으로 입력할 수 있도록한다.
가장 마지막 리스트로 옮기는 경우, 마지막 order_id+1을 하고, 가장 처음으로 리스트를 옮기는 경우 0과 1번째 order_id의 중간값으로 입력한다.

문제점 : 소수점은 결국 한계가 있다. 이 부분을 해결하기 위해 생각해 본 방안은 아래와 같다.
방안)
- 소수점의 길이가 어느정도가 넘어가는 순간(ex. 65번째)을 탐지한다.
- 탐지가 된 순간, 배치잡을 통해 전체 리스트의 order_id를 초기화 할 수 있도록 한다.
- 그동안의 클라이언트 단의 Drag & Drop은 잠시 멈춘다.

그 외 생각했던 방법 및 문제점)
(TODO : 빠른 결과 vs 정확한 결과,,)
- 1. 다음에 오는 게시글 id(next_id)를 저장하는 방법
- 문제점 : 리스트 정렬 시 

- 2. 정렬 컬럼을 100씩 추가 (ex. 100, 200, 300...)
- 문제점 : 100씩 추가 시 100 ~ 200 범위에만 넣을 수 있기 때문에 금방 차오를 수 있음

- 3. 옮기려는 순번까지의 게시글 모두 update
- 문제점 : 최악의 경우(ex.1번 게시글 삭제 등) 전체 게시글을 update해야하는 경우 발생


- 4. order_id, depth를 이용(사이 값 이용 X)
- 문제점 : 계속해서 같은 순번으로 옮기고자 할 때 order_id와 depth가 동일하게 겹치는 경우가 발생한다. 이때 depth를 추가하는 방법이 존재하나 이 또한 한계점이 있다.
- ex. depth1, depth2, depth3,,,

- 5. updated_at(시간단위)로 정렬
- 이번에 구현한 내용과 비슷하다. 시간 단위로 정렬을 하는 경우, '시간'이란 컬럼에 그 사이값을 넣는다면 컬럼의 의미가 불분명해진다고 판단되어 소수점으로 변경
- mysql micro초의 경우, 최대 6자리까지 비교가 가능하며 동시에 옮겨지는 경우에 대한 처리가 필요하다.
