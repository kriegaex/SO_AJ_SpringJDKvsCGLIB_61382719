package de.scrum_master.spring;

import org.springframework.stereotype.Component;

@Component
public class FooImplChild extends FooImpl {
  public void doEvenMore() {
    System.out.println("Doing even more in subclass");
  }

  @Override
  public void doSomething(int i) {
    System.out.println("[Override] Doing something in sub class");
  }
}
