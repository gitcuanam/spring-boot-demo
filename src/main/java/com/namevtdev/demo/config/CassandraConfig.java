//package com.namevtdev.demo.config;
//import com.datastax.oss.driver.api.core.CqlSession;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
//import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
//
//@Configuration
//@EnableCassandraRepositories
//public class CassandraConfig extends AbstractCassandraConfiguration {
//
//    private static final Logger logger = LoggerFactory.getLogger(CassandraConfig.class);
//
//    @Override
//    protected String getKeyspaceName() {
//        return "mykeyspace";
//    }
//
//    @Bean
//    public CqlSession cqlSession() {
//        try {
//            return CqlSession.builder().build();
//        } catch (Exception e) {
//            // Log the exception using SLF4J
//            logger.error("Error creating Cassandra session", e);
//
//            // You can throw a custom exception or return a fallback value
//            // throw new MyCustomException("Error creating Cassandra session", e);
//            return null;
//        }
//    }
//}