/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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

// Contributors:
//     Denise Smith - February 9, 2010 - Since 2.1
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.elementkey;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;

import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.Employee;

public class SingleElementNullReferenceClassTestCases extends OXTestCase {

    Project p;

    public SingleElementNullReferenceClassTestCases(String name) throws Exception {
        super(name);
        p = new SingleElementKeyProject();
        XMLObjectReferenceMapping mapping = ((XMLObjectReferenceMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("address"));
        mapping.setReferenceClass(null);
        mapping.setReferenceClassName(null);
    }

    public void testNullReferenceClass() throws Exception{
        try{
            super.getXMLContext(p);
        }catch(IntegrityException e){
            Exception internalException = (Exception)((IntegrityException)e).getIntegrityChecker().getCaughtExceptions().get(0);
            if (internalException instanceof DescriptorException) {
                assertTrue("An incorrect DescriptorException exception occurred.", ((DescriptorException)internalException).getErrorCode() == DescriptorException.REFERENCE_CLASS_NOT_SPECIFIED);
                return;
            }
        }
        fail("A DescriptorException should have happened but didn't");
        return;
    }
}
