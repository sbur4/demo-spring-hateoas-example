package org.example.data;

import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
@Component
public class DatabaseInitializer {
    @Autowired
    private DataSource dataSource;

    //    @PersistenceContext
//    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeDatabase() {
        createUserByDataSource("John Doe");
        createUserByJPA("Tester");
        createUserByHibernate("Hello");
        createUserByJDBCTemplate("Abc");
    }

    private void createUserByDataSource(String name) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name) VALUES (?)")) {
            stmt.setString(1, name);
            stmt.execute();
            log.debug("User created via DataSource: {}", name);
        } catch (SQLException e) {
            log.error("Error creating user via DataSource", e);
        }
    }

    @Transactional
    private void createUserByJPA(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(new User(name));
            entityManager.getTransaction().commit();
            log.debug("User created via EntityManager: {}", name);
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.error("Error creating user via EntityManager", e);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private void createUserByHibernate(String name) {
        try (SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(new User(name));
            session.getTransaction().commit();
            log.debug("User created via Hibernate: {}", name);
        } catch (Exception e) {
            log.error("Error creating user via Hibernate", e);
        }
    }

    private void createUserByJDBCTemplate(String name) {
        try {
            String sql = "INSERT INTO users (name) VALUES (?)";
            jdbcTemplate.update(sql, name);
            log.debug("User created via JdbcTemplate: {}", name);
        } catch (Exception e) {
            log.error("Error creating user via JdbcTemplate", e);
        }
    }
}
