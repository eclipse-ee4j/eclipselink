/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - October 20, 2009
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.xml.sax.helpers.DefaultHandler;

public class XMLMarshalExceptionTestCases  extends OXTestCase {

    private XMLContext context;

    public XMLMarshalExceptionTestCases(String name) {
        super(name);
     }

    public static void main(String[] args) {

        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshalExceptionTestCase" };
        TestRunner.main(arguments);
    }

    @Override
    public void setUp() throws Exception {
    }

    public void testMarshalWithNoPrefixInNamespaceResolver() {

        DefaultHandler output = new DefaultHandler();
        try {
            context = getXMLContext(new XMLMarshallerExceptionTestProject());
        } catch (org.eclipse.persistence.exceptions.IntegrityException e){
            assertEquals(1, e.getIntegrityChecker().getCaughtExceptions().size());
            Exception nestedException = e.getIntegrityChecker().getCaughtExceptions().get(0);
            assertTrue(nestedException instanceof XMLMarshalException);
            assertEquals("An unexpected XMLMarshalException was caught.", XMLMarshalException.NAMESPACE_NOT_FOUND, ((XMLMarshalException) nestedException).getErrorCode());
            return;
        }

        fail("An XMLValidation should have been caught but wasn't.");
    }
}
