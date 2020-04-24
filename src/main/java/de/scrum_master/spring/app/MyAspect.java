package de.scrum_master.spring.app;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyAspect {
  @Around("execution(* de.scrum_master.spring..*(..))")
  public Object aspectProcess(ProceedingJoinPoint pjp) throws Throwable {
    System.out.println(pjp);
    return pjp.proceed();
  }
}
