/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref;

import jakarta.xml.bind.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests verify, that expected MOXy ErrorCode (50016) is returned during JAXBContext creation when system property org.eclipse.persistence.moxy.annotation.xml-id-extension is not set.
 */
public class XmlIdSystemPropertyNotSetTestCase{

    private static final Class<?>[] DOMAIN_CLASSES = new Class<?>[]{OwnerIntegerId.class, ThingIntegerId.class};

    @Test
    public void testSystemXmlIdExtensionNotSet() {
        try {
            JAXBContextFactory.createContext(DOMAIN_CLASSES, null);
            fail("Expected JAXBException.");
        } catch (JAXBException expected) {
            assertEquals(50016, ((org.eclipse.persistence.exceptions.JAXBException)expected.getLinkedException()).getErrorCode());
        }
    }
}
