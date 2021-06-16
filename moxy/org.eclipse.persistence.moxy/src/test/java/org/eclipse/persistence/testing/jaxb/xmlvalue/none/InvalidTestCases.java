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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlvalue.none;

import jakarta.xml.bind.JAXBContext;

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
        } catch(jakarta.xml.bind.JAXBException e) {
            JAXBException moxyException = (JAXBException) e.getCause();
            assertEquals(JAXBException.SUBCLASS_CANNOT_HAVE_XMLVALUE, moxyException.getErrorCode());
            return;
        }
        fail();
    }
}
