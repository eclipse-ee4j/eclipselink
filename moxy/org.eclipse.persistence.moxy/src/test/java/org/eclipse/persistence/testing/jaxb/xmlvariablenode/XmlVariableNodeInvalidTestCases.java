/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import java.util.ArrayList;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class XmlVariableNodeInvalidTestCases extends OXTestCase {//extends JAXBWithJSONTestCases{

    public XmlVariableNodeInvalidTestCases(String name) throws Exception {
        super(name);
    }

    public void testInvalid(){
        Class[] classes = new Class[]{RootInvalid.class};
        try {
            JAXBContext ctx = JAXBContextFactory.createContext(classes, null);
        } catch (JAXBException e) {
            Throwable nested = e.getLinkedException();
            assertTrue(nested instanceof org.eclipse.persistence.exceptions.JAXBException);
            if(((org.eclipse.persistence.exceptions.JAXBException)nested).getErrorCode() == org.eclipse.persistence.exceptions.JAXBException.UNKNOWN_TYPE_FOR_VARIABLE_MAPPING){
                return;
            }
        }
        fail("A JAXBException should have occurred");
    }

    public void testInvalid2(){
        Class[] classes = new Class[]{RootInvalid2.class};
        try {
            JAXBContext ctx = JAXBContextFactory.createContext(classes, null);
        } catch (JAXBException e) {
            Throwable nested = e.getLinkedException();
            assertTrue(nested instanceof org.eclipse.persistence.exceptions.JAXBException);
            if(((org.eclipse.persistence.exceptions.JAXBException)nested).getErrorCode() == org.eclipse.persistence.exceptions.JAXBException.UNKNOWN_PROPERTY_FOR_VARIABLE_MAPPING){
                return;
            }
        }
        fail("A JAXBException should have occurred");

    }

    public void testInvalid3(){
        Class[] classes = new Class[]{RootInvalid3.class};
        try {
            JAXBContext ctx = JAXBContextFactory.createContext(classes, null);
        } catch (JAXBException e) {
            Throwable nested = e.getLinkedException();
            assertTrue(nested instanceof org.eclipse.persistence.exceptions.JAXBException);
            if(((org.eclipse.persistence.exceptions.JAXBException)nested).getErrorCode() == org.eclipse.persistence.exceptions.JAXBException.INVALID_TYPE_FOR_VARIABLE_MAPPING){
                return;
            }
        }
        fail("A JAXBException should have occurred");

    }
}
