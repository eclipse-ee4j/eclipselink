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
//  - rbarkhouse - 04 October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.xmllocation;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class XmlLocationErrorTestCases extends TestCase {

    public void testInvalidXmlLocationType() {
        Exception caughtEx = null;
        try {
            JAXBContext c = JAXBContextFactory.createContext(new Class[] { DataError.class }, null);
        } catch (Exception e) {
            caughtEx = e;
        }

        assertNotNull("Exception was not thrown as expected.", caughtEx);
        assertTrue("Unexpected exception type was thrown.", caughtEx instanceof JAXBException);

        JAXBException jEx = (JAXBException) caughtEx;
        assertTrue("Unexpected exception type was thrown.", jEx.getLinkedException() instanceof org.eclipse.persistence.exceptions.JAXBException);

        org.eclipse.persistence.exceptions.JAXBException elEx = (org.eclipse.persistence.exceptions.JAXBException) jEx.getLinkedException();
        assertEquals("", org.eclipse.persistence.exceptions.JAXBException.INVALID_XMLLOCATION, elEx.getErrorCode());
    }

}
