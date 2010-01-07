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

import org.eclipse.persistence.exceptions.IntegrityChecker;


//Created By Ian Reid
//This class should be sub-classed for all test cases inwhich a security manager is changed
//to the session OR
// the integrityChecker is changed

public class ExceptionTestSaveSecurityManager extends ExceptionTest {

    SecurityManager orgSecurityManager;
    IntegrityChecker orgIntegrityChecker;

    public ExceptionTestSaveSecurityManager() {
    }

    protected void setup() {
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker()); //moved into setup
        getSession().getIntegrityChecker().dontCatchExceptions(); //moved into setup
        orgSecurityManager = System.getSecurityManager(); //security java.policy must allow full access
        System.setSecurityManager(new TestSecurityManager());
    }

    public void reset() {
        getSession().setIntegrityChecker(orgIntegrityChecker); //security java.policy must allow full access
        System.setSecurityManager(orgSecurityManager);
    }
}


