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
package org.eclipse.persistence.testing.oxm.schemareference;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

public class CustomErrorHandler extends DefaultHandler {
    private boolean ignoredError = false;
    public void warning(SAXParseException ex) throws SAXException {
        System.out.println("Warning Raised: " + ex.getMessage());
    }
    public void error(SAXParseException ex) throws SAXException {
        String message = ex.getMessage();
        System.out.println("Error with message " + message + " being handled...");
        if(message.indexOf("Element \'b\'") != -1) {
            System.out.println("Ignoring error...");
            ignoredError = true;
        }
        else {
            System.out.println("Unable to ignore error, rethrowing...");
            throw ex;
        }
    }
    public void fatalError(SAXParseException ex) throws SAXException {
        System.out.println("Fatal Error Raised: " + ex.getMessage());
        String message = ex.getMessage();
        System.out.println("Error with message " + message + " being handled...");
        if(message.indexOf("Element \'b\'") != -1) {
            System.out.println("Ignoring error...");
            ignoredError = true;
        }
        else {
            System.out.println("Unable to ignore error, rethrowing...");
            throw ex;
        }
    }

    public boolean ignoredError() {
        return ignoredError;
    }
}
