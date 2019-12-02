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
#
![factory (1)](https://user-images.githubusercontent.com/57391270/69934639-e1804d80-1515-11ea-9a14-376b4dba5b84.jpg)
#
소스코드
#
```java
    public static Elements select(String query, Element root) {
        Validate.notEmpty(query);
        return select(QueryParser.parse(query), root);
    }
```
#
사용자가 입력한 조건인 Css Selector 를 파싱하여 의미를 파악하게 도와주는
QueryParser.parse() 메소드는 사용자의 String 입력에 맞춰 QueryParser 
클래스에서 생성할 클래스를 결정해 준다.  
#
  
```java
private void findElements() {
        if (tq.matchChomp("#"))
            byId();
        else if (tq.matchChomp("."))
            byClass();
        else if (tq.matchesWord() || tq.matches("*|"))
            byTag();
        else if (tq.matches("["))
            byAttribute();
        else if (tq.matchChomp("*"))
            allElements();
        else if (tq.matchChomp(":lt("))
            indexLessThan();
        else if (tq.matchChomp(":gt("))
            indexGreaterThan();
        else if (tq.matchChomp(":eq("))
            indexEquals();
```
#
QueryParser.parse() 에서 findElements() 메소드를 통해 태그에 맞는
적합한 생성자를 찾게 한다.
#
#
```java
    private void byId() {
        String id = tq.consumeCssIdentifier();
        Validate.notEmpty(id);
        evals.add(new Evaluator.Id(id));
    }
```
#
```java
    private void byClass() {
        String className = tq.consumeCssIdentifier();
        Validate.notEmpty(className);
        evals.add(new Evaluator.Class(className.trim()));
    }
```
#
결과적으로 태그에 맞게 생성된 클래스가 List(사진에서 evals) 에 추가된다.
#

# 전략 패턴
![strategy](https://user-images.githubusercontent.com/57391270/69934463-49826400-1515-11ea-86ee-23948c449395.jpg)
#
소스코드
#
```java
public abstract class Evaluator {
    protected Evaluator() {
    }

    /**
     * Test if the element meets the evaluator's requirements.
     *
     * @param root    Root of the matching subtree
     * @param element tested element
     * @return Returns <tt>true</tt> if the requirements are met or
     * <tt>false</tt> otherwise
     */
    public abstract boolean matches(Element root, Element element);
```
#

Evaluator 추상 클래스를 만들어 공통적인 matches 메소드를 캡슐화한다.

```java
public static final class Tag extends Evaluator {
        private String tagName;

        public Tag(String tagName) {
            this.tagName = tagName;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return (element.tagName().equalsIgnoreCase(tagName));
        }
```
#

```java
 public static final class IsFirstChild extends Evaluator {
    	@Override
    	public boolean matches(Element root, Element element) {
    		final Element p = element.parent();
    		return p != null && !(p instanceof Document) && element.elementSiblingIndex() == 0;
    	}
```
Evaluator 클래스를 구현하는 Concrete 클래스에서는 각기 다른 matches 로직을
구현한다.  
#
```java
public void head(Node node, int depth) {
			if (node instanceof Element) {
				Element el = (Element) node;
				if (eval.matches(root, el))
					elements.add(el);
			}
		}
```

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
```java
    public static Elements collect (Evaluator eval, Element root) {
        Elements elements = new Elements();
        NodeTraversor.traverse(new Accumulator(root, elements, eval), root);
        return elements;
    }
```
#
기존 코드는 NodeTraversor 에서 직접 DFS로 구현된 traverse 를 호출하여 알고리즘에 대한 유연성이 부족했다.
#
```java
public interface MasterTraverse {
	public void traverse(Node root);
	public static void traverse(NodeVisitor visitor, Node root) {}
}
```
#
이를 개선하고자 MasterTraverse 인터페이스를 생성하여 유연성을 확보하였다.
#
```java
public class NodeTraversor_BFS implements MasterTraverse{
```
#
```java
public class NodeTraversor implements MasterTraverse {
```
#
서로 다른 알고리즘이 들어있는 클래스에서 MasterTraverse 를 구현한다.
#
```java
public static void traverse(NodeVisitor visitor, Node root) { 
        Node node = root;
        int depth = 0;
        while (node != null) {
            visitor.head(node, depth);
            if (node.childNodeSize() > 0) {
                node = node.childNode(0);
                depth++;
            } else {
                while (node.nextSibling() == null && depth > 0) {
                    visitor.tail(node, depth);
                    node = node.parentNode();
                    depth--;
                }
                visitor.tail(node, depth);
                if (node == root)
                    break;
                node = node.nextSibling();
            }
        }
    }
```
#
위 그림은 기존 DFS 코드이다.
#
```java
    public static void traverse(NodeVisitor visitor, Node root) {
    	
    	Queue<Node> q = new LinkedList<Node>();
        Node node = root;
        int depth = 0;
        q.add(root);
        
        while(!q.isEmpty()) {
        	node = q.poll();
        	visitor.head(node, depth);
        	
        	for(int i=0; i<node.childNodeSize(); i++) {
        		q.add(node.childNode(i));
        	}
        }
        
    }

```
#
위 그림은 새로운 클래스에 추가된 BFS 코드이다.
#
```java
	public static Elements collect(Evaluator eval, Element root) {
		Elements elements = new Elements();
		
		//MasterTraverse for_dfs = new NodeTraversor(new Accumulator(root, elements, eval));
		MasterTraverse for_bfs = new NodeTraversor_BFS(new Accumulator(root, elements, eval));
		
		//for_dfs.traverse(root);
		for_bfs.traverse(root);;
		
		return elements;
		
	}

```
#
클라이언트 호출 코드이다.
#
#
테스트 코드
#
```java
package org.jsoup.select;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Collector.Accumulator;
import org.junit.jupiter.api.Test;

class CollectorTest {
	@Test
	void test_bfs_dfs() throws IOException {
		String h = "<html><div id=1><p>Test<p><b>Code</b></p></div><div id=2><span>Test</span></div><div></div></html>";
		String Cssquery = "div";
		Document doc = Jsoup.parse(h);
		Evaluator eval = QueryParser.parse(Cssquery);
		
		Elements DFS_elements = new Elements();
		Elements BFS_elements = new Elements();

		MasterTraverse for_dfs = new NodeTraversor(new Accumulator(doc, DFS_elements, eval));
		MasterTraverse for_bfs = new NodeTraversor_BFS(new Accumulator(doc, BFS_elements, eval));
		for_dfs.traverse(doc);
		for_bfs.traverse(doc);

		assertEquals(3, DFS_elements.size());
		assertEquals(3, BFS_elements.size());
	}
	
	@Test
	void test_bfs_dfs2() throws IOException {
		String h = "<p><img src=foo.png id=1><img src=bar.jpg id=2><img src=qux.JPEG id=3><img src=old.gif><img></p>";
		String Cssquery = "img";
		Document doc = Jsoup.parse(h);
		Evaluator eval = QueryParser.parse(Cssquery);
		
		Elements DFS_elements = new Elements();
		Elements BFS_elements = new Elements();

		MasterTraverse for_dfs = new NodeTraversor(new Accumulator(doc, DFS_elements, eval));
		MasterTraverse for_bfs = new NodeTraversor_BFS(new Accumulator(doc, BFS_elements, eval));
		for_dfs.traverse(doc);
		for_bfs.traverse(doc);
		
		assertEquals(5, DFS_elements.size());
		assertEquals(5, BFS_elements.size());
	}

	@Test
	void test_bfs_dfs3() throws IOException {
		String h = "<div title=foo /><div title=bar /><div /><p></p><img /><span title=qux>";
		String Cssquery = "div";
		Document doc = Jsoup.parse(h);
		Evaluator eval = QueryParser.parse(Cssquery);
		
		Elements DFS_elements = new Elements();
		Elements BFS_elements = new Elements();

		MasterTraverse for_dfs = new NodeTraversor(new Accumulator(doc, DFS_elements, eval));
		MasterTraverse for_bfs = new NodeTraversor_BFS(new Accumulator(doc, BFS_elements, eval));
		for_dfs.traverse(doc);
		for_bfs.traverse(doc);
		
		assertEquals(3, DFS_elements.size());
		assertEquals(3, BFS_elements.size());
	}
}
```
#
테스트 결과
#
![testResult](https://user-images.githubusercontent.com/57391270/69968082-0946d400-155d-11ea-80d8-12f02dd87b8a.JPG)
#

