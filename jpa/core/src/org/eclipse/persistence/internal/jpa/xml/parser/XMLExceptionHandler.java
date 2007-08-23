/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.xml.parser;

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
