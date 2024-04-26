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



