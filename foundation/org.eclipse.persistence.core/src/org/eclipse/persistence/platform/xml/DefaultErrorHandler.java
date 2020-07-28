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
// mmacivor - August 22/2008 - 1.0 - Initial implementation
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

    private static final DefaultErrorHandler instance = new DefaultErrorHandler();

    private DefaultErrorHandler() {}

    public static DefaultErrorHandler getInstance() {
        return instance;
    }

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
