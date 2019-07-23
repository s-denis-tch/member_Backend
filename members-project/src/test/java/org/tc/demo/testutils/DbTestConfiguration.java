package org.tc.demo.testutils;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tc.demo.config.AbstractDbConfig;
import org.tc.demo.config.AppPackages;

@Configuration
@Profile("test")
@ComponentScan({ AppPackages.ROOT })
public class DbTestConfiguration extends AbstractDbConfig {

}
