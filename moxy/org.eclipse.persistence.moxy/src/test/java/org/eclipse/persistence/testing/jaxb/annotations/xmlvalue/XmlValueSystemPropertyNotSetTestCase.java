/*
 * Copyright (c) 2014, 2020 Oracle and/or its affiliates. All rights reserved.
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
