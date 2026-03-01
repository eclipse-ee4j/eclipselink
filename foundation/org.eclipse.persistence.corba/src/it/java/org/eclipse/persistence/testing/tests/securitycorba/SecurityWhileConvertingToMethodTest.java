/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.securitycorba;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.remote.DistributedSession;
import org.eclipse.persistence.sessions.remote.corba.sun.CORBAConnection;
import org.eclipse.persistence.sessions.remote.corba.sun.CORBARemoteSessionControllerDispatcher;
import org.eclipse.persistence.testing.tests.security.ExceptionTestSaveSecurityManager;
import org.eclipse.persistence.testing.tests.security.TestSecurityManager;

import java.util.logging.Logger;

//Created by Ian Reid
//Date: April 25, 2k3
public class SecurityWhileConvertingToMethodTest extends ExceptionTestSaveSecurityManager {

    private static final Logger LOGGER = Logger.getLogger(SecurityWhileConvertingToMethodTest.class.getName());

    private TransformationMapping mapping;

    public SecurityWhileConvertingToMethodTest(Class<?> c) {
        super("This tests security while converting to method (TL-ERROR 85)", c);
    }

    @Override
    protected void setup() {
        //Don't execute at JDK 21 and higher
        if (Runtime.version().feature() < 21) {
            super.setup();
            expectedException = DescriptorException.securityWhileConvertingToMethod("getStartTime", mapping, new Exception());

            mapping = new TransformationMapping();
            mapping.setAttributeName("normalHours");
            mapping.setAttributeTransformation("buildNormalHours");
            mapping.addFieldTransformation("EMPLOYEE.START_TIME", "getStartTime");
            mapping.addFieldTransformation("EMPLOYEE.END_TIME", "getEndTime");
            // the following three lines ensure that the mapping is not isWriteOnly();
            mapping.setAttributeName(null);
            mapping.getAttributeAccessor().setAttributeName(null);
            mapping.setAttributeTransformation(null);
            mapping.setDescriptor(getTestDescriptor());
        } else {
            LOGGER.info("JDK is 21 and higher. Test was not executed!");
        }
    }

    @Override
    public void test() {
        //Don't execute at JDK 21 and higher
        if (Runtime.version().feature() < 21) {
            boolean orig = TestSecurityManager.TRIGGER_EX;
            TestSecurityManager.TRIGGER_EX = false;
            try {
                //need to use the remote Initialization as the normal one would product TL-084 before check this test
                CORBARemoteSessionControllerDispatcher dispatcher = new CORBARemoteSessionControllerDispatcher(getSession());
                CORBAConnection connection = new CORBAConnection(dispatcher);
                DistributedSession ds = (DistributedSession) (connection).createRemoteSession();
                TestSecurityManager.TRIGGER_EX = true;
                mapping.remoteInitialization(ds);
            } catch (EclipseLinkException exception) {
                caughtException = exception;
            } finally {
                TestSecurityManager.TRIGGER_EX = orig;
            }
        } else {
            LOGGER.info("JDK is 21 and higher. Test was not executed!");
        }
    }

    @Override
    protected void verify() {
        //Don't execute at JDK 21 and higher
        if (Runtime.version().feature() < 21) {
            super.verify();
        } else {
            LOGGER.info("JDK is 21 and higher. Test was not executed!");
        }
    }

    static class ConvertMethod {
        public void buildNormalHours() {
            //do nothing security manager will cause error to occur
        }

        public String getEndTime(Session s) {
            //do nothing security manager will cause error to occur
            return "";
        }
    }

    public static class ConvertMethodNoArg extends ConvertMethod {
        public String getStartTime() {
            //do nothing security manager will cause error to occur
            return "";
        }
    }

    public static class ConvertMethodSession extends ConvertMethod {
        public void getStartTime(Session session) {
            //do nothing security manager will cause error to occur
        }
    }

    public class ConvertMethodAbstractSession extends ConvertMethod {
        public void getStartTime(AbstractSession session) {
            //do nothing security manager will cause error to occur
        }
    }
}
