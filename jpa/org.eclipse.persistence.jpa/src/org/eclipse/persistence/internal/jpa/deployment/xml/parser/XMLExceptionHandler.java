/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.deployment.xml.parser;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLExceptionHandler implements ErrorHandler {
    private XMLException m_xmlException;

    public void warning(SAXParseException exception) throws SAXException {
        this.error(exception);
    }

    public void error(SAXParseException exception) throws SAXException {
        if (m_xmlException == null) {
            m_xmlException = new XMLException();
        }
        m_xmlException.addNestedException(exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        this.error(exception);
    }

    public XMLException getXMLException() {
        return m_xmlException;
    }
}
