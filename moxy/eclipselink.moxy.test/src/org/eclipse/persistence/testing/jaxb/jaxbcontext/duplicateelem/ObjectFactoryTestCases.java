/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbcontext.duplicateelem;

import javax.xml.bind.JAXBException;
import junit.framework.TestCase;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class ObjectFactoryTestCases extends TestCase {

    public void testCreateJAXBContextWithDuplicateElem() throws JAXBException {
    	try{
           JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.jaxbcontext.duplicateelem", this.getClass().getClassLoader());
    	}catch(JAXBException e){
    		Throwable nested = e.getLinkedException();
    		if(nested instanceof org.eclipse.persistence.exceptions.JAXBException){
    			assertEquals(org.eclipse.persistence.exceptions.JAXBException.DUPLICATE_ELEMENT_NAME, ((org.eclipse.persistence.exceptions.JAXBException)nested).getErrorCode());
    			return;
    		}
    	}
    	fail("A duplicate element exception should have occurred.");
    }

}