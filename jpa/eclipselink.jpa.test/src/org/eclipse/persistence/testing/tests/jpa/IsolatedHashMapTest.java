/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/11/2015-2.7 Tomas Kraus
 *       - Initial API and implementation.
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.spi.PersistenceUnitInfo;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.helper.JPAClassLoaderHolder;
import org.eclipse.persistence.internal.jpa.IsolatedHashMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.platform.server.ServerPlatformUtils;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.testing.framework.ReflectionHelper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * Tests partition isolated {@link HashMap}.
 */
public class IsolatedHashMapTest extends JUnitTestCase {

    /**
     * {@link ServerPlatform} mocking class. Allows changing current partition ID
     * during the test.
     */
    private static class TestServerPlatform implements ServerPlatform {

        /** Current partition ID. */
        private String partitionId;

        /**
         * Created an instance of {@code ServerPlatform} mocking class.
         * Current partition ID is set to default value.
         */
        private TestServerPlatform() {
            partitionId = DEFAULT_PARTITION_ID;
        }

        /**
         * Check whether this platform uses partitions.
         * @return Always returns {@code true}.
         */
        @Override
        public boolean usesPartitions() {
            return true;
        }

        /**
         * Get current partition ID.
         * @return Current partition ID.
         */
        @Override
        public String getPartitionID() {
            return partitionId;
        }

        /**
         * Set partition ID to be used as current partition context.
         * @param id New partition ID.
         */
        private void setPartitionID(final String id) {
            partitionId = id;
        }

        @Override
        public DatabaseSession getDatabaseSession() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getServerNameAndVersion() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getModuleName() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public Class getExternalTransactionControllerClass() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void setExternalTransactionControllerClass(final Class newClass) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void initializeExternalTransactionController() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean isJTAEnabled() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean isRuntimeServicesEnabledDefault() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void disableJTA() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean isRuntimeServicesEnabled() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void disableRuntimeServices() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void registerMBean() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void unregisterMBean() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void shutdown() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public int getThreadPoolSize() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void setThreadPoolSize(final int threadPoolSize) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public Connection unwrapConnection(final Connection connection) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void launchContainerRunnable(final Runnable runnable) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public SessionLog getServerLog() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean shouldUseDriverManager() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean wasFailureCommunicationBased(
                final SQLException exception, final Accessor connection, final AbstractSession sessionForProfile) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public JPAClassLoaderHolder getNewTempClassLoader(final PersistenceUnitInfo puInfo) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void clearStatementCache(Connection connection) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public int getJNDIConnectorLookupType() {
            throw new UnsupportedOperationException("Not implemented");
        }

    }

    /** Session name and partition ID separator. */
    private static final char SEPARATOR = '$';

    /** Default short enough partition ID when server does not support partitions.*/
    private static final String DEFAULT_PARTITION_ID = "0";

    /** Tested map shall be stared trough whole JVM to run test against the same map for all configured partitions. */
    private static final Map<String, String> map = IsolatedHashMap.newMap();

    /** Detected server platform. */
    private static final ServerPlatform serverPlatform;

    /** Does platform support partitions? */
    private static final boolean supportPartitions;

    /** Session name templates used to generate session names in the tests. */
    private static final String[] sessionNameTemplates = {
        "", "0", "mySession", "EclipseLink"
    };

    /** Class initialization code. */
    static {
        serverPlatform = ServerPlatformUtils.createServerPlatform(
                null, ServerPlatformUtils.detectServerPlatform(null), SessionManager.class.getClassLoader());
        // False value also handles case when serverPlatform is null to avoid NPE.
        supportPartitions = serverPlatform != null ? serverPlatform.usesPartitions() : false;
    }

    /**
     * Creates jUnit test suite for partition isolated {@link HashMap} tests.
     * @return new jUnit test suite containing all the tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("IsolatedHashMapTest");
        suite.addTest(new IsolatedHashMapTest("testIsolationInRealContext"));
        suite.addTest(new IsolatedHashMapTest("testIsolationInMockedContext"));
        return suite;
    }

    /**
     * Initialize session names in current partition context.
     * Test initialization helper.
     * @param partitionId Current partition ID. Shall not be {@code null}.
     */
     private static String[] initSessionNames(final String partitionId) {
         final int count = sessionNameTemplates.length;
         final String[] sessionNames = new String[count];
         int maxLength = 0;
         // Calculate StringBuilder internal storage size to avoid resizing.
         for (int i = 0; i < count; i++) {
             final int sessionNameTemplateLength = sessionNameTemplates[i].length();
             if (sessionNameTemplateLength > maxLength) {
                 maxLength = sessionNameTemplateLength;
             }
         }
         maxLength += 1 + partitionId.length();
         final StringBuilder sb = new StringBuilder(maxLength);
         for (int i = 0; i < count; i++) {
             if (i > 0) {
                 sb.delete(0, sb.length() - 1);
             }
             sb.append(sessionNameTemplates[i]);
             sb.append(SEPARATOR);
             sb.append(partitionId);
             sessionNames[i] = sb.toString();
         }
         return sessionNames;
     }

