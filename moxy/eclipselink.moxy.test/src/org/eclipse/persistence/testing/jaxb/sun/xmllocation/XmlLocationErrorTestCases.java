/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 04 October 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.sun.xmllocation;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

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
