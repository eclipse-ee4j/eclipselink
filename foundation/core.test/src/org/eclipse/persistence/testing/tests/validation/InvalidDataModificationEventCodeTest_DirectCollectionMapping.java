/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectCollectionMapping;


//Created by Ian Reid
//Date: Feb 27, 2k3

public class InvalidDataModificationEventCodeTest_DirectCollectionMapping extends ExceptionTest {

    public InvalidDataModificationEventCodeTest_DirectCollectionMapping() {
        setDescription("This tests Invalid Data Modification Event Code (DirectCollectionMapping) (TL-ERROR 36)");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        expectedException = DescriptorException.invalidDataModificationEventCode(null, null);
    }

    public void test() {
        try {
            DirectCollectionMapping aMapping = new DirectCollectionMapping();

            String[] args = { "BAD_CODE", "BAD_CODE", "BAD_CODE" };
            aMapping.performDataModificationEvent(args, (AbstractSession)getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}
