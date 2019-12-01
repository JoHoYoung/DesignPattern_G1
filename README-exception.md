# Exceptions

**역할** Exception를 상속하여 특정 상황의 에러 클래스를 만든다.

# Class Diagram 
#
![exception-all](./images/exception-all.png)
#
# 주요 클래스 설명
#
HttpStatusException: HTTP Status 코드와 메세지를 갖는 Exception
#
SerializationException: DOM 문서를 직렬화 시키지 못할 경우 발생하는 Exception
#
UncheckedIOException: 버퍼링 중에 발생하는 IO Exception
#
UnsupportedMimeTypeException: 지원하지 않는 MIME Type를 요청할 경우 발생하는 Exception
#
# 기능 개선
#
#
# 목적
#
Exceptions Class를 좀 더 쉽게 관리한다.
#
#
# 해결책
#
exception package 생성 이후 Exceptions Class를 해당 패키지로 이동