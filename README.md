# Spring demo for JDK vs. CGLIB proxy usage

This project demonstrates how Spring behaves differently depending on the proxy mode configured for Spring AOP:
  * `@EnableAspectJAutoProxy` - use JDK proxies for components implementing interfaces, CGLIB proxies for components not
    implementing any interfaces
  * `@EnableAspectJAutoProxy(proxyTargetClass = true)` - always use CGLIB proxies, no matter if components implement
    any interfaces or not

Due to restrictive pre-configuration, Spring Boot currently always runs in CGLIB mode, see
[issue #12194](https://github.com/spring-projects/spring-boot/issues/12194). So this effect has to be demonstrated in a
classical Spring setup not using Spring Boot or any of its dependencies.

I used this example project to answer the following Stack Overflow questions:
  * https://stackoverflow.com/a/61390594/1082681
  * https://stackoverflow.com/a/70491009/1082681

Feel free to inspect this code base including the Maven POM. Just run the `Application` class with or without a command
line parameter containing `CGLIB` (case-insensitive) in order to switch between both modes (JDK proxy mode is the
default).

Here is sample some output.

## JDK mode

```text
Bean class: com.sun.proxy.$Proxy19
Proxy type: JDK
execution(void de.scrum_master.spring.app.IFoo.doSomething(int))
Doing something in base class

----------------------------------------

Bean class: com.sun.proxy.$Proxy19
Proxy type: JDK
execution(void de.scrum_master.spring.app.IFoo.doSomething(int))
[Override] Doing something in sub class

----------------------------------------

Bean class: de.scrum_master.spring.app.WithoutInterface$$EnhancerBySpringCGLIB$$df0e80e7
Proxy type: CGLIB
execution(void de.scrum_master.spring.app.WithoutInterface.doSomething(int))
Doing something in class without interface
execution(void de.scrum_master.spring.app.WithoutInterface.doSomethingElse())
Doing something else in class without interface
execution(int de.scrum_master.spring.app.WithoutInterface.calculateSomething(int,int))
Calculating something in class without interface

----------------------------------------

java.lang.ClassCastException: com.sun.proxy.$Proxy19 cannot be cast to de.scrum_master.spring.app.FooImpl
  at de.scrum_master.spring.app.Application.useClassLogic(Application.java:51)
  at de.scrum_master.spring.app.Application.main(Application.java:19)

----------------------------------------

java.lang.ClassCastException: com.sun.proxy.$Proxy19 cannot be cast to de.scrum_master.spring.app.FooImplChild
  at de.scrum_master.spring.app.Application.useClassLogic(Application.java:63)
  at de.scrum_master.spring.app.Application.main(Application.java:19)
```

Please note:
  * Bean class `com.sun.proxy.$Proxy19` proves that for classes implementing interfaces Spring AOP creates JDK proxies.
  * Bean class `WithoutInterface$$EnhancerBySpringCGLIB$$df0e80e7` shows that CGLIB proxies will be created if the
    target class does not implement any interface. 
  * The `ClassCastException` shows that you cannot cast a JDK proxy to anything else but ones of its implemented
    interface types. This is a JDK proxy limitation because JDK proxies only work with interfaces. Consequently, the
    proxy is not a subclass of the bean class it is created for but an anonymous interface implementation. It can only
    proxy interface methods even though the delegate is an instance of a concrete class with possibly more methods. But
    unless you expose the proxy and call its methods directly, you are limited to interface methods generally,
    specifically also in the context of Spring AOP.
    
## CGLIB mode

```text
Bean class: de.scrum_master.spring.app.FooImpl$$EnhancerBySpringCGLIB$$b6e1c863
Proxy type: CGLIB
execution(void de.scrum_master.spring.app.FooImpl.doSomething(int))
Doing something in base class

----------------------------------------

Bean class: de.scrum_master.spring.app.FooImplChild$$EnhancerBySpringCGLIB$$2b7ba0ab
Proxy type: CGLIB
execution(void de.scrum_master.spring.app.FooImplChild.doSomething(int))
[Override] Doing something in sub class

----------------------------------------

Bean class: de.scrum_master.spring.app.WithoutInterface$$EnhancerBySpringCGLIB$$d793f95c
Proxy type: CGLIB
execution(void de.scrum_master.spring.app.WithoutInterface.doSomething(int))
Doing something in class without interface
execution(void de.scrum_master.spring.app.WithoutInterface.doSomethingElse())
Doing something else in class without interface
execution(int de.scrum_master.spring.app.WithoutInterface.calculateSomething(int,int))
Calculating something in class without interface

----------------------------------------

Bean class: de.scrum_master.spring.app.FooImpl$$EnhancerBySpringCGLIB$$b6e1c863
Proxy type: CGLIB
execution(void de.scrum_master.spring.app.FooImpl.doSomething(int))
Doing something in base class
execution(void de.scrum_master.spring.app.FooImpl.doSomethingElse())
Doing something else in base class
execution(int de.scrum_master.spring.app.FooImpl.calculateSomething(int,int))
Calculating something in base class

----------------------------------------

Bean class: de.scrum_master.spring.app.FooImplChild$$EnhancerBySpringCGLIB$$2b7ba0ab
Proxy type: CGLIB
execution(void de.scrum_master.spring.app.FooImplChild.doSomething(int))
[Override] Doing something in sub class
execution(void de.scrum_master.spring.app.FooImpl.doSomethingElse())
Doing something else in base class
execution(int de.scrum_master.spring.app.FooImpl.calculateSomething(int,int))
Calculating something in base class
execution(void de.scrum_master.spring.app.FooImplChild.doEvenMore())
Doing even more in subclass
```

Please note:
  * Now we always see bean classes like `MyClassName$$EnhancerBySpringCGLIB$$b6e1c863`, i.e. Spring creates CGLIB
    proxies in all cases. 
  * Because the CGLIB proxies are direct subclasses of their delegate objects, we can now cast the beans to those
    classes and call all of their respective methods. At the end of the console log there are no more
    `ClassCastException`s.
