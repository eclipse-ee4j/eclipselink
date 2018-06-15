/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.xmltype.proporder;

import javax.xml.bind.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import junit.framework.TestCase;

public class ExtraPropTestCases extends TestCase {
    public void testInvalidPropOrder() {

        try {
            JAXBContextFactory.createContext(new Class[] {ExtraPropRoot.class}, null);

        } catch(javax.xml.bind.JAXBException e) {
            try {
                throw e.getLinkedException();
            } catch(org.eclipse.persistence.exceptions.JAXBException e2) {
                assertEquals(50012, e2.getErrorCode());
                return;
            } catch(Throwable t) {
                fail();
            }
        fail();
    }
    }
}
