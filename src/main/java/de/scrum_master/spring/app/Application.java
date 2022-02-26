package de.scrum_master.spring.app;

import de.scrum_master.spring.config.CglibPoxyConfig;
import de.scrum_master.spring.config.JdkProxyConfig;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
  public static void main(String[] args) {
    Class<?> configClass = JdkProxyConfig.class;
    if (args.length > 0 && args[0].toUpperCase().contains("CGLIB"))
      configClass = CglibPoxyConfig.class;

    try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(configClass)) {
      useInterfaceLogic(context);
      useClassWithoutInterface(context);
      useClassLogic(context);
    }
  }

  private static void useInterfaceLogic(ApplicationContext context) {
    IFoo iFoo = (IFoo) context.getBean("fooImpl");
    printBeanAndProxyInfos(iFoo);
    iFoo.doSomething(11);
    printSeparator();

    iFoo = (IFoo) context.getBean("fooImplChild");
    printBeanAndProxyInfos(iFoo);
    iFoo.doSomething(11);
    printSeparator();

  }

  private static void useClassWithoutInterface(ApplicationContext context) {
    WithoutInterface withoutInterface = (WithoutInterface) context.getBean("withoutInterface");
    printBeanAndProxyInfos(withoutInterface);
    withoutInterface.doSomething(11);
    withoutInterface.doSomethingElse();
    withoutInterface.calculateSomething(22, 33);
    printSeparator();
  }

  /**
   * This method only runs correctly in CGLIB proxy mode. It is going to catch and log a {@link ClassCastException}
   * both times when trying to cast a JDK proxy to a non-interface type.
   */
  private static void useClassLogic(ApplicationContext context) throws ClassCastException {
    try {
      FooImpl fooImpl = (FooImpl) context.getBean("fooImpl");
      printBeanAndProxyInfos(fooImpl);
      fooImpl.doSomething(11);
      fooImpl.doSomethingElse();
      fooImpl.calculateSomething(22, 33);
    }
    catch (ClassCastException e) {
      e.printStackTrace(System.out);
    }
    printSeparator();

    try {
      FooImplChild fooImplChild = (FooImplChild) context.getBean("fooImplChild");
      printBeanAndProxyInfos(fooImplChild);
      fooImplChild.doSomething(11);
      fooImplChild.doSomethingElse();
      fooImplChild.calculateSomething(22, 33);
      fooImplChild.doEvenMore();
    }
    catch (ClassCastException e) {
      e.printStackTrace(System.out);
    }
  }

  private static void printBeanAndProxyInfos(Object bean) {
    boolean aopProxy = AopUtils.isAopProxy(bean);
    boolean jdkProxy = AopUtils.isJdkDynamicProxy(bean);
    boolean cglibProxy = AopUtils.isCglibProxy(bean);
    // Those two are not working for Spring proxies
    // cglibProxy = net.sf.cglib.proxy.Proxy.isProxyClass(bean.getClass());
    // cglibProxy = org.springframework.cglib.proxy.Proxy.isProxyClass(bean.getClass());

    String proxyType = aopProxy ? (jdkProxy ? "JDK" : (cglibProxy ? "CGLIB" : "unknown")) : "none";
    System.out.println("Bean class: " + bean.getClass().getName());
    System.out.println("Proxy type: " + proxyType);
  }

  private static void printSeparator() {
    System.out.println("\n----------------------------------------\n");
  }
}
