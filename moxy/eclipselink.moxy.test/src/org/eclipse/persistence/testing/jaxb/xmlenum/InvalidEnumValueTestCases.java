/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlenum;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class InvalidEnumValueTestCases extends TestCase {

    public InvalidEnumValueTestCases(String name) {
        super(name);
    }

    public void testCreateContext() throws JAXBException{
    	try
    	{
           JAXBContextFactory.createContext(new Class[] {InvalidEnum.class}, null);
    	}catch(JAXBException jException){
    		 org.eclipse.persistence.exceptions.JAXBException linkedException  = ( org.eclipse.persistence.exceptions.JAXBException)jException.getLinkedException();
    		
    		assertEquals(org.eclipse.persistence.exceptions.JAXBException.INVALID_ENUM_VALUE ,linkedException.getErrorCode());
    		return;
    	}
    	fail("A JAXBException should have been thrown");
    }

}
