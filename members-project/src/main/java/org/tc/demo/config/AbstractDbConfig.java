package org.tc.demo.config;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

public abstract class AbstractDbConfig {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(AbstractDbConfig.class);

  @Autowired
  protected Environment _env;

  @Bean(name = "datasource")
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    
    dataSource.setDriverClassName(_env.getRequiredProperty("datasource.driverClassName"));
    dataSource.setUrl(_env.getRequiredProperty("datasource.url"));
    dataSource.setUsername(_env.getRequiredProperty("datasource.username"));
    dataSource.setPassword(_env.getRequiredProperty("datasource.password"));

    log.info("Connect to DB: " + dataSource.getUrl());

    return dataSource;
  }

  @Bean(name = "entityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {

    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
    
    entityManagerFactoryBean.setDataSource(dataSource);
    entityManagerFactoryBean.setPackagesToScan(new String[] { AppPackages.DB_ENTITIES });
    entityManagerFactoryBean.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
    entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

    Map<String, Object> jpaProperties = Arrays
        .stream(new String[] { "hibernate.hbm2ddl.auto", "hibernate.show_sql", "hibernate.format_sql",
            "hibernate.use_sql_comments", "hibernate.dialect" })
        .collect(Collectors.toMap(s -> s, s -> (String) _env.getRequiredProperty(s)));
    entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);

    return entityManagerFactoryBean;
  }
}
