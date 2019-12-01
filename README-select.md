# Select 

**역할** 요청한 주소의 HTML 코드와 선택자 ( ex. a , div , img > div 등등 CSS Selector ) 를 이용하여 HTML 코드에서 추출하고 싶은 태그를 추려낼 수 있도록 함.

# Class Diagram 
#
![Final-Class-Diagram](https://user-images.githubusercontent.com/57391270/69909778-854cf900-1443-11ea-9e72-a9d8def3975c.jpg)
#
# 사용된 디자인패턴

팩토리 패턴과 커맨드 패턴이 사용되었다.

# 팩토리 패턴

![Factory-1](https://user-images.githubusercontent.com/57391270/69908493-11ecbc80-142e-11ea-8c9a-8f635bbacb56.JPG)
#
대표적으로 사용자가 입력한 Css Selector 를 파싱하여 의미를 파악하게 도와주는
QueryParser.parse() 메소드는 사용자의 String 입력에 맞춰 QueryParser 
클래스에서 생성할 클래스를 결정해 준다.  
#
  
![Factory-2](https://user-images.githubusercontent.com/57391270/69908501-3c3e7a00-142e-11ea-967f-84c63683ddc1.JPG)
#
QueryParser.parse() 에서 findElements() 메소드를 통해 태그에 맞는
적합한 생성자를 찾게 한다.
#
#
![Factory-2_1](https://user-images.githubusercontent.com/57391270/69908508-537d6780-142e-11ea-9c43-8cb8bfb07fd5.JPG)
![Factory-2_2](https://user-images.githubusercontent.com/57391270/69908510-61cb8380-142e-11ea-9b24-c611d7d62b7d.JPG)
#
결과적으로 태그에 맞게 생성된 클래스가 List(사진에서 evals) 에 추가된다.
#

# 전략 패턴
#
![Command-abstract](https://user-images.githubusercontent.com/57391270/69908519-9f301100-142e-11ea-91e3-67feb2d75a65.JPG)
#

Evaluator 추상 클래스를 만들어 공통적인 matches 메소드를 캡슐화한다.

![Command-Concrete](https://user-images.githubusercontent.com/57391270/69908520-a3f4c500-142e-11ea-8b3d-c91057383e3a.JPG)
#

![Command-Concrete_2](https://user-images.githubusercontent.com/57391270/69909637-5897e200-1441-11ea-98d0-b787661cb1ce.JPG)
Evaluator 클래스를 구현하는 Concrete 클래스에서는 각기 다른 matches 로직을
구현한다.  
#
![Command-1](https://user-images.githubusercontent.com/57391270/69908522-a5be8880-142e-11ea-847b-e0163ab238b0.JPG)

#
클라이언트에서는 matches 라는 메소드명만 알고 호출하여 로직은 캡슐화 된다.
#
#
#
# 기능 개선
#
#
# 문제
#
상위 몇 개 추출하는 메소드 없음. 또한 특정 depth 에서 추출하는데 시간이 많이 걸림
#
#
# 해결책
#
1. 상위 몇개 추출하는 메소드 추가.
#
2. 탐색 알고리즘이 들어있는 메소드를 전략패턴을 이용하여 DFS 뿐만아니라 BFS 도 이용하게 하여
특정 depth 에서의 발견이 용이하게 함
#
#
#
# 전체 동작 과정

**동작과정**  ex. 네이버 메인에서 a[href] 만 추출하고 싶은 경우

1. Document doc = Jsoup.connect("https://naver.com").get();  ( doc 은 네이버 메인의 HTML 코드 )

2. Elements links = doc.select("a[href]"); ( Elements 는 Element들이 들어있는 ArrayList 이다. )

3. doc.select("a[href]"); 는 Selector 클래스의 select("a[href]", doc); 를 리턴한다.

4. Selector 클래스의 select("a[href]", doc); 는 Selector 클래스의 select(QueryParser.parse("a[href]"), doc); 를 리턴한다.

5. QueryParser.parse("a[href]") 는 Evaluator 클래스형을 리턴하는 메소드이다. ( 이후에 HTML 코드와 비교할 때 사용됨. )

6. Selector 클래스의 select(QueryParser.parse("a[href]"), doc); 는 Collector.collect(evaluator, doc); 를 리턴한다.

7. Collector.collect(evaluator, doc); 에서 본격적으로 탐색이 시작되는데 
    
   public static Elements collect(Evaluator eval, Element root) { 
   
   
	Elements elements = new Elements();
	
	NodeTraversor.traverse(new Accumulator(root, elements, eval), root);
	
	return elements;
	
   }
  
  NodeTraversor.traverse(new Accumulator(root, elements, eval), root) 에서
  
  (1) root는 전체 HTML 코드이다. 
  
  (2) elements 는 추출한 결과( element들 )를 담을 ArrayList이다.
  
  (3) eval 은 QueryParser 를 거친 a [href] 이다. (HTML 코드에서 추려낼 대상)
  
  
  NodeTraversor.traverse 내부에서는 DFS 알고리즘을 사용하여 전체 HTML 코드를 탐색하면서 그 중 eval 와 일치하는 것을
  
  ArrayList인 elements에 저장한다.
  
  결과적으로 
  
  Elements links = doc.select("a[href]"); 실행을 통해 links 에는 doc 에서 a href 태그를 추출한 ArrayList<element> 를 반환하게된다.
  
  
 출력코드 : 
         
        print("\nLinks: (%d)", links.size());
	
        for (Element link : links) {
	
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
	    
        }
        
 출력결과 :
  
 * a: <https://www.naver.com#news_cast>  (연합뉴스 바로가기)
 * a: <https://www.naver.com#themecast>  (주제별캐스트 바로가기)
 * a: <https://www.naver.com#time_square>  (타임스퀘어 바로가기)
 * a: <https://www.naver.com#shp_cst>  (쇼핑캐스트 바로가기)
 * a: <https://www.naver.com#account>  (로그인 바로가기)
 * a: <https://whale.naver.com/whaleevent/?=main>  (NAVER Whale 지금 웨일 브라우저에서 달라진 네이버를 .)
 * a: <http://update.whale.naver.net/downloads/installers/WhaleSetup.exe>  ()
 * a: <http://help.naver.com/support/alias/contents2/naverhome/naverhome_1.naver>  (네이버를 시작페이지로)
 * a: <http://jr.naver.com>  (쥬니어네이버)
 * a: <http://happybean.naver.com/main/SectionMain.nhn>  (해피빈)
 * a: <>  (자동완성 펼치기)
 * a: <>  (한글 입력기)
