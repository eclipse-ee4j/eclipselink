/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.exceptions.IntegrityChecker;


//Created By Ian Reid
//This class should be sub-classed for all test cases inwhich a descriptor is added
//to the session OR
// the integrityChecker is changed

public class ExceptionTestSaveDescriptor extends ExceptionTest {

    IntegrityChecker orgIntegrityChecker;
    Vector orgOrderedDescriptor;
    Map orgDescriptors; //added for changes in 10

    public ExceptionTestSaveDescriptor() {
    }

    protected void setup() {
        orgIntegrityChecker = getSession().getIntegrityChecker();
        orgOrderedDescriptor = new Vector(getSession().getProject().getOrderedDescriptors()); //added
        orgDescriptors = (Map)((HashMap)getSession().getProject().getDescriptors()).clone();
        getSession().setIntegrityChecker(new IntegrityChecker()); //moved into setup
        getSession().getIntegrityChecker().dontCatchExceptions(); //moved into setup
    }

    public void reset() {
        getSession().setIntegrityChecker(orgIntegrityChecker);
        getSession().getProject().setDescriptors(orgDescriptors);
        getAbstractSession().clearDescriptors();
        //the next method re-addes all descriptor back into hashtable BUT this was changed for 10
        getSession().getProject().setOrderedDescriptors(orgOrderedDescriptor); //added
    }
}
