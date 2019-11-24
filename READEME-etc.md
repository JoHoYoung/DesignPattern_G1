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


## Validate.java
## HttpConnection.java
## W3CDom.java

# integration

# internal

# nodes

=> 다른 팀원이 분석

# parser

=> 다른 팀원이 분석

# safety


# select

=> 다른 팀원이 분석

# Connection.java

HTTP 연결 관련 interface

# Jsoup.java

실제 Jsoup 라이브러리에서 기능을 제공하는 부분

# Exceptions

Exception를 상속하여 특정 상황의 에러 클래스를 만든다.

## HttpStatusException.java
## SerializationException.java
## UncheckedIOException.java
## UnsupportedMimeTypeException.java



/* 이거 아래는 다 무시
# MultiLocaleRule.java


# TextUtil.java

## Dependency

1. java.util.regex.Pattern 

정규표현식을 이용해서 사용자가 설계된 원하는 값만 입력할 수 있도록 만들어 준다. 

```
\r	The carriage-return character ('\u000D')
X?	X, once or not at all
\n	The newline (line feed) character ('\u000A')
\s	A whitespace character: [ \t\n\x0B\f\r]
X*	X, zero or more times
```

> \r는 탈출 문자(Escape Character)로 Carriage Return(CR)이란 의미를 가진다.   
> \n은 Line Feed(LF)란 의미를 가지며 일반적으로 New Line이라고 읽는다.

> OS에 따라서 줄바꿈을 다르게 사용합니다.
윈도우 계열은 CRLF, unix 계열은 LF를 사용합니다.

## Structure

### stripeNewlines

```
Pattern.compile("\\r?\\n\\s*");
```

위 정규식 개행문자와 공백의 합을 나타냅니다.

stripeNewlines 함수는 개행문자를 찾아서
빈문자열("")으로 바꿔주는 함수 입니다.
