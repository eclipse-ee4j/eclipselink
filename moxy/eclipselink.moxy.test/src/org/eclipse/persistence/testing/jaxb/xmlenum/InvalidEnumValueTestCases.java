/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.1 - initial implementation
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
