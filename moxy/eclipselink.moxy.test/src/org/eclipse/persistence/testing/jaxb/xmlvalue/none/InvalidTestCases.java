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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlvalue.none;

import javax.xml.bind.JAXBContext;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class InvalidTestCases extends TestCase {

    public InvalidTestCases(String name) {
        super(name);
    }

    public void testCreateContext() throws Exception {
        try {
            JAXBContextFactory.createContext(new Class[] {InvalidChild.class}, null);
        } catch(javax.xml.bind.JAXBException e) {
            JAXBException moxyException = (JAXBException) e.getCause();
            assertEquals(JAXBException.SUBCLASS_CANNOT_HAVE_XMLVALUE, moxyException.getErrorCode());
            return;
        }
        fail();
    }
}
