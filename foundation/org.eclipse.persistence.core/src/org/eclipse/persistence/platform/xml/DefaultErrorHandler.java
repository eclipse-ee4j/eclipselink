/*******************************************************************************
* Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - August 22/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.platform.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


/**
 * INTERNAL:
 * A default implementation of ErrorHandler that simply rethrows the SAXParseExceptions. This works around
 * an issue that can result in a NPE when setting a Schema on a parser without setting an Error Handler.
 * @author mmacivor
 *
 */
public class DefaultErrorHandler implements ErrorHandler {
    
    public void error(SAXParseException ex) throws SAXParseException {
        throw ex;
    }
    
    public void warning(SAXParseException ex) throws SAXParseException {
        throw ex;
    }
    
    public void fatalError(SAXParseException ex) throws SAXParseException {
        throw ex;
    }

}
