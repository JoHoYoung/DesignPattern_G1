# Select 
#
요청한 주소의 HTML 코드와 선택자 ( ex. a , div , img > div 등등 CSS Selector ) 를 이용하여 HTML 코드에서 추출하고 싶은 태그를 추려낼 수 있도록 함.
#
# Class Diagram 
#
![Final-Class-Diagram](https://user-images.githubusercontent.com/57391270/69909778-854cf900-1443-11ea-9e72-a9d8def3975c.jpg)
#
# 주요 클래스 설명
#
Collector : 조건에 맞는 Element 들을 반환한다.
#
Selector :  사용자의 입력을 QueryParser 를 이용해 처리하고 Collector 클래스에게 넘겨준다.
#
Elements : Elements 는 Element 를 모은 ArrayList 인데 ArrayList에 적용할 수 있는 메소드들을 정의한다.
#
Evaluator : 여러 조건의 Evaluator 들이 사용할 수 있는 메소드를 정의하는 추상 클래스
#
CombiningEvaluator : 사용자가 입력한 Combining 연산자를 처리한다.
#
StructuralEvaluator : 사용자가 입력한 Structural 연산자를 처리한다.
#
NodeTraversor :  DFS 방식으로 노드를 탐색하는 클래스
#
QueryParser : 사용자가 입력한 조건(String)을 파싱하여 String의 의미를 파악하고 그 결과로 적절한 Evaluator 를 반환한다. 
#
# 적용된 디자인패턴

팩토리 패턴과 전략 패턴이 사용되었다.

# 팩토리 패턴

![Factory-1](https://user-images.githubusercontent.com/57391270/69908493-11ecbc80-142e-11ea-8c9a-8f635bbacb56.JPG)
#
사용자가 입력한 조건인 Css Selector 를 파싱하여 의미를 파악하게 도와주는
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
# 목적
#
HTML 코드를 탐색하는 알고리즘의 유연성을 확보한다.
#
#
# 해결책
#
전략 패턴 적용
#
# 기존 코드와의 비교 및 개선
#
![Strategy_BEFORE](https://user-images.githubusercontent.com/57391270/69910561-c566a880-1450-11ea-8d9c-46deeb96a26d.JPG)
#
기존 코드는 NodeTraversor 에서 직접 DFS로 구현된 traverse 를 호출하여 알고리즘에 대한 유연성이 부족했다.
#
![Strategy1](https://user-images.githubusercontent.com/57391270/69910592-3c03a600-1451-11ea-96d5-a8c610448fa1.JPG)
#
이를 개선하고자 MasterTraverse 인터페이스를 생성하여 유연성을 확보하였다.
#
![Strategy2](https://user-images.githubusercontent.com/57391270/69910598-53db2a00-1451-11ea-8197-98ad9c75975f.JPG)
![Strategy3](https://user-images.githubusercontent.com/57391270/69910599-5ccbfb80-1451-11ea-923b-74d62f6943ee.JPG)
#
서로 다른 알고리즘이 들어있는 클래스에서 MasterTraverse 를 구현한다.
#
![StrategyDFS](https://user-images.githubusercontent.com/57391270/69910608-8d139a00-1451-11ea-8542-7db5d8d22f69.JPG)
#
위 그림은 기존 DFS 코드이다.
#
![StrategyBFS](https://user-images.githubusercontent.com/57391270/69910613-9bfa4c80-1451-11ea-9ebf-0cd0e86b6663.JPG)
#
위 그림은 새로운 클래스에 추가된 BFS 코드이다.
#
![Strategy_AFTER](https://user-images.githubusercontent.com/57391270/69910621-b7655780-1451-11ea-9f7a-609e217a42ca.JPG)
#
클라이언트 호출 코드이다.
#

