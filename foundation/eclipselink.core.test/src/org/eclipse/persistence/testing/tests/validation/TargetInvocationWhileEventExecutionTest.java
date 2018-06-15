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

public class TargetInvocationWhileEventExecutionTest extends ExceptionTest {
    public TargetInvocationWhileEventExecutionTest() {
        setDescription("This tests Target Invocation While Event Execution (TL-ERROR 98)");
    }
    ClassDescriptor descriptor;
    DescriptorEventManager eventManager;
    DescriptorEvent event;

    protected void setup() {
        descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(TargetInvocationWhileEventExecutionTest.class);
        descriptor.addTableName("DummyTable");
        eventManager = descriptor.getEventManager();
        eventManager.setPreUpdateSelector("invalidMethod"); //this method throws an exception
        eventManager.initialize((AbstractSession)getSession());
        event = new DescriptorEvent(new TargetInvocationWhileEventExecutionTest());
        event.setEventCode(DescriptorEventManager.PreUpdateEvent);
        event.setDescriptor(descriptor);
        event.setSession((AbstractSession)getSession());
        expectedException = DescriptorException.targetInvocationWhileEventExecution("invalidMethod", descriptor, new Exception());
    }

    public void test() {
        try {
            eventManager.executeEvent(event);

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public void invalidMethod(DescriptorEvent event) throws java.lang.IllegalAccessException {
        throw new java.lang.IllegalAccessException();
    }

}
