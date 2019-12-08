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
![iterator2](https://user-images.githubusercontent.com/37579650/69910281-2049d100-144c-11ea-9563-f776de07b241.png)

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

### 설계 개선 - DocumentType
##### 기존코드 
```
public class DocumentType extends LeafNode {
    // todo needs a bit of a chunky cleanup. this level of detail isn't needed
    public static final String PUBLIC_KEY = "PUBLIC";
    public static final String SYSTEM_KEY = "SYSTEM";
    private static final String NAME = "name";
    private static final String PUB_SYS_KEY = "pubSysKey"; // PUBLIC or SYSTEM
    private static final String PUBLIC_ID = "publicId";
    private static final String SYSTEM_ID = "systemId";
    // todo: quirk mode from publicId and systemId

    /**
     * Create a new doctype element.
     * @param name the doctype's name
     * @param publicId the doctype's public ID
     * @param systemId the doctype's system ID
     */
    public DocumentType(String name, String publicId, String systemId) {
        Validate.notNull(name);
        Validate.notNull(publicId);
        Validate.notNull(systemId);
        attr(NAME, name);
        attr(PUBLIC_ID, publicId);
        if (has(PUBLIC_ID)) {
            attr(PUB_SYS_KEY, PUBLIC_KEY);
        }
        attr(SYSTEM_ID, systemId);
    }
    .
    .
}
```
#### 문제점
![기존구조](https://user-images.githubusercontent.com/37579650/69910351-7a976180-144d-11ea-85d0-bce2724b607c.png)

1. PUBLIC_KEY 같은 값이, DocumentType클래스에 final변수로 고정되어 있다.
2. 해당 final변수들을 다양하게 가져가려 할 때. DocumentType클래스를 수정해야 한다.
3. DocumentType객체에 PUBLIC_KEY등을 변경할때 변경값, 현재 값이 따로 저장되어 있지 않다.

#### 해결방안 (Factory Method Pattern + Singleton Pattern, OCP, DIP)
![개선](https://user-images.githubusercontent.com/37579650/69910350-7a976180-144d-11ea-8c3c-343615d1b90b.png)
1. 해당 final 변수들을 설정값 클래스로 따로 분리
2. 상황에따라 해당 기본 값 변경시 클래스를 확장, composition으로 처리 할 수 있도록 Abstract Class 구현
3. Abstract Class를 확장하여 OCP 구현.
4. DocumentType 클래스로 부터의 final변수에 대한 의존성 분리, DIP 적용
5. 설정값이 추가될 때 마다 코드수정을 최소화하도록 Factory Method Pattern 적용
6. Factory Instance는 SingleTon 적용

##### 기존코드 
```
public class DocumentType extends LeafNode {
    // todo needs a bit of a chunky cleanup. this level of detail isn't needed
    public static final String PUBLIC_KEY = "PUBLIC";
    public static final String SYSTEM_KEY = "SYSTEM";
    private static final String NAME = "name";
    private static final String PUB_SYS_KEY = "pubSysKey"; // PUBLIC or SYSTEM
    private static final String PUBLIC_ID = "publicId";
    private static final String SYSTEM_ID = "systemId";
    // todo: quirk mode from publicId and systemId

    /**
     * Create a new doctype element.
     * @param name the doctype's name
     * @param publicId the doctype's public ID
     * @param systemId the doctype's system ID
     */
    public DocumentType(String name, String publicId, String systemId) {
        Validate.notNull(name);
        Validate.notNull(publicId);
        Validate.notNull(systemId);
        attr(NAME, name);
        attr(PUBLIC_ID, publicId);
        if (has(PUBLIC_ID)) {
            attr(PUB_SYS_KEY, PUBLIC_KEY);
        }
        attr(SYSTEM_ID, systemId);
    }
}
```
##### 개선된 코드
```
public class DocumentType extends LeafNode {

  private KeyStoreFactory keyStoreFactory = KeyStoreFactory.getInstance();
  private KeyStore keyStore;

  /**
   * Create a new doctype element.
   *
   * @param name     the doctype's name
   * @param publicId the doctype's public ID
   * @param systemId the doctype's system ID
   */
  public DocumentType(String name, String publicId, String systemId) {

    this.keyStore = keyStoreFactory.getKeyStore("default");

    Validate.notNull(name);
    Validate.notNull(publicId);
    Validate.notNull(systemId);

    this.keyStore.setName(name);
    this.keyStore.setPublicId(publicId);
    this.keyStore.setSystemId(systemId);

    attr(this.keyStore.getNameType(), this.keyStore.getName());
    attr(this.keyStore.getPublicIdType(), this.keyStore.getPublicId());
    if (has(keyStore.getPublicIdType())) {
      attr(keyStore.getPublicSystemKeyType(), this.keyStore.getPublicKey());
    }
    attr(this.keyStore.getSystemIdType(), this.keyStore.getSystemId());
  }
  
   public void setKeyStore(String name) {
      this.keyStore = keyStoreFactory.getKeyStore(name);
   }
  .
  .
}
```
##### KeyStore Abstract Class
```
package org.jsoup.nodes.keystore;

public abstract class KeyStore {
  private String PUBLIC_KEY_TYPE;
  private String SYSTEM_KEY_TYPE;
  private String NAME_TYPE;
  private String PUB_SYS_KEY_TYPE;
  private String PUBLIC_ID_TYPE;
  private String SYSTEM_ID_TYPE;

  public KeyStore(){
    this.PUBLIC_KEY_TYPE = "PUBLIC";
    this.SYSTEM_KEY_TYPE = "SYSTEM";
    this.NAME_TYPE = "name";
    this.PUB_SYS_KEY_TYPE = "pubSysKey";
    this.PUBLIC_ID_TYPE = "publicId";
    this.SYSTEM_ID_TYPE= "systemId";
  }

  public String getPublicKeyType() {
    return this.PUBLIC_KEY_TYPE;
  }
  public String getSystemKeyType() {
    return this.SYSTEM_KEY_TYPE;
  }
  public String getNameType() {
    return this.NAME_TYPE;
  }
  public String getPublicSystemKeyType() {
    return this.PUB_SYS_KEY_TYPE;
  }
  public String getPublicIdType() {
    return this.PUBLIC_ID_TYPE;
  }
  public String getSystemIdType() {
    return this.SYSTEM_ID_TYPE;
  }

  abstract public String getPublicKey();
  abstract public String getSystemKey();
  abstract public String getName();
  abstract public String getPublicSystemKey();
  abstract public String getPublicId();
  abstract public String getSystemId();

  abstract public void setPublicKey(String value);
  abstract public void setSystemKey(String value);
  abstract public void setName(String value);
  abstract public void setPublicSystemKey(String value);
  abstract public void setPublicId(String value);
  abstract public void setSystemId(String value);

}
```
##### DefaultKeyStore (KeyStore Implementation)
```
package org.jsoup.nodes.keystore;

public class DefaultKeyStore extends KeyStore {

  private String PUBLIC_KEY;
  private String SYSTEM_KEY;
  private String NAME;
  private String PUB_SYS_KEY;
  private String PUBLIC_ID;
  private String SYSTEM_ID;

  public DefaultKeyStore(){
    super();
    this.PUBLIC_KEY = "PUBLIC";
    this.SYSTEM_KEY = "SYSTEM";
    this.NAME = "name";
    this.PUB_SYS_KEY = "pubSysKey";
    this.PUBLIC_ID = "publicId";
    this.SYSTEM_ID = "systemId";
  }

  @Override
  public String getPublicKey() {
    return this.PUBLIC_KEY;
  }

  @Override
  public String getSystemKey() {
    return this.SYSTEM_KEY;
  }
  @Override
  public String getName() {
    return this.NAME;
  }
  @Override
  public String getPublicSystemKey() {
    return this.PUB_SYS_KEY;
  }

  @Override
  public String getPublicId() {
    return this.PUBLIC_ID;
  }

  @Override
  public String getSystemId() {
    return this.SYSTEM_ID;
  }

  @Override
  public void setPublicKey(String value) {
    this.PUBLIC_KEY = value;
  }

  @Override
  public void setSystemKey(String value) {
    this.SYSTEM_KEY = value;
  }
  @Override
  public void setName(String value) {
    this.NAME = value;
  }
  @Override
  public void setPublicSystemKey(String value) {
    this.PUB_SYS_KEY = value;
  }

  @Override
  public void setPublicId(String value) {
    this.PUBLIC_ID = value;
  }

  @Override
  public void setSystemId(String value) {
    this.SYSTEM_ID = value;
  }

}
```
##### KeyStoreFactory( Singleton, Factory)
```
package org.jsoup.nodes.keystore;

public class KeyStoreFactory {

  private volatile static KeyStoreFactory instance = null;

  private KeyStoreFactory(){};

  public static KeyStoreFactory getInstance(){
    if(instance == null){
      synchronized (KeyStoreFactory.class){
        if(instance == null){
          instance = new KeyStoreFactory();
        }
        return instance;
      }
    }
    return instance;
  }

  public KeyStore getKeyStore(String name){
    if(name == "default"){
      return new DefaultKeyStore();
    }
    return new DefaultKeyStore();
  }
}

```

### 테스트
#### 테스트코드
```
package org.jsoup.nodes;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the DocumentType node
 */
public class DocumentTypeTest {
    @Test
    public void constructorValidationOkWithBlankName() {
        DocumentType fail = new DocumentType("","", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorValidationThrowsExceptionOnNulls() {
        DocumentType fail = new DocumentType("html", null, null);
    }

    @Test
    public void constructorValidationOkWithBlankPublicAndSystemIds() {
        DocumentType fail = new DocumentType("html","", "");
    }

    @Test public void outerHtmlGeneration() {
        DocumentType html5 = new DocumentType("html", "", "");
        assertEquals("<!doctype html>", html5.outerHtml());

        DocumentType publicDocType = new DocumentType("html", "-//IETF//DTD HTML//", "");
        assertEquals("<!DOCTYPE html PUBLIC \"-//IETF//DTD HTML//\">", publicDocType.outerHtml());

        DocumentType systemDocType = new DocumentType("html", "", "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd");
        assertEquals("<!DOCTYPE html \"http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd\">", systemDocType.outerHtml());

        DocumentType combo = new DocumentType("notHtml", "--public", "--system");
        assertEquals("<!DOCTYPE notHtml PUBLIC \"--public\" \"--system\">", combo.outerHtml());
    }

    @Test public void testRoundTrip() {
        String base = "<!DOCTYPE html>";
        assertEquals("<!doctype html>", htmlOutput(base));
        assertEquals(base, xmlOutput(base));

        String publicDoc = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
        assertEquals(publicDoc, htmlOutput(publicDoc));
        assertEquals(publicDoc, xmlOutput(publicDoc));

        String systemDoc = "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\">";
        assertEquals(systemDoc, htmlOutput(systemDoc));
        assertEquals(systemDoc, xmlOutput(systemDoc));

        String legacyDoc = "<!DOCTYPE html SYSTEM \"about:legacy-compat\">";
        assertEquals(legacyDoc, htmlOutput(legacyDoc));
        assertEquals(legacyDoc, xmlOutput(legacyDoc));
    }

    private String htmlOutput(String in) {
        DocumentType type = (DocumentType) Jsoup.parse(in).childNode(0);
        return type.outerHtml();
    }

    private String xmlOutput(String in) {
        return Jsoup.parse(in, "", Parser.xmlParser()).childNode(0).outerHtml();
    }
}

```
#### 테스트결과
<img width="1639" alt="테스트결과" src="https://user-images.githubusercontent.com/37579650/69910757-dbc23380-1453-11ea-8238-be107bdf68df.png">
