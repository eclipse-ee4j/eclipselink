/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jms;

import java.util.*;

import java.rmi.server.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.testing.framework.*;

public class JMSTestModel extends TestModel {
    public JMSTestModel() {
        setDescription("This model performs unit tests on the JMS classes.");
    }

    public void addTests() {
        addTest(getJMSConnectionTestSuite());
    }

    public TestSuite getJMSConnectionTestSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("JMSConnectionTestSuite");
        suite.setDescription("This test suite tests the JMS connection class.");

        // most methods return hardcoded garbage values (null, false, 0), so simply call them w/o checking results
        suite.addTest(new JMSConnectionTest("processCommand", new Class[] { RemoteCommand.class }, new Object[] { new RemoteCommandImpl() }));
        suite.addTest(new JMSConnectionTest("commitRootUnitOfWork", new Class[] { RemoteUnitOfWork.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("createRemoteSession"));
        suite.addTest(new JMSConnectionTest("cursoredStreamNextPage", new Class[] { RemoteCursoredStream.class, ReadQuery.class, RemoteSession.class, int.class }, new Object[] { null, null, null, new Integer(1) }));
        suite.addTest(new JMSConnectionTest("cursoredStreamSize", new Class[] { ObjID.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("cursorSelectObjects", new Class[] { CursoredStreamPolicy.class, DistributedSession.class }, new Object[] { null, null }));
        suite.addTest(new JMSConnectionTest("cursorSelectObjects", new Class[] { ScrollableCursorPolicy.class, DistributedSession.class }, new Object[] { null, null }));
        suite.addTest(new JMSConnectionTest("getDefaultReadOnlyClasses"));
        suite.addTest(new JMSConnectionTest("getDescriptor", new Class[] { Class.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("getLogin"));
        suite.addTest(new JMSConnectionTest("getSequenceNumberNamed", new Class[] { Object.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("instantiateRemoteValueHolderOnServer", new Class[] { RemoteValueHolder.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("remoteExecute", new Class[] { DatabaseQuery.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("remoteExecuteNamedQuery", new Class[] { String.class, Class.class, Vector.class }, new Object[] { null, null, null }));
        suite.addTest(new JMSConnectionTest("scrollableCursorAbsolute", new Class[] { ObjID.class, int.class }, new Object[] { null, new Integer(1) }));
        suite.addTest(new JMSConnectionTest("scrollableCursorCurrentIndex", new Class[] { ObjID.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("scrollableCursorFirst", new Class[] { ObjID.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("scrollableCursorIsAfterLast", new Class[] { ObjID.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("scrollableCursorIsBeforeFirst", new Class[] { ObjID.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("scrollableCursorIsFirst", new Class[] { ObjID.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("scrollableCursorIsLast", new Class[] { ObjID.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("scrollableCursorLast", new Class[] { ObjID.class }, new Object[] { null }));
        suite.addTest(new JMSConnectionTest("scrollableCursorNextObject", new Class[] { ObjID.class, ReadQuery.class, RemoteSession.class }, new Object[] { null, null, null }));
        suite.addTest(new JMSConnectionTest("scrollableCursorPreviousObject", new Class[] { ObjID.class, ReadQuery.class, RemoteSession.class }, new Object[] { null, null, null }));
        suite.addTest(new JMSConnectionTest("scrollableCursorRelative", new Class[] { ObjID.class, int.class }, new Object[] { null, new Integer(1) }));
        suite.addTest(new JMSConnectionTest("scrollableCursorSize", new Class[] { ObjID.class }, new Object[] { null }));
        // test exception handling
        suite.addTest(new JMSConnectionExceptionHandlingTest("processCommand", new Class[] { RemoteCommand.class }, new Object[] { null }));

        return suite;
    }
}
