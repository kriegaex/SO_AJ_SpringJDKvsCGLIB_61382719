package de.scrum_master.spring;

import org.springframework.stereotype.Component;

@Component
public class WithoutInterface {
  public void doSomething(int i) {
    System.out.println("Doing something in class without interface");
  }

  public void doSomethingElse() {
    System.out.println("Doing something else in class without interface");
  }

  public int calculateSomething(int a, int b) {
    System.out.println("Calculating something in class without interface");
    return a + b;
  }
}
