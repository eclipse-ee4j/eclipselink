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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/*
 * Tests that the integrityChecker is lazily initialized on the session after
 * calling getIntegrityChecker()
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date January 28, 2003
 */
public class IntegrityCheckerLazyCreationTest extends AutoVerifyTestCase {
    IntegrityChecker integrityChecker;

    public IntegrityCheckerLazyCreationTest() {
        setDescription("Test that the integrity checker on a session is lazily initialized");
    }

    public void reset() {
        integrityChecker = null;
    }

    public void test() {
        Session session = getSession();
        session.setIntegrityChecker(null);
        integrityChecker = session.getIntegrityChecker();
    }

    public void verify() {
        if (integrityChecker == null) {
            throw new TestErrorException("The integrity checker was not lazily initialized");
        }
    }
}
