/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshallerHandler;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class XMLUnmarshallerHandlerTestCases extends OXTestCase {
    private XMLUnmarshallerHandler xmlUnmarshallerHandler;

    public XMLUnmarshallerHandlerTestCases(String name) {
        super(name);
    }

    public void setUp() {
        XMLMarshallerCarProject project = new XMLMarshallerCarProject();
        XMLContext xmlContext = new XMLContext(project);
        XMLUnmarshaller xmlUnmarshaller = xmlContext.createUnmarshaller();
        xmlUnmarshallerHandler = xmlUnmarshaller.getUnmarshallerHandler();
    }

    public void testInvalidState() {
        try {
            xmlUnmarshallerHandler.getResult();
        } catch (XMLMarshalException e) {
            if (e.getErrorCode() != XMLMarshalException.ILLEGAL_STATE_XML_UNMARSHALLER_HANDLER) {
                fail("The wrong XMLMarshalException was thrown.");
            } else {
                return;
            }
        }
        fail("An XMLMarshalException with error code " + XMLMarshalException.ILLEGAL_STATE_XML_UNMARSHALLER_HANDLER + " should have been thrown");
    }
}
