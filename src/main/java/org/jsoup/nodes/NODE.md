## nodes

![alldiagram](https://user-images.githubusercontent.com/37579650/69910261-91d54f80-144b-11ea-9c6e-8bf6741c7bc5.png)
### Purpose

##### Attribute
> 특정 Key(Attribute Name)의 Value를 저장하는 클래스
 

##### Attributes
> Attribute를 저장하는 Iterator 삭제기능, 추가기능 제공 기본적인 Map의 형태. Iterator, Prototype Pattern 구
Attrbutes.html()을 통하여 해당 Attributes안에 있는 모든 Attribute들을 html Attribute 형식의 String으로 출력

##### Node
> Document, Element들을 트리 형태로 구성하기 위한 인터페이스. 구현체인 LeafNode, Element의 기능을 달리 함으로서 Composite Pattern 구현
> Attributes를 Composition하여 트리형태를 가지는 노드별로 구성. Cloneable을 구현함으로서 Prototype Pattern 사용

##### Element 
> Node의 구현체로서, Child를 추가하는 기능 구현. Composite Pattern의 Child 추가하는 역할 Cloneable을 구현함으로서 Prototype Pattern 사용

##### LeafNode
> Node의 구현체로서, Child추가 불가능. Composite Pattern의 LeafNode 역할 Cloneable을 구현함으로서 Prototype Pattern 사용

##### Document
> Element를 상속하는 클래스. Child Node추가 가능. Cloneable을 구현함으로서 Prototype Pattern 사용
> Document를 Parsing한 내용과, Locaion URI를 저장 할 수 있음.
> TreeBuilder를 사용해 HTML String Parsing한 Document 저장 하는 객체 Cloneable을 구현 함으로서 Prototype Pattern 사


##### DocumentType
>Document의 타입 저장 타입에 publicID, systemID 저장


### Design Patterns
#### Iterator Pattern(Attributes – Attribute)
![iterator](https://user-images.githubusercontent.com/37579650/69910263-91d54f80-144b-11ea-9054-70a0936b93e1.png)
```
public class Attributes implements Iterable<Attribute>, Cloneable {
.
.
   public Iterator<Attribute> iterator() {
        return new Iterator<Attribute>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public Attribute next() {
                final Attribute attr = new Attribute(keys[i], vals[i], Attributes.this);
                i++;
                return attr;
            }

            @Override
            public void remove() {
                Attributes.this.remove(--i); // next() advanced, so rewind
            }
        };
    }
.
.
}

```
> Iterator Interface 를 구현히여, Attribute를 순회하는 next, hasNext Method를 오버라이딩 하였다.

#### Prototype Pattern
![Prototype](https://user-images.githubusercontent.com/37579650/69910264-926de600-144b-11ea-989d-f9e9270b508b.png)
```
public class Attributes implements Iterable<Attribute>, Cloneable {
.
.
    @Override
    public Attributes clone() {
        Attributes clone;
        try {
            clone = (Attributes) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.size = size;
        keys = copyOf(keys, size);
        vals = copyOf(vals, size);
        return clone;
    }
.
.
}

```

```
public abstract class Node implements Cloneable {
.
.
    @Override
    public Node clone() {
        Node thisClone = doClone(null); // splits for orphan

        // Queue up nodes that need their children cloned (BFS).
        final LinkedList<Node> nodesToProcess = new LinkedList<>();
        nodesToProcess.add(thisClone);

        while (!nodesToProcess.isEmpty()) {
        Node currParent = nodesToProcess.remove();

        final int size = currParent.childNodeSize();
        for (int i = 0; i < size; i++) {
            final List<Node> childNodes = currParent.ensureChildNodes();
            Node childClone = childNodes.get(i).doClone(currParent);
            childNodes.set(i, childClone);
            nodesToProcess.add(childClone);
         }
        }
    return thisClone;
  }
.
.
}
```
> Cloneable Interface를 구현하여, clone() Method를 오버라이딩, 객체를 복사하는 Prototype패턴

#### Composite Patter (Node,  LeafNode, Element)
##### Element, LeafNode는 Node의 구현체 이고, Element에는 자식노드를 추가 할 수 있으나, Leaf에는 자식노드를 추가 할 수 없다.
##### Element 자식 추가하는 코드
![composite](https://user-images.githubusercontent.com/37579650/69910262-91d54f80-144b-11ea-8a09-06f818d3aebc.png)
```
public class Element extends Node {

        public Element appendChild(Node child) {
            Validate.notNull(child);
            reparentChild(child);
            ensureChildNodes();
            childNodes.add(child);
            child.setSiblingIndex(childNodes.size() - 1);
            return this;
        }
        
        public Element insertChildren(int index, Collection<? extends Node> children) {
            Validate.notNull(children, "Children collection to be inserted must not be null.");
            int currentSize = childNodeSize();
            if (index < 0) index += currentSize +1; // roll around
            Validate.isTrue(index >= 0 && index <= currentSize, "Insert position out of bounds.");
    
            ArrayList<Node> nodes = new ArrayList<>(children);
            Node[] nodeArray = nodes.toArray(new Node[0]);
            addChildren(index, nodeArray);
            return this;
        }
    
        public Element insertChildren(int index, Node... children) {
            Validate.notNull(children, "Children collection to be inserted must not be null.");
            int currentSize = childNodeSize();
            if (index < 0) index += currentSize +1; // roll around
            Validate.isTrue(index >= 0 && index <= currentSize, "Insert position out of bounds.");
    
            addChildren(index, children);
            return this;
        }
        
}
```
##### LeafNode에는 자식을 추가하는 Method가 없다
```
abstract class LeafNode extends Node {
    protected final boolean hasAttributes() {}
    public final Attributes attributes() {}
    private void ensureAttributes() {}
    String coreValue() {}
    void coreValue(String value) {}
    public String attr(String key) {}
    public Node attr(String key, String value) {}
    public boolean hasAttr(String key) {}
    public Node removeAttr(String key) {}
    public String absUrl(String key) {}
    public String baseUri() {}
    protected void doSetBaseUri(String baseUri) {}
    public int childNodeSize() {}
    protected List<Node> ensureChildNodes() {}
}
```
> Node의 자식인 Element에는 자식 Node를 추가, LeafNode에는 자식을 추가할 수 없게하여 Composite Pattern을 사용하고 있다.

