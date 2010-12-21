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

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;


//Created by Ian Reid
//Date: Feb 27, 2k3

public class InvalidDataModificationEventTest extends ExceptionTest {

    public InvalidDataModificationEventTest() {
        setDescription("This tests Invalid Data Modification Event (TL-ERROR 35)");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        expectedException = DescriptorException.invalidDataModificationEvent(null);
    }

    public void test() {
        try {

            DirectToFieldMapping aMapping = new DirectToFieldMapping();
            aMapping.performDataModificationEvent(null, (AbstractSession)getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}
