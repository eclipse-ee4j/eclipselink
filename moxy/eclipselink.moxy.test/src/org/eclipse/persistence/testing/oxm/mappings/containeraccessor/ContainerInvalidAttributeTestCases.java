/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class ContainerInvalidAttributeTestCases extends OXTestCase {
    public ContainerInvalidAttributeTestCases(String name) {
        super(name);
    }

    public void testCreateContextAttributeAccess() {
        try {
            getXMLContext(new EmployeeInvalidContainerAttributeProject(false));
        } catch(IntegrityException ex) {
            ex.printStackTrace();
            assertTrue("incorrect number of errors", ex.getIntegrityChecker().getCaughtExceptions().size() == 1);
            DescriptorException caughtException = (DescriptorException)ex.getIntegrityChecker().getCaughtExceptions().elementAt(0);
            assertTrue(caughtException.getErrorCode() == 59);
        }
    }

    public void testCreateContextMethodAccess() {
        try {
            getXMLContext(new EmployeeInvalidContainerAttributeProject(true));
        } catch(IntegrityException ex) {
            ex.printStackTrace();
            assertTrue("incorrect number of errors", ex.getIntegrityChecker().getCaughtExceptions().size() == 1);
            DescriptorException caughtException = (DescriptorException)ex.getIntegrityChecker().getCaughtExceptions().elementAt(0);
            assertTrue(caughtException.getErrorCode() == 60);
        }
    }
}
