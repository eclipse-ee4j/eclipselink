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
package org.eclipse.persistence.internal.jpa.deployment.xml.parser;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLExceptionHandler implements ErrorHandler {
    private XMLException m_xmlException;

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        this.error(exception);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        if (m_xmlException == null) {
            m_xmlException = new XMLException();
        }
        m_xmlException.addNestedException(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        this.error(exception);
    }

    public XMLException getXMLException() {
        return m_xmlException;
    }
}
