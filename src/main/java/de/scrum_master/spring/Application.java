package de.scrum_master.spring;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

public class Application {
  public static void main(String[] args) {
    try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class)) {
      useInterfaceLogic(context);
      useClassWithoutInterface(context);
      if (isCglibMode())
        useClassLogic(context);
    }
  }

  private static void useInterfaceLogic(ConfigurableApplicationContext context) {
    IFoo iFoo = (IFoo) context.getBean("fooImpl");
    System.out.println("Bean class = " + iFoo.getClass());
    iFoo.doSomething(11);
    printSeparator();

    iFoo = (IFoo) context.getBean("fooImplChild");
    System.out.println("Bean class = " + iFoo.getClass());
    iFoo.doSomething(11);
    printSeparator();

  }

  private static void useClassWithoutInterface(ConfigurableApplicationContext context) {
    WithoutInterface withoutInterface = (WithoutInterface) context.getBean("withoutInterface");
    System.out.println("Bean class = " + withoutInterface.getClass());
    withoutInterface.doSomething(11);
    withoutInterface.doSomethingElse();
    withoutInterface.calculateSomething(22, 33);
    printSeparator();
  }

  private static boolean isCglibMode() {
    return Config.class
      .getDeclaredAnnotation(EnableAspectJAutoProxy.class)
      .proxyTargetClass();
  }

  /**
   * This class only runs correctly in CGLIB proxy mode.
   * If you run it in JDK proxy mode, you will see exceptions like:
   * <p></p>
   * <code>{@code
   * java.lang.ClassCastException: com.sun.proxy.$Proxy19 cannot be cast to de.scrum_master.spring.FooImpl
   * }</code>
   */
  private static void useClassLogic(ConfigurableApplicationContext context) {
    FooImpl fooImpl = (FooImpl) context.getBean("fooImpl");
    System.out.println("Bean class = " + fooImpl.getClass());
    fooImpl.doSomething(11);
    fooImpl.doSomethingElse();
    fooImpl.calculateSomething(22, 33);
    printSeparator();

    FooImplChild fooImplChild = (FooImplChild) context.getBean("fooImplChild");
    System.out.println("Bean class = " + fooImplChild.getClass());
    fooImplChild.doSomething(11);
    fooImplChild.doSomethingElse();
    fooImplChild.calculateSomething(22, 33);
    fooImplChild.doEvenMore();
  }

  private static void printSeparator() {
    System.out.println("\n----------------------------------------\n");
  }
}
