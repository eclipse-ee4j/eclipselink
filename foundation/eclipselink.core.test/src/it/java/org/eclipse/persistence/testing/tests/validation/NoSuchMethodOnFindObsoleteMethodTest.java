/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;


//Created by Ian Reid
//Date: Feb 27, 2k3

public class NoSuchMethodOnFindObsoleteMethodTest extends ExceptionTest {
    public NoSuchMethodOnFindObsoleteMethodTest() {
        super();
        setDescription("This tests No Such Method On Find Obsolete Method (TL-ERROR 55)");
    }

    protected void setup() {
        expectedException = DescriptorException.noSuchMethodOnFindObsoleteMethod("Dummy_Method", new RelationalDescriptor(), new Exception());
    }

    public void test() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(NoSuchMethodOnFindObsoleteMethodTest.class);
        DescriptorEventManager eventManager = descriptor.getEventManager();
        //need getEventSelectors().elementAt(index) != null
        //getEventMethods().setElementAt(findMethod(index), index);
        eventManager.setPreWriteSelector("Dummy_Method"); //this method does not exist in above class

        try {
            //need to thrown noSuchMethod         return Helper.getDeclaredMethod(getDescriptor().getJavaClass(), methodName, declarationParameters);
            eventManager.initialize((AbstractSession)getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}
