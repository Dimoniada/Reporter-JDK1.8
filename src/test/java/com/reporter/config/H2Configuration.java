package com.reporter.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

//@Configuration
//@EnableJpaRepositories(
//    basePackages = "com.reporter.db.repositories.h2",
//    entityManagerFactoryRef = "h2EntityManager",
//    transactionManagerRef = "h2TransactionManager"
//)
public class H2Configuration {

//    @Autowired
    private Environment env;

//    @Bean(name = "embeddedDatabaseH2")
    public DataSource embeddedDatabaseH2() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }

//    @Bean(name = "jdbcTemplateH2")
    public NamedParameterJdbcTemplate jdbcTemplateH2(@Qualifier("embeddedDatabaseH2") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

//    @Bean
    public PlatformTransactionManager h2TransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager
            .setEntityManagerFactory(
                h2EntityManager().getObject()
            );
        return transactionManager;
    }

//    @Bean
    public LocalContainerEntityManagerFactoryBean h2EntityManager() {

        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(embeddedDatabaseH2());
        em.setPackagesToScan("com.reporter.db.repositories.h2");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        final Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));

        em.setJpaPropertyMap(properties);
        return em;
    }
}
