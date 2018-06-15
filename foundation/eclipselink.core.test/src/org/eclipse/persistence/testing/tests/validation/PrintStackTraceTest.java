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

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/* This class tests if EclipseLinkException.printStackTrace works correctly, ie. if it ever flushed.
 */

public class PrintStackTraceTest extends AutoVerifyTestCase {
    /**
     * This method was created in VisualAge.
     */
    public PrintStackTraceTest() {
        super();
        setDescription("Tests if printStackTrace works correctly, ie. if Exceptions are ever logged using printStackTrace()." + "\nExceptions are expected to print out.  If there is none, test doesn't pass.");
    }

    /**
     * This method was created in VisualAge.
     */
    public void test() {

        // do not use logMessages(), which has been already set up for the session
        Integer integer = new Integer(1);
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.registerObject(integer);
            uow.commit();

        } catch (org.eclipse.persistence.exceptions.EclipseLinkException e) {
            java.io.OutputStream out = new java.io.ByteArrayOutputStream(100);
            e.printStackTrace(new java.io.PrintStream(out));
            if (out.toString().length() < 200) {
                throw new TestErrorException("Exception toString was to complete.");
            }
        }
    }
}
