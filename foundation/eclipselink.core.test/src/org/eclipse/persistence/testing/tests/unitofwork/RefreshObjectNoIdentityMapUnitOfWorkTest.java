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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.*;

// This test compares the difference between session.refreshObject and uow.refreshObject.

public class RefreshObjectNoIdentityMapUnitOfWorkTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public UnitOfWork uow;
    public Weather objectToBeWritten;
    public Weather uowObject;
    public Weather sessionObject;

    /**
     * This method was created in VisualAge.
     */
    public void setup() {
        uow = getSession().acquireUnitOfWork();

        //just read the first one out of the database
        objectToBeWritten = (Weather)getSession().readObject(Weather.class);

        uowObject = (Weather)uow.registerObject(objectToBeWritten);

        (uowObject).setStormPattern((uowObject).getStormPattern() + " and windy");

    }

    /**
     * This method was created in VisualAge.
     */
    public void test() {
        try {
            uowObject = (Weather)uow.refreshObject(uowObject);
            uow.commit();
        } catch (Exception exp) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("An exception was thrown on commit.  Exception is :\n" + 
                                                                          exp.toString());
        }

        //now checking the refreshObject() in the session
        sessionObject = (Weather)getSession().refreshObject(objectToBeWritten);
    }

    /**
     * This method was created in VisualAge.
     */
    public void verify() {
        //let's compare the object refreshed in the UnitOfWork with the original object
        if (objectToBeWritten.getStormPattern().equals(uowObject.getStormPattern())) {
            // test passed
        } else {
            throw new org.eclipse.persistence.testing.framework.TestWarningException("Object refreshed through UnitOfWork returned nulls as its attribute values. Will be fixed in a later release.\n");
        }

        //now let's compare the object refreshed in the session with the original object
        if (objectToBeWritten.getStormPattern().equals(sessionObject.getStormPattern())) {
            // test passed
        } else {
            throw new org.eclipse.persistence.testing.framework.TestWarningException("Object refreshed through Session returned nulls as its attribute values. Test failed.\n");
        }
        uow.release();
    }
}
