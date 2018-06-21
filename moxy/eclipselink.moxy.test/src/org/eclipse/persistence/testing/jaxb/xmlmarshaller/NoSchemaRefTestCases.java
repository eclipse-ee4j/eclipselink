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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationException;
import javax.xml.bind.Validator;

import junit.framework.TestCase;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.platform.xml.XMLPlatformException;

public class NoSchemaRefTestCases extends TestCase {

    public void testValidateRootNoSchemaReference() {
        try {
            Class[] classes = {Address.class};
            JAXBContext jc = JAXBContextFactory.createContext(classes, null);
            Validator validator = jc.createValidator();
            validator.validateRoot(new Address());
        } catch(ValidationException e) {
            XMLMarshalException xme = (XMLMarshalException) e.getLinkedException();
            assertEquals(XMLMarshalException.ERROR_RESOLVING_XML_SCHEMA, xme.getErrorCode());
            return;
        } catch(JAXBException e) {
            fail("A JAXBException was thrown instead of the expected ValidationException.");
        }
        fail("No exception was thrown instead of the expected ValidationException.");
    }

    public void testValidateNoSchemaReference() throws JAXBException {
        try {
            Class[] classes = {Address.class};
            JAXBContext jc = JAXBContextFactory.createContext(classes, null);
            Validator validator = jc.createValidator();
            validator.validate(new Address());
        } catch (ValidationException e) {
            XMLMarshalException xme = (XMLMarshalException) e.getLinkedException();
            XMLPlatformException xpe = (XMLPlatformException) xme.getInternalException();
            XMLMarshalException xme2 = (XMLMarshalException) xpe.getInternalException();
            assertEquals(XMLMarshalException.ERROR_RESOLVING_XML_SCHEMA, xme2.getErrorCode());
            return;
        }
    }

}
