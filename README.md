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


