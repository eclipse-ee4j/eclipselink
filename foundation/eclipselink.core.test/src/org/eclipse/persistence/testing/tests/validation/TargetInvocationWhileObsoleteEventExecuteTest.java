/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;


//Created by Ian Reid
//Date: Mar 20, 2k3

public class TargetInvocationWhileObsoleteEventExecuteTest extends ExceptionTest {
    public TargetInvocationWhileObsoleteEventExecuteTest() {
        setDescription("This tests Target Invocation While Obsolete Event Execute (TL-ERROR 105)");
    }
    ClassDescriptor descriptor;
    DescriptorEventManager eventManager;
    DescriptorEvent event;

    protected void setup() {
        descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(TargetInvocationWhileObsoleteEventExecuteTest.class);
        descriptor.addTableName("DummyTable");
        eventManager = descriptor.getEventManager();
        eventManager.setPreUpdateSelector("invalidMethod"); //this method throws an exception
        eventManager.initialize((AbstractSession)getSession());
        event = new DescriptorEvent(new TargetInvocationWhileObsoleteEventExecuteTest());
        event.setEventCode(DescriptorEventManager.PreUpdateEvent);
        event.setDescriptor(descriptor);
        event.setSession((AbstractSession)getSession());
        expectedException = DescriptorException.targetInvocationWhileObsoleteEventExecute("invalidMethod", descriptor, new Exception());
    }

    public void test() {
        try {
            eventManager.executeEvent(event);
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public void invalidMethod(org.eclipse.persistence.sessions.Session event) throws java.lang.IllegalAccessException {
        throw new java.lang.IllegalAccessException();
    }
}
