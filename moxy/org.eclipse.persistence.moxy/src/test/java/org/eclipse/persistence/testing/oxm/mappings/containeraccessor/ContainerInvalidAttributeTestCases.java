/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
            assertTrue("incorrect number of errors", ex.getIntegrityChecker().getCaughtExceptions().size() == 1);
            DescriptorException caughtException = (DescriptorException)ex.getIntegrityChecker().getCaughtExceptions().elementAt(0);
            assertTrue(caughtException.getErrorCode() == 59);
        }
    }

    public void testCreateContextMethodAccess() {
        try {
            getXMLContext(new EmployeeInvalidContainerAttributeProject(true));
        } catch(IntegrityException ex) {
            assertTrue("incorrect number of errors", ex.getIntegrityChecker().getCaughtExceptions().size() == 1);
            DescriptorException caughtException = (DescriptorException)ex.getIntegrityChecker().getCaughtExceptions().elementAt(0);
            assertTrue(caughtException.getErrorCode() == 60);
        }
    }
}
