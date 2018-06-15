/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/30/2016-2.7 Tomas Kraus
//       - 490677: Initial API and implementation.
package org.eclipse.persistence.testing.tests.eis.nosql;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.persistence.eis.interactions.MappedInteraction;
import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLOperation;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLPlatform;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.testing.framework.ReflectionHelper;
import org.eclipse.persistence.testing.framework.junit.LogTestExecution;
import org.eclipse.persistence.testing.tests.nosql.NoSQLURI;
import org.eclipse.persistence.testing.tests.nosql.SessionHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Oracle NoSQL database simple tests with no model requirements.
 */
public class NoSQLSimpleTest {

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /** Database session shared by tests except first tests which are verifying session creation. */
    DatabaseSession session;

    /**
     * Create an instance of NoSQL database test suite.
     */
    public NoSQLSimpleTest() {
        session = null;
    }

    /**
     * Initialize this test suite.
     */
    @Before
    public void setUp() {
        session = SessionHelper.createDatabaseSession(NoSQLTestSuite.project);
    }

    /**
     * Clean up this test suite.
     */
    @After
    public void tearDown() {
        session.logout();
        session = null;
    }

    /**
     * Test URI parser.
     */
    @Test
    public void testURIParser() throws Exception {
        // Each String[] item contains 5 fields:
        // leading spaces, host, port, store and trailing spaces.
        final String[][] uris = {
                {"", "host", "12345", "store", ""},
                {"", "host", "", "store", ""},
                {"    ", "host", "12345", "store", ""},
                {"    ", "host", "", "store", ""},
                {"", "host", "12345", "store", "    "},
                {"", "host", "", "store", "    "},
                {"    ", "host", "12345", "store", "    "},
                {"    ", "host", "", "store", "    "}
        };
        final String kw = ReflectionHelper.<String>getPrivateStatic(NoSQLURI.class, "KEYWORD");
        // Build NoSQL URI
        for (String[] uriParts : uris) {
            StringBuilder sb = new StringBuilder();
            sb.append(uriParts[0]);
            sb.append(kw);
            sb.append(uriParts[1]);
            if (uriParts[2] != null && uriParts[2].length() > 0) {
                sb.append(':');
                sb.append(uriParts[2]);
            }
            sb.append('/');
            sb.append(uriParts[3]);
            sb.append(uriParts[4]);
            final String uriStr = sb.toString();
            final NoSQLURI uri = new NoSQLURI(uriStr);
            final String host = uri.getHost();
            final int portRef = uriParts[2] != null && uriParts[2].length() > 0 ? Integer.parseInt(uriParts[2]) : -1;
            final int port = uri.getPort();
            final String store = uri.getStore();
            LOG.log(SessionLog.FINE, String.format("Testing URI parser on URI: |%s|", uriStr));
            assertEquals(String.format("Host shall be %s for URI %s: %s",
                    uriParts[1], uriStr, host), uriParts[1], host);
            assertEquals(String.format("Port shall be %d for URI %s: %d",
                    portRef, uriStr, port), portRef, port);
            assertEquals(String.format("Store shall be %s for URI %s: %s",
                    uriParts[3], uriStr, store), uriParts[3], store);

        }
    }

    /**
     * Test native Oracle NoSQL queries.
     */
    @Test
    public void testNative() throws Exception {
        final MappedInteraction insertCall = new MappedInteraction();
        insertCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT.name());
        insertCall.addArgumentValue("Order/1234", "foo");
        final DataModifyQuery insert = new DataModifyQuery(insertCall);
        session.executeQuery(insert);
        final MappedInteraction readCall = new MappedInteraction();
        readCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET.name());
        readCall.addArgumentValue("Order/1234", "");
        final DataReadQuery read = new DataReadQuery(readCall);
        @SuppressWarnings("unchecked")
        final List<Record> result = (List<Record>)session.executeQuery(read);
        final String value = new String((byte[])result.get(0).get("Order/1234"));
        assertEquals("foo expected: " + value, "foo", value);
    }

}
