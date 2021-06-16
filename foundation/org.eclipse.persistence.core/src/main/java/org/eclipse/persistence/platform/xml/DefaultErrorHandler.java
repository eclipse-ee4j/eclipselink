/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

    @Override
    public void error(SAXParseException ex) throws SAXParseException {
        throw ex;
    }

    @Override
    public void warning(SAXParseException ex) throws SAXParseException {
        throw ex;
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXParseException {
        throw ex;
    }

}
