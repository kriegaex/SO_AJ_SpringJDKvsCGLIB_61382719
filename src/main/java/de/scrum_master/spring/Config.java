package de.scrum_master.spring;

import org.springframework.context.annotation.*;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = false) // Use 'true' in order to always use CGLIB proxies
@ComponentScan("de.scrum_master.spring")
public class Config {}
