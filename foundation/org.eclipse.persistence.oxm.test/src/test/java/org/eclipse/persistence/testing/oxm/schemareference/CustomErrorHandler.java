/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.schemareference;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class CustomErrorHandler extends DefaultHandler {
    private boolean ignoredError = false;
    @Override
    public void warning(SAXParseException ex) throws SAXException {
        System.out.println("Warning Raised: " + ex.getMessage());
    }
    @Override
    public void error(SAXParseException ex) throws SAXException {
        String message = ex.getMessage();
        System.out.println("Error with message " + message + " being handled...");
        if(message.contains("Element 'b'")) {
            System.out.println("Ignoring error...");
            ignoredError = true;
        }
        else {
            System.out.println("Unable to ignore error, rethrowing...");
            throw ex;
        }
    }
    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        System.out.println("Fatal Error Raised: " + ex.getMessage());
        String message = ex.getMessage();
        System.out.println("Error with message " + message + " being handled...");
        if(message.contains("Element 'b'")) {
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
