# my-servlet-container
How Tomcat Works 실습

## Chapter2

### Facade 패턴 적용하기  
  
이번 챕터에서 흥미로웠던 점은 Facade 패턴을 적용하는 부분이었다.  
ServletRequest(또는 Response)를 구현하는 클래스 Request를 up-casting하여 Servlet에 전해주게 되면,
이를 사용하는 Servlet이 down-casting을 통해서 container만이 사용해야 하는 Request의 public 메소드를 사용할 수 있다는 문제점이 
발생한다는 부분이었다.  
이를 해결하기 위해서 동일하게 ServletRequest를 상속받는 RequestFacade를 생성하여, Servlet에게 Request를 직접 전달하는 것이 아닌,
이를 인스턴스 변수로 가지는 Facade를 제공하는 방법을 사용하였다.  
  
요즘에 리팩토링에 관한 서적을 읽다보니, up-casting을 통해서 다형성을 제공하는 것은 항상 좋다고 생각했었는데 
컨테이너 측면에서 생각해보면 보안상에 문제가 될 수 있다는 사실이 새롭게 다가왔다.  
또한 Facade 패턴은 기본적으로 간단한 인터페이스를 제공하는 심플한 디자인 패턴이었는데, 이런 방식으로 문제해결에 도움이 될 수 있다는 사실을
알 수 있었던 유익한 실습이었다.

## Chapter3

### Connector 모듈 구현

책에서 설명하는 Tomcat의 주요가 되는 컴포넌트인 Connector - Container 중 하나인 Connector를 조금 더 자세하게 구현하였다.  
정리하자면 Connector의 주요 역활은 Client의 요청이 들어오면 HttpRequest와 HttpResponse를 생성하여,
Container에게 passing하는 것이다.
해당 과정에서 uri를 처리하는 상세한 과정을 경험해볼 수 있었고, 그 과정 속에서 jsessionid의 처리부분을 구현해볼 수 있었다.  
  
servlet container에서 session 관리를 위해서 사용하는 sessionid가 jsessionid인데, 이 id를 처음에는 cookie와 fat-url로 동시에 전달하지만
이후 요청에서 cookie가 클라이언트에서 제공되는 경우에는 fat-url을 사용하지 않게 된다. 이때, 이러한 맥락을 어떻게 파악하는지, 그 과정이 궁금했었는데
request.setRequestSessionURL의 boolean값이 true인 경우에 클라이언트가 fat-url로 접속함을 알 수 있으므로, cookie로 jsessionid가 도달했는지를
파악 함으로써 session을 cookie로 관리해도 될지 결정할 수 있음을 알 수 있다.  
  
비록 setRequestSessionURL로 설정된 boolean값과 cookie를 통해서 의사결정을 최종적으로 내리는 컴포넌트는 connector가 아닌 container가 아닐까 생각된다.
(미리 본 chapter4에서 container가 하는 역활 중, manage session이 존재했다.) 따라서 이러한 의사 결정 코드는 보지 못했지만, 이러한 과정의 일부로써
connector가 참여하는 것을 구체적 코드로 경험하는 것 만으로도 chapter 3가 유익했다고 본다.

## Chapter 4

Chapter 4의 내용을 간략하게 정리하자면 다음과 같다.

- 여러개의 클라이언트 요청을 동시에 처리하기 위해서 HttpProcessor를 비동기적으로 사용하는 방법
- tomcat이 Persistence Connection(Connection: Keep-Alive 헤더)를 지원하는 방법
- Request&Response 객체들의 클래스 다이어그램(구조)

먼저 HttpProcessor를 비동기적으로 활용하는 코드 구현 파트에서는 Connection 모듈에서 Processor의 pool을 생성하고 이를 재사용하는 코드를 직접 구현해 볼 수
있었다. 또한 여러개의 processor가 하나의 Container를 사용하게 만드는 부분 또한 서블릿 컨테이너의 구조를 한층 더 이해할 수 있도록 만드는 부분이라고 
생각한다. BootStrap 코드에서 Connector와 Container를 모두 생성하여 Connector에 Container를 제공하고, Connector가 이를 여러개의 processor에 
할당함으로써 request&response를 해석하고 초기화하는 processor는 여러개의 인스턴스가 존재하지만, 이를 사용하는 Container의 인스턴스는
하나로 관리한다는 부분들을 파악할 수 있었다.  
이렇게 container가 하나이기 때문에 container가 관리하는 servlet 인스턴스가 하나로 유지될 수 있다는 점도 파악할 수 있다. (비록 현재는 요청마다
인스턴스를 생성하지만, 이 또한 추후 챕터에서 개선될 것이라고 예측할 수 있는 부분이다.)

