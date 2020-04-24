package de.scrum_master.spring.config;

import org.springframework.context.annotation.*;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(value = "de.scrum_master.spring.app")
public class JdkProxyConfig {}
