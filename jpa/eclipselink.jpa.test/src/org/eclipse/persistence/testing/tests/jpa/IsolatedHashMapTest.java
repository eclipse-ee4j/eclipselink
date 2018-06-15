/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     05/11/2015-2.7 Tomas Kraus
//       - Initial API and implementation.
package org.eclipse.persistence.testing.tests.jpa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.exceptions.ServerPlatformException;
import org.eclipse.persistence.internal.jpa.IsolatedHashMap;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.platform.server.ServerPlatformUtils;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests partition isolated {@link HashMap}.
 */
public class IsolatedHashMapTest extends JUnitTestCase {

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
        String platformClass = ServerPlatformUtils.detectServerPlatform(null);
        ServerPlatform sp = null;
        try {
            sp = platformClass != null
                    ? ServerPlatformUtils.createServerPlatform(null, platformClass, SessionManager.class.getClassLoader())
                    : null;
        } catch (ServerPlatformException e) {
        }
        serverPlatform = sp;
        // False value also handles case when serverPlatform is null to avoid
        // NPE.
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
        log.log(SessionLog.INFO, "  Partition ID: " + partitionId);
        for (String sessionName : sessionNames) {
            log.log(SessionLog.INFO, "    Adding session name: " + sessionName);
            map.put(sessionName, sessionName);
            namesSet.add(sessionName);
        }
        // Map shall contain session names in current partition context only.
        final int mapSize = map.size();
        log.log(SessionLog.INFO, "  Checking map size: session names count = "
                + sessionNames.length + ", map size = " + mapSize );
        assertEquals("Map size does not match session names count.", sessionNames.length, mapSize);
        // Verify individual session names.
        for (String sessionName : map.keySet()) {
            log.log(SessionLog.INFO, "    Getting and checking session name: " + sessionName);
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

}
