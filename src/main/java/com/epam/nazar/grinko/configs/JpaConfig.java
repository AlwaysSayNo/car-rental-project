package com.epam.nazar.grinko.configs;

import com.epam.nazar.grinko.constants.DatabaseConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.core.env.Environment;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
@PropertySource("classpath:META-INF/my-application.properties")
public class JpaConfig {

    private final Environment environment;

    public JpaConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(){
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource());
        entityManager.setPackagesToScan(environment.getProperty(DatabaseConstants.ENTITY_MANAGER_PACKAGES_TO_SCAN));
        entityManager.setJpaVendorAdapter(jpaVendorAdapter());
        entityManager.setJpaProperties(jpaProperties());
        return entityManager;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(
                environment.getProperty(DatabaseConstants.SOURCE_DRIVER_CLASS_NAME))
        );
        dataSource.setUrl(environment.getProperty(DatabaseConstants.SOURCE_URL));
        dataSource.setUsername(environment.getProperty(DatabaseConstants.SOURCE_USERNAME));
        dataSource.setPassword(environment.getProperty(DatabaseConstants.SOURCE_PASSWORD));
        return dataSource;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    // property pairs we define to customize JPA-Hibernate
    @Bean
    public Properties jpaProperties() {
        Properties properties = new Properties();
        properties.setProperty(DatabaseConstants.HIBERNATE_FORMAT_SQL,
                environment.getProperty(DatabaseConstants.HIBERNATE_FORMAT_SQL));
        properties.setProperty(DatabaseConstants.HIBERNATE_SHOW_SQL,
                environment.getProperty(DatabaseConstants.HIBERNATE_SHOW_SQL));
        properties.setProperty(DatabaseConstants.HIBERNATE_HBM2DDL_AUTO,
                environment.getProperty(DatabaseConstants.HIBERNATE_HBM2DDL_AUTO));
        properties.setProperty(DatabaseConstants.HIBERNATE_DIALECT,
                environment.getProperty(DatabaseConstants.HIBERNATE_DIALECT));
        return properties;
    }


    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }


}
