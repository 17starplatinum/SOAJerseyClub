package ru.itmo.cs.dandadan.config;

import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import ru.itmo.cs.dandadan.model.entity.Car;
import ru.itmo.cs.dandadan.model.entity.Coordinates;
import ru.itmo.cs.dandadan.model.entity.HumanBeing;

@NoArgsConstructor
public class HibernateSessionFactoryConfig {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                String dbUser = System.getenv("DB_USERNAME");
                if (dbUser == null) dbUser = "sXXXXXX";

                String dbPass = System.getenv("DB_PASSWORD");
                if (dbPass == null) dbPass = "*****************";

                configuration.setProperty("hibernate.connection.username", dbUser);
                configuration.setProperty("hibernate.connection.password", dbPass);
                configuration.addAnnotatedClass(Coordinates.class);
                configuration.addAnnotatedClass(Car.class);
                configuration.addAnnotatedClass(HumanBeing.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sessionFactory;
    }
}
