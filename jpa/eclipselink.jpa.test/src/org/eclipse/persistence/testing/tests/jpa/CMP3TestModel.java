/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.TestModel;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;

/**
 * <p><b>Purpose</b>: Base class for CMP3 Test Models.
 */
public class CMP3TestModel extends TestModel {
    public static EntityManager entityManager;
    public static boolean shouldLogoutOnReset = true;
    protected static Map originalProperties;

    public void setup() {
        setup(getSession());
    }
    
    public static void setup(Session originalSession) {
        DatabaseLogin systemLogin = (DatabaseLogin)originalSession.getDatasourceLogin();
        Map currentMap = new HashMap();
        currentMap.put(PersistenceUnitProperties.JDBC_DRIVER, systemLogin.getDriverClassName());
        currentMap.put(PersistenceUnitProperties.JDBC_URL, systemLogin.getConnectionString());
        currentMap.put(PersistenceUnitProperties.JDBC_USER, systemLogin.getUserName());
        currentMap.put(PersistenceUnitProperties.JDBC_PASSWORD, systemLogin.getPassword());
// let's use platform Auto detection 
//        currentMap.put(PersistenceUnitProperties.TARGET_DATABASE, systemLogin.getPlatform().getClass().getName());
        currentMap.put(PersistenceUnitProperties.LOGGING_LEVEL, AbstractSessionLog.translateLoggingLevelToString(originalSession.getSessionLog().getLevel()));
        // this property added to test bug fix to 221226
        currentMap.put(PersistenceUnitProperties.CLASSLOADER, systemLogin.getClass().getClassLoader());  
        currentMap.putAll(JUnitTestCaseHelper.persistencePropertiesTestMap);

        if (!currentMap.equals(JUnitTestCaseHelper.propertiesMap)) {
            originalProperties = JUnitTestCaseHelper.propertiesMap;
            JUnitTestCaseHelper.propertiesMap = currentMap;
            JUnitTestCase.closeEntityManagerFactory("default");
            entityManager = null;
        }
    }
    
    public void reset() {
        reset(getSession());
    }

    public static void reset(Session originalSession) {
        if (shouldLogoutOnReset) {
            JUnitTestCase.closeEntityManagerFactory("default");
            entityManager = null;
        }
        if (originalProperties != null) {
            JUnitTestCaseHelper.propertiesMap = originalProperties;
        }
    }

    public static EntityManager getEntityManager() {
        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = createEntityManager();
        }
        return entityManager;
    }

    public static void closeAndRemoveEntityManager() {
        if(entityManager != null) {
            if(entityManager.isOpen()) {
                entityManager.close();
            }
            entityManager = null;
        }
    }

    public static EntityManager createEntityManager() {
        entityManager = JUnitTestCase.createEntityManager("default");
        return entityManager;
    }

    public static ServerSession getServerSession() {
        return ((EntityManagerImpl)getEntityManager()).getServerSession();
    }
}