persistence connection을 구현하는 부분은 책에 자세히 설명되어 있지 않았던 만큼, 여러가지의 시행착오를 겪은 부분이다.  
문제는 "하나의 tcp connection으로 오는 여러개의 메시지를 어떻게 구분할 것인가?" 였다.
직접 구현을 통해서 알아보니, 요청이 동일한 socket에, 비연속적으로 오는 경우에는 java의 inputstream이 시작을 인식할 수 있으므로 구분할 수 있으며,
연속적으로 오는 경우에는 content-length 헤더를 통해서 구분해야 한다.
추가적으로 Connection: Keep-Alive와 Keep-Alive: timeout=5 헤더를 통해서 여러개의 소켓이 아닌 하나의 소켓으로 요청을 처리할 수 있도록 하는
코드를 작성하여, 이에 대한 동작을 확인했고, 반대로 Connection: close를 통해서 무조건 하나의 요청마다 다른 processor를 사용하도록 강제하는
코드 또한 구현하여 이를 확인해 보았다.

추가적으로 PersistenceConnectionTest가 성공과 실패를 반복한다. 사실 이는 네트워크의 문제인지, inputstream에 대한 이해가 부족한건지, 
아니면 구현이 잘못된것인지 알수는 없으나, Keep-Alive 커넥션을 사용하는 경우에, 예상치 못한 상황에 대해서 Server가 대응하는 코드가 필요하다는 것을
알 수 있다. (사실이는 모든 네트워크 관련 코드에 해당한다고 생각한다.)
추가: SocketTest와 PersistentInputStream 클래스를 통해서 다시한번 확인해 봤다. 크롬 브라우저에서 처음 접속하는 경우에는 일부 리소스가 누락되는
경우가 발생하나, 새로고침을 통해 접속하면 빠짐없이 리소스가 전달된다. 적어도 코드 내부의 문제는 아니지 않을까... 생각해본다.
 
## Chapter 5

Chapter 5에서는 다음과 같은 내용을 공부했다.

- Container를 구성하는 4개의 컴포넌트 (Engine, Host, Context, Wrapper)의 역활
- Pipeline과 Valve를 이용한 Container가 task를 처리하는 방식
- Context와 Wrapper의 Parent-Child 관계와 Mapper를 통해서 요청을 처리할 Wrapper 매핑

첫번째로 Container를 구성하는 각 컴포넌트들이 각자, Container 인터페이스를 구현하는 컴포지트 패턴이 구현된 사례를 체험할 수 있었다. 실습 예제가
먼저 최상위 컨테이너가 Wrapper인 application, 즉 servlet이 1개인 container를 구현하다가, 최상위 컨테이너가 Context인 Application을 구현하는
방식으로 실습을 진행하였는데, 이를 통해서 최상위 container의 구현이 wrapper에서 context로 변경되었음에도, connector가 container를 사용하는
방법에 있어서 변하지 않았다는 점에서 설계상의 이점을 알 수 있었다. (bootstrap에서 connection.setContainer를 호출하는 부분)  

