/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - February 9, 2010 - Since 2.1
 ******************************************************************************/ 
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
