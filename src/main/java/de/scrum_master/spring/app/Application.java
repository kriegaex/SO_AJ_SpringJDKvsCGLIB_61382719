package de.scrum_master.spring.app;

import de.scrum_master.spring.config.CglibPoxyConfig;
import de.scrum_master.spring.config.JdkProxyConfig;
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
    System.out.println("Bean class = " + iFoo.getClass());
    iFoo.doSomething(11);
    printSeparator();

    iFoo = (IFoo) context.getBean("fooImplChild");
    System.out.println("Bean class = " + iFoo.getClass());
    iFoo.doSomething(11);
    printSeparator();

  }

  private static void useClassWithoutInterface(ApplicationContext context) {
    WithoutInterface withoutInterface = (WithoutInterface) context.getBean("withoutInterface");
    System.out.println("Bean class = " + withoutInterface.getClass());
    withoutInterface.doSomething(11);
    withoutInterface.doSomethingElse();
    withoutInterface.calculateSomething(22, 33);
    printSeparator();
  }

  /**
   * This method only runs correctly in CGLIB proxy mode.
   *
   * @throws ClassCastException when trying to cast a JDK proxy to a non-interface type
   */
  private static void useClassLogic(ApplicationContext context) throws ClassCastException {
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
