package org.eclipse.persistence.jpa.test.canonical;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.sessions.Session;
import org.junit.Test;

public class TestCanonicalMetaModel {

    @Test
    public void testUseCanonicalModelFieldName() {

        PersistenceProvider persistenceProvider = new PersistenceProvider();
        SEPersistenceUnitInfo persistenceUnitInfo = new SEPersistenceUnitInfo();

        // For ASM to do its work creating metaclasses
        persistenceUnitInfo.setClassLoader(Thread.currentThread().getContextClassLoader());

        // We only programmatically add 'leaf' domain classes to pui
        persistenceUnitInfo.setManagedClassNames(Arrays.asList(DomainClass.class.getName()));
        persistenceUnitInfo.setPersistenceUnitName("canonical-test");
        persistenceUnitInfo.setPersistenceUnitRootUrl(DomainClass.class.getProtectionDomain()
                .getCodeSource().getLocation());

        Properties properties = new Properties();
        readProperty(properties, "javax.persistence.jdbc.driver");
        readProperty(properties, "javax.persistence.jdbc.url");
        readProperty(properties, "javax.persistence.jdbc.user");
        readProperty(properties, "javax.persistence.jdbc.password");

        // In our actual application we have a generic session customizer that binds a number
        // of delegate session customizers in a 'logical' order - for this example however we
        // only need to have this 'test' one - see below class
        properties.setProperty("eclipselink.session.customizer", CanonicalMetaModelCustomizer.class.getName());

        try (AutoCloseableEntityManagerFactory acemf = new AutoCloseableEntityManagerFactory(persistenceProvider,
                persistenceUnitInfo, properties)) {
            assertThat("The \"name\" field is properly initialized", DomainInterface_.name, notNullValue());
        }

    }

    private void readProperty(Properties properties, String key) {

        properties.setProperty(key, System.getProperty(key));
    }

    /**
     * What it is doing is that it will be providing the link between an (impl) 'hidden' persistable and an
     * api (exposed) canonical metamodel class.
     */
    public static class CanonicalMetaModelCustomizer implements SessionCustomizer {

        @Override
        public void customize(Session session) throws Exception {

            AbstractSession.class.cast(session).addStaticMetamodelClass(
                    DomainPersistable.class.getName(), DomainInterface_.class.getName());
        }
    }

    /**
     * Provides some small cleanup test helper facility.
     */
    static class AutoCloseableEntityManagerFactory implements AutoCloseable {

        private final EntityManagerFactory entityManagerFactory;
        private final EntityManager firstEntityManager;

        AutoCloseableEntityManagerFactory(PersistenceProvider persistenceProvider,
                SEPersistenceUnitInfo persistenceUnitInfo, Properties properties) {

            entityManagerFactory = persistenceProvider.createContainerEntityManagerFactory(persistenceUnitInfo, properties);
            firstEntityManager = entityManagerFactory.createEntityManager();
        }

        @Override
        public void close() {

            if (firstEntityManager != null) {
                firstEntityManager.close();
            }

            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }
}
