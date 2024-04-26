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


