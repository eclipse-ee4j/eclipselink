/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - October 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmltype.proporder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import junit.framework.TestCase;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class MissingPropTestCases extends TestCase {

    public void testInvalidPropOrder() {

        try {
            JAXBContextFactory.createContext(new Class[] {MissingPropRoot.class}, null);
        } catch(javax.xml.bind.JAXBException e) {
            try {
                throw e.getLinkedException();
            } catch(org.eclipse.persistence.exceptions.JAXBException e2) {
                // MATCH ON CORRECT ERROR CODE
                assertEquals(50013, e2.getErrorCode());
                return;

            } catch(Throwable t) {
                fail();
            }

        }
        fail();
    }  
}