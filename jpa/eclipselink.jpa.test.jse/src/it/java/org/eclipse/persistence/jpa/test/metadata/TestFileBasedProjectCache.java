package org.eclipse.persistence.jpa.test.metadata;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.metadata.FileBasedProjectCache;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.metadata.model.SimpleMetadataEntity;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestFileBasedProjectCache {

    // Cache file with serialized Project class
    private static final String FILE_NAME = "TestFileBasedProjectCache.file";

    @Emf(
            name="TestFileBasedProjectCache", createTables = DDLGen.DROP_CREATE, classes = {SimpleMetadataEntity.class},
            properties = {
                    // This causes deployment to occur on refreshMetadata rather than wait until an em is obtained
                    @Property(name = PersistenceUnitProperties.DEPLOY_ON_STARTUP, value = "true"),
                    // Properties we are testing:
                    @Property(name = PersistenceUnitProperties.PROJECT_CACHE, value = "java-serialization"),
                    @Property(name = PersistenceUnitProperties.PROJECT_CACHE_FILE, value = FILE_NAME)
    })
    private EntityManagerFactory emf;

    @AfterClass
    public static void cleanup() {
        // Remove cache file after tests are done.
        final File file = new File(FILE_NAME);
        if (file.exists()) {
            if (!file.delete()) {
                // Log warning if there is a problem with cache file removal.
                AbstractSessionLog.getLog().log(
                        SessionLog.WARNING,
                        String.format("Cache file %s with serialized Project class could not be removed", FILE_NAME));
            }
        }
    }

    private Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
        // This causes deployment to occur on refreshMetadata rather than wait until an em is obtained
        properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
        // Properties we are testing:
        properties.put(PersistenceUnitProperties.PROJECT_CACHE, "java-serialization");
        properties.put(PersistenceUnitProperties.PROJECT_CACHE_FILE, FILE_NAME);
        return properties;
    }

    @Test
    public void test() {
        final EntityManager em = emf.createEntityManager();
        try {
            FileBasedProjectCache projectCache = new FileBasedProjectCache();
            Session session = em.unwrap(ServerSession.class);
            Project project = projectCache.retrieveProject(getProperties(), session.getDatasourcePlatform().getConversionManager().getLoader(), session.getSessionLog());
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }

    }

}
