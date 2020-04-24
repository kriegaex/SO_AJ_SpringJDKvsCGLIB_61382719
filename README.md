# Spring demo for JDK vs. CGLIB proxy usage

This project demonstrates how Spring behaves differently depending on the proxy mode configured for Spring AOP:
  * `@EnableAspectJAutoProxy` - use JDK proxies for components implementing interfaces, CGLIB proxies for components not
    implementing any interfaces
  * `@EnableAspectJAutoProxy(proxyTargetClass = true)` - always use CGLIB proxies, no matter if components implement
    any interfaces or not

Due to restrictive pre-configuration, Spring Boot currently always runs in CGLIB mode, see
[issue #12194](https://github.com/spring-projects/spring-boot/issues/12194). So this effect has to be demonstrated in a
classical Spring setup not using Spring Boot or any of its dependencies.

Feel free to inspect this code base including the Maven POM. Just run the `Application` class with or without a command
line parameter containing `CGLIB` (case-insensitive) in order to switch between both modes (JDK proxy mode is the
default).

Here is sample some output.

## JDK mode

```text
Bean class = class com.sun.proxy.$Proxy19
execution(void de.scrum_master.spring.app.IFoo.doSomething(int))
Doing something in base class

----------------------------------------

Bean class = class com.sun.proxy.$Proxy19
execution(void de.scrum_master.spring.app.IFoo.doSomething(int))
[Override] Doing something in sub class

----------------------------------------

Bean class = class de.scrum_master.spring.app.WithoutInterface$$EnhancerBySpringCGLIB$$a798682a
execution(void de.scrum_master.spring.app.WithoutInterface.doSomething(int))
Doing something in class without interface
execution(void de.scrum_master.spring.app.WithoutInterface.doSomethingElse())
Doing something else in class without interface
execution(int de.scrum_master.spring.app.WithoutInterface.calculateSomething(int,int))
Calculating something in class without interface

----------------------------------------

Exception in thread "main" java.lang.ClassCastException: com.sun.proxy.$Proxy19 cannot be cast to de.scrum_master.spring.app.FooImpl
    at de.scrum_master.spring.app.Application.useClassLogic(Application.java:50)
    at de.scrum_master.spring.app.Application.main(Application.java:18)
```

Please note:
  * Bean class `com.sun.proxy.$Proxy19` proves that for classes implementing interfaces Spring AOP creates JDK proxies.
  * Bean class `WithoutInterface$$EnhancerBySpringCGLIB$$a798682a` shows that CGLIB proxies will be created if the
    target class does not implement any interface. 
  * The `ClassCastException` shows that you cannot cast a JDK proxy to anything else but ones of its implemented
    interface types. This is a JDK proxy limitation because JDK proxies only work with interfaces. Consequently, the
    proxy is not a subclass of the bean class it is created for but an anonymous interface implementation. It can only
    proxy interface methods even though the delegate is an instance of a concrete class with possibly more methods. But
    unless you expose the proxy and call its methods directly, you are limited to interface methods generally,
    specifically also in the context of Spring AOP.
    
## CGLIB mode

```text
Bean class = class de.scrum_master.spring.app.FooImpl$$EnhancerBySpringCGLIB$$fd0a816c
execution(void de.scrum_master.spring.app.FooImpl.doSomething(int))
Doing something in base class

----------------------------------------

Bean class = class de.scrum_master.spring.app.FooImplChild$$EnhancerBySpringCGLIB$$71a459b4
execution(void de.scrum_master.spring.app.FooImplChild.doSomething(int))
[Override] Doing something in sub class

----------------------------------------

Bean class = class de.scrum_master.spring.app.WithoutInterface$$EnhancerBySpringCGLIB$$1dbcb265
execution(void de.scrum_master.spring.app.WithoutInterface.doSomething(int))
Doing something in class without interface
execution(void de.scrum_master.spring.app.WithoutInterface.doSomethingElse())
Doing something else in class without interface
execution(int de.scrum_master.spring.app.WithoutInterface.calculateSomething(int,int))
Calculating something in class without interface

----------------------------------------

Bean class = class de.scrum_master.spring.app.FooImpl$$EnhancerBySpringCGLIB$$fd0a816c
execution(void de.scrum_master.spring.app.FooImpl.doSomething(int))
Doing something in base class
execution(void de.scrum_master.spring.app.FooImpl.doSomethingElse())
Doing something else in base class
execution(int de.scrum_master.spring.app.FooImpl.calculateSomething(int,int))
Calculating something in base class

----------------------------------------

Bean class = class de.scrum_master.spring.app.FooImplChild$$EnhancerBySpringCGLIB$$71a459b4
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
  * Now we always see bean classes like `MyClassName$$EnhancerBySpringCGLIB$$a798682a`, i.e. Spring creates CGLIB
    proxies in all cases. 
  * Because the CGLIB proxies are direct subclasses of their delegate objects, we can now cast the beans to those
    classes and call all of their respective methods. At the end of the console log there are no more
    `ClassCastException`s.
