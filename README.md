# crawler

## 프로젝트 구조

```
/java
  /com.example.homeworkcrawler
    /application
      /CrawlingApplication.java - 크롤링 비즈니스 로직  
      /extractor/NumberAlphabetExtractor.java - 크롤링한 문서의 컨텐츠 추출(정렬,중복제거,머지)
    /config
      /AppConfig.java - Bean 생성, 추가될 @Annotation 등 전반적인 설정
    /controller
      /CrawlingController.java - 크롤링 컨트롤러
    /exception
      /ExceptionHandleControllerAdvice.java - 예외처리 Advice
      /xxxException.java
    /infrastructure
      /cache/CacheEventLogger.java - Cache 처리시 생성 및 만료 로그 등을 위한 listener 
    /service
      /HtmlDocumentService.java - Html 문서를 조회
    /util
      /xxxUtil.java
/resource
  /application.yml
  /ehcache.xml - ehcache 설정
/http
  /crawling.http - 실제 API를 호출하여 각 케이스를 테스트 할 수 있도록 추가(IntelliJ 제공 기능) 
```

## 구현
### 크롤링
* 크롤링 API에 2개 이상의 대상 URL이 전달될 경우 1개라도 실패하면 결과는 실패로 내려가며 모든 실패한 사유 및 URL을 반환하게 처리하였습니다.
``` 
성공 응답
{
  "success": true,
  "data": "Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9KkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz",
  "message": null
}

실패 응답
{
  "success": false,
  "data": null,
  "message": "[HTTP error fetching URL. Status=404, URL=[https://stackoverflow.com/abcdefg]] https://stackoverflow.com/abcdefg,[Unknown host] https://shop.hyundaiabcdefg.com"
}
```
### 병렬 처리
* ForkJoinPool
  * parallelStream() 을 사용합니다.
  * 기본 pool 이 사용되지 않도록 core수*4만큼의 pool을 config에 정의하였습니다. 
### Timeout 처리
* @Retryable 을 이용하여 지정된 exception이 throw 될때만 재시도를 합니다.
### 문서 조회 및 파싱
* Jsoup을 사용하여 html 컨텐츠를 읽어옵니다. 
* 요구사항은 tag를 포함한 전체 html을 읽어서 처리하지만 tag를 제외한 text만 읽도록 변경 될 수 있으므로 Jsoup을 사용하는게 유리하다 판단했습니다.  
* html 문서 조회시 Redirect 및 Timeout을 고려하였습니다.
* URL에 Scheme이 없는 경우 https로 호출합니다.
### 문서 컨텐츠 추출
* 숫자의 경우 중복 제거 및 정렬을 위해 TreeMap을 사용합니다.
* 문자의 경우 대/소문자 Pair를 만듭니다.
* 최종 결과 merge를 위해 Queue(LinkedList)를 사용하여 남아 있는 요소가 있는지 없는지 확인합니다.
### Cache
* ehcache를 사용합니다. LRU 알고리즘이 적용되며 ehcache.xml에 메모리 사이즈, 만료시간, 리스너의 위치 설정이 있습니다.
* 리스너(CacheEventLogger)에서는 캐시 생성, 만료시 로그를 기록합니다.

## 테스트
### Spock
* 단위 테스트를 위해 Spock Framework를 사용합니다.
* 성공 및 실패 등의 케이스를 위한 테스트 코드를 추가하였습니다.
### Intellij HTTP
* {project root}/http/crawling.http 에 API 테스트를 위한 엔드포인트들을 추가하였습니다.
* 요구사항에 있는 기본3개 사이트 외에 리다이렉트, 타임아웃, Unknown Host 등의 각 케이스별 API 응답을 볼 수 있도록 하였습니다. 로컬에서 서버 어플리케이션 실행 후 각 호출을 테스트 할 수 있습니다.