     /**
     * Creates an instance of partition isolated {@link HashMap} test.
     */
    public IsolatedHashMapTest() {
        super();
    }

    /**
     * Creates an instance of partition isolated {@link HashMap} test.
     * @param name jUnit test name.
     */
    public IsolatedHashMapTest(final String name) {
        super(name);
    }

    /**
     * Do partition isolation check in current partition context.
     * @param partitionId Current partition ID that should be visible in {@code map}.
     */
    private static void doIsolationCheck(final Map<String, String> map, final String partitionId, final SessionLog log) {
        final String[] sessionNames = initSessionNames(partitionId);
        final Set<String> namesSet = new HashSet<>(sessionNames.length);
        log.log(SessionLog.FINE, "  Partition ID: " + partitionId);
        for (String sessionName : sessionNames) {
            log.log(SessionLog.FINER, "    Adding session name: " + sessionName);
            map.put(sessionName, sessionName);
            namesSet.add(sessionName);
        }
        // Map shall contain session names in current partition context only.
        assertEquals("Map size does not match session names count.", sessionNames.length, map.size());
        // Verify individual session names.
        for (String sessionName : map.keySet()) {
            log.log(SessionLog.FINER, "    Getting and checking session name: " + sessionName);
            assertTrue("Session name " + sessionName + "was not stored in current partition context.",
                    namesSet.contains(sessionName));
        }
    }

    /**
     * Test partition isolation in real context with real {@link ServerPlatform}.
     */
    public void testIsolationInRealContext() {
        final SessionLog log = getServerSession().getSessionLog();
        log.log(SessionLog.INFO, "IsolatedHashMapTest.testIsolationInRealContext()");
        doIsolationCheck(map, supportPartitions ? serverPlatform.getPartitionID() : DEFAULT_PARTITION_ID, log);
    }

    /**
     * Test partition isolation in mocked partition context.
     */
    public void testIsolationInMockedContext() {
        final SessionLog log = getServerSession().getSessionLog();
        log.log(SessionLog.INFO, "IsolatedHashMapTest.testIsolationInMockedContext()");
        // Replace serverPlatform with mocked instance in IsolatedHashMap.
        final TestServerPlatform testPlatform = new TestServerPlatform();
        Field serverPlatformField;
        Field supportPartitions;
        try {
            final ServerPlatform originalPlatform
                    = (ServerPlatform)ReflectionHelper.getPrivateStatic(IsolatedHashMap.class, "serverPlatform");
            final boolean originalsupport
                    = (boolean)ReflectionHelper.getPrivateStatic(IsolatedHashMap.class, "supportPartitions");
            log.log(SessionLog.FINER,
                    "Original platform field instance: " + originalPlatform.getClass().getName());
            log.log(SessionLog.FINER, "Original partitions support flag: " + Boolean.toString(originalsupport));
            ReflectionHelper.setPrivateStaticFinal(IsolatedHashMap.class, "serverPlatform", testPlatform);
            ReflectionHelper.setPrivateStaticFinal(
                    IsolatedHashMap.class, "supportPartitions", testPlatform.usesPartitions());
            // Run the test in mocked partition context.
            final Map<String, String> map = IsolatedHashMap.newMap();
            final String[] partitionIds = {"first", "second", "third"};
            for (String partitionId : partitionIds) {
                testPlatform.setPartitionID(partitionId);
                doIsolationCheck(map, partitionId, log);
            }
            // Return original serverPlatform instance into IsolatedHashMap.
            ReflectionHelper.setPrivateStaticFinal(IsolatedHashMap.class, "serverPlatform", originalPlatform);
            ReflectionHelper.setPrivateStaticFinal(IsolatedHashMap.class, "supportPartitions", originalsupport);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            log.logThrowable(SessionLog.WARNING, e);
            e.printStackTrace();
        }
    }

}
