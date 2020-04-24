package de.scrum_master.spring;

import org.springframework.stereotype.Component;

@Component
public class FooImpl implements IFoo {
  @Override
  public void doSomething(int i) {
    System.out.println("Doing something in base class");
  }

  public void doSomethingElse() {
    System.out.println("Doing something else in base class");
  }

  public int calculateSomething(int a, int b) {
    System.out.println("Calculating something in base class");
    return a + b;
  }
}
