/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests useXmlValueExtension method when system property org.eclipse.persistence.moxy.annotation.xml-value-extension is not set.
 */
public class XmlValueSystemPropertyNotSetTestCase {

    private static final Class<?>[] DOMAIN_CLASSES = new Class<?>[]{Parent.class, Child.class};

    @Test
    public void testSystemXmlValuextensionNotSet() {
        try {
            JAXBContextFactory.createContext(DOMAIN_CLASSES, null);
            fail("Expected JAXBException.");
        } catch (jakarta.xml.bind.JAXBException expected) {
            assertEquals(50011, ((org.eclipse.persistence.exceptions.JAXBException)expected.getLinkedException()).getErrorCode());
        }
    }
}
