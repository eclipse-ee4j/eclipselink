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
//     Denise Smith - October 2012
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