두번째로 container의 task를 pipeline과 valve를 통해서 처리하는 부분을 구현하였다. 이렇게 구현한 설계적 이점에 대해서 책에서 설명하지 않았지만,
개인적으로 생각해본 설계적 이점은 다음과 같다.
1. 각각의 컴포넌트(Engine, Host, Context, Wrapper)가 task를 처리하기 위한 독립적인 인터페이스를 갖는것은 algorithm을 한곳에서 관리할 수 없다.
2. 그렇다고 Container 인터페이스에 task를 처리하기 위한 메소드를 추가하는 것은 처리된 request와 response를 알맞은 servlet으로 mapping 해준다는
container의 유일한 책임에 부합하지 않으므로, 독립적인 인터페이스(클래스)를 갖는것이 설계상으로 유리하다.  
-> 따라서 Pipeline 인터페이스를 독립적으로 가져야 한다. (사실 이부분은 Mapper나 Loader와 동일하게 여러가지 책임을 분리하는 것이다)
3. 또한 Valve를 이용해서 각 task가 컴포넌트의 종류에 의존하지 않도록 구성하였다. 이를 통해서 각 Valve를 재사용하기 쉬워졌다.
(이는 실습상에서 경험할 수 있는 부분인데, 두개의 LoggerValve가 처음에는 Wrapper의 Valve였지만, 
Context가 구현된 후에는 Context의 Valve로 pipeline에 추가되었다)
4. Valve를 통해서 실제 task가 구현되는 부분을 분리함으로써, task의 실행 과정(순서나 모든 task들이 실행될 수 있도록 하는 알고리즘)을 관리하는
pipeline의 책임을 단일화 하였다. 이로인해 각 pipeline과 valve가 재사용 가능해졌다.
사실 Filter와 다르게 Valve는 tomcat을 구현하는 개발자들을 위한 것이니 만큼, 이를 사용하는 사용처는 사실 잘 모르겠는 부분이다. 추후 이 부분에
대해서도 알 수 있길 희망하는 바이다.

사실 Mapper를 tomcat4를 마지막으로 사용하지 않는다고 나와있는데, 현재 tomcat10이 나와있는 만큼, 이부분이 얼마나 바뀌었는지는 사실 모르지만,
기본적인 원리는 비슷할것이라고 생각한다.(컴포넌트 구조가 변하지 않는다면)  
Context가 여러개의 wrapper중 하나를 선택하고, Wrapper가 해당하는 Servlet에 대한 Wrapper라는, 컴포넌트의 목적이 그대로라면 어떤 mapping 방법을
사용하든 Context가 Wrapper를 child로써 선택하는 것은 동일할 것이다. (최근의 Container 인터페이스를 살펴봤는데, child와 parent 메소드가
존재하는 것 보니 구조는 비슷할 듯 하다)  
다른 실습에서 간단하게 servlet container를 구현할때는 url과 servlet을 직접적으로 매핑하는 방식을 사용했는데, 실질적인 구조는 개인적으로 많이
다름을 깨달을 수 있어서 의미있는 경험이라고 생각한다.  

간단하게 구현한 container와 실제 container 구조가 다른것은(context와 wrapper가 존재하는 측면에서) 현재까지 구현으로 봤을 떄, loader, mapper,
pipeline, valve등, 간단한 container에서는 구현할 필요가 없는 추가적인 기능을 구현해야 하기 때문이다. 특히 개인적으로 Valve가 큰
부분을 차지한다고 본다. tomcat github를 보니 session관리(PersistenceValve)와 Filter관리 (FilterValve)등이 Valve로 구현되어 있었다.
이렇게 Valve와 같이 task를 처리하는 부분들을 별도의 클래스로 처리하는 방식을 추후 나의 코드에서도 적용해볼수있지 않을까 하는 생각이 든다.
(물론 Valve와 같이 사용하려면 invoke메소드, 즉 task를 처리하기 위해 주어지는 인자가 동일해야하는 제한이 존재하지만 말이다)

## Chapter 6

chapter 6에서는 Lifecycle에 대해서 공부한다.

Lifecycle의 이점은 다음과 같다
1. 계층구조를 통하여 최상위 컴포넌트의 start/stop(lifecycle중 하나)를 통해서 모든 하위 컴포넌트의 start/stop을 트리거한다.
2. init/destroy 의 일반화
3. 2번으로 부터 얻어지는 listener 등록의 일반화

제일 흥미로웠던 부분은 1번이라고 생각하는데, start 코드를 보면 child 컴포넌트와, loader/pipeline과 같이 연관된 컴포넌트의 start를 
트리거하는 부분을 확인할 수 있다.
추가적으로 자식 컴포넌트의 start가 완료된 뒤에 부모 컴포넌트의 start_event가 발생하는 부분을 확인할 수 있다.

톰캣의 컴포넌트의 초기화 프로세스 흐름을 파악할 수 있는 유용한 챕터라고 생각한다.