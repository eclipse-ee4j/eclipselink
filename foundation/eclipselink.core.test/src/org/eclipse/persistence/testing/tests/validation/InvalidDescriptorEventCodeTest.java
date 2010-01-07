/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;


//Created by Ian Reid
//Date: Feb 27, 2k3

public class InvalidDescriptorEventCodeTest extends ExceptionTest {

    public InvalidDescriptorEventCodeTest() {
        setDescription("This tests Invalid Descriptor Event Code (TL-ERROR 37)");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        expectedException = DescriptorException.invalidDescriptorEventCode(new DescriptorEvent(this), null);
    }

    public void test() {
        try {

            DescriptorEventManager eManager = new DescriptorEventManager();
            DescriptorEvent event = new DescriptorEvent(this);
            DescriptorEventAdapter listener = new DescriptorEventAdapter();

            event.setEventCode(-1);
            eManager.addListener(listener);
            eManager.notifyListeners(event);

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}
