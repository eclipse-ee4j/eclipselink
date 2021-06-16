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
//     Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.xmltype.proporder;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import junit.framework.TestCase;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class MissingPropTestCases extends TestCase {

    public void testInvalidPropOrder() {

        try {
            JAXBContextFactory.createContext(new Class[] {MissingPropRoot.class}, null);
        } catch(jakarta.xml.bind.JAXBException e) {
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
