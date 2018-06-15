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
package org.eclipse.persistence.testing.tests.security;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;

//Created by Ian Reid
//Date: April 25, 2k3
public class SecurityOnFindMethodTest extends ExceptionTestSaveSecurityManager {

    private DescriptorEventManager eventManager;

    public SecurityOnFindMethodTest() {
        super("This tests security on find method (TL-ERROR 82)", FindMethod.class);
    }

    protected void setup() {
        super.setup();
        expectedException = DescriptorException.securityOnFindMethod("dummy_Method", new RelationalDescriptor(), new Exception());

        eventManager = getTestDescriptor().getEventManager();
        //need getEventSelectors().elementAt(index) != null
        //getEventMethods().setElementAt(findMethod(index), index);
        eventManager.setPreWriteSelector("dummy_Method"); //this method exist in above class
    }

    public void test() {
        try {
            //need to thrown security         return Helper.getDeclaredMethod(getDescriptor().getJavaClass(), methodName, declarationParameters);
            eventManager.initialize((AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    static class FindMethod {
        public void dummy_Method(DescriptorEvent event) {
            //do nothing security manager will cause error to occur
        }
    }
}
