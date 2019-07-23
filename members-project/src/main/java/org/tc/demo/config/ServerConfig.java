package org.tc.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("server")
public class ServerConfig extends AbstractDbConfig {

}
