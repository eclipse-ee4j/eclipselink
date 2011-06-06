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
            //need to thrown noSuchMethod 		return Helper.getDeclaredMethod(getDescriptor().getJavaClass(), methodName, declarationParameters);
            eventManager.initialize((AbstractSession)getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}
