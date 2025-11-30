/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlenum;

import jakarta.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class InvalidEnumValueTestCases extends TestCase {

    public InvalidEnumValueTestCases(String name) {
        super(name);
    }

    public void testCreateContext() throws JAXBException{
        try
        {
           JAXBContextFactory.createContext(new Class<?>[] {InvalidEnum.class}, null);
        }catch(JAXBException jException){
             org.eclipse.persistence.jaxb.JAXBException linkedException  = (org.eclipse.persistence.jaxb.JAXBException)jException.getLinkedException();

            assertEquals(org.eclipse.persistence.jaxb.JAXBException.INVALID_ENUM_VALUE ,linkedException.getErrorCode());
            return;
        }
        fail("A JAXBException should have been thrown");
    }

}
