Iterator, Prototype, Visitor, Composite

#### Attribute
특정 Key(Attribute Name)의 Value를 저장하는 클래스
- Null Key 체크
- 

##### Attributes
Attribute를 저장하는 Iterator 

삭제기능, 추가기능.
Iterator 패턴

해당 Attributes안에 있는 모든 Attribute들을 html Attribute 형식의 String으로 출력가능
Attrbutes.html();


##### Node
부모노드를 가짐. 트리형태 . 추상클래스, Cloneable
```
  public abstract Attributes attributes();
  protected abstract boolean hasAttributes();
  public Node attr(String attributeKey, String attributeValue) {
    attributeKey = NodeUtils.parser(this).settings().normalizeAttribute(attributeKey);
    attributes().putIgnoreCase(attributeKey, attributeValue);
    return this;
    }
```
Prototype Pattern

구현체들은 Attributes를 Composition 하도록함.
Attribute를 추가하는 기능 제공

##### Element 
Node를 상속
Attibutes를 가지고 있음.
Attribute를 추가, 삭제.
Attributes를 확장한 Container 역


##### Document
Element를 상속
Parsing한내용, Location URI저장
HTML 내용저장
-> TreeBuilder를 사용해 HTML String Parsing한 Document 저장 하는 객체
Cloneable
title(), body(), Heade()를 통해 없을경우 태그생성, 있을경우 변경 OR 가져오기 가
Parser 주입

html() Method를 통해 Document HTML 내용 출력.
Charset변


##### DocumentType
Document의 타입 저장
타입에 publicID, systemID 저장



##### LeafNode
Composite Pattern => LeafNode / Element
