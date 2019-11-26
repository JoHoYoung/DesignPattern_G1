# jsoup analysis Deign Pattern

# ETC (Select, Nodes, Parser를 제외한 클래스)

# helper

## ChangeNotifyingArrayList.java

ArrayList를 상속한 추상클래스

add/remove등 arrayList 내부에 변화가 생길 경우에
onContentsChanged를 호출하도록 구현했으며
이때 onContentsChanged 메소드는 실제 구현한 클래스에게 위임한다.

> 적용된 패턴    
> 템플릿 패턴

## DataUtil.java

Internal static utilities for handling data.


## Validate.java

isFalse, isTrue, noNullElements등을 validate를 확인하는 static method가 모여있다.

## HttpConnection.java

Connection 인터페이스를 HTTP에 맞게 주현한 클래스

## W3CDom.java

org.jsoup.nodes.Document 를 org.w3c.dom.Document로 바꿔주는 클래스

> 적용된 패턴
> javax.xml.parsers.DocumentBuilderFactory => 팩토리 패턴

# internal

## ConstrainableInputStream.java

InputStream를 상속한 클래스

## Normalizer.java

lowerCase와 trim를 하는 기능을 가진 클래스

## StringUtil.java

String 관련 유틸리티 클래스

# nodes

=> 다른 팀원이 분석

# parser

=> 다른 팀원이 분석

# safety

## Cleaner.java

HTML파일을 whitelist로 변환해주는 클래스

## Whitelist.java

HTML에 정의된 요소와 속성을 제외한 내용은 삭제하는 클래스

# select

=> 다른 팀원이 분석

# Connection.java

연결 관련 interface

# Jsoup.java

실제 Jsoup 라이브러리에서 기능을 제공하는 부분

# Exceptions

Exception를 상속하여 특정 상황의 에러 클래스를 만든다.

## HttpStatusException.java
## SerializationException.java
## UncheckedIOException.java
## UnsupportedMimeTypeException.java