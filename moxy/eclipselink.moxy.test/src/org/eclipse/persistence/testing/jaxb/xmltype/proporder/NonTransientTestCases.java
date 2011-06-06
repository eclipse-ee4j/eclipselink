/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmltype.proporder;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class NonTransientTestCases extends TestCase {

    public NonTransientTestCases(String name) {
        super(name);
    }

    public void testCreateContext() {
        try {
            JAXBContextFactory.createContext(new Class[] {ChildOfRoot.class}, null);
        } catch(javax.xml.bind.JAXBException e) {
            JAXBException jaxbException = (JAXBException) e.getCause();
            assertEquals(JAXBException.NON_EXISTENT_PROPERTY_IN_PROP_ORDER, jaxbException.getErrorCode());
            return;
        }
        fail();
    }
}
