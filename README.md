# Select 

**역할** 요청한 주소의 HTML 코드와 선택자 ( ex. a[href] ) 를 이용하여 HTML 코드에서 추출하고 싶은 태그를 추려낼 수 있도록 함.

**동작과정**  ex. 네이버 메인에서 a[href] 만 추출하고 싶은 경우

1. Document doc = Jsoup.connect("https://naver.com").get();  ( doc 은 네이버 메인의 HTML 코드 )

2. Elements links = doc.select("a[href]"); ( Elements 는 Element들이 들어있는 ArrayList 이다. )

3. doc.select("a[href]"); 는 Selector 클래스의 select("a[href]", doc); 를 리턴한다.

4. Selector 클래스의 select("a[href]", doc); 는 Selector 클래스의 select(QueryParser.parse("a[href]"), doc); 를 리턴한다.

5. QueryParser.parse("a[href]") 는 Evaluator 클래스형을 리턴하는 메소드이다. ( 이후에 HTML 코드와 비교할 때 사용됨. )

6. Selector 클래스의 select(QueryParser.parse("a[href]"), doc); 는 Collector.collect(evaluator, doc); 를 리턴한다.

7. Collector.collect(evaluator, doc); 에서 본격적으로 탐색이 시작되는데 
    
   public static Elements collect(Evaluator eval, Element root) { // root 가 doc 
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



 계속해서 추가하겠습니다.
