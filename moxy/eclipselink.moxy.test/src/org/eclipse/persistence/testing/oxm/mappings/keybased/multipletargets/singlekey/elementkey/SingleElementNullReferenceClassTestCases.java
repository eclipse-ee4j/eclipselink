/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - February 9, 2010 - Since 2.1
package org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.elementkey;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;

import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.Employee;

public class SingleElementNullReferenceClassTestCases extends OXTestCase {

    Project p;

    public SingleElementNullReferenceClassTestCases(String name) throws Exception {
        super(name);
        p = new SingleElementKeyWithGroupingProject();
        XMLCollectionReferenceMapping mapping = ((XMLCollectionReferenceMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("addresses"));
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
