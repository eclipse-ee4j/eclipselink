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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.platform.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

public interface XMLParser {
    int NONVALIDATING = 0;
    int DTD_VALIDATION = 2;
    int SCHEMA_VALIDATION = 3;

    void setNamespaceAware(boolean isNamespaceAware);

    void setWhitespacePreserving(boolean isWhitespacePreserving);

    int getValidationMode();

    void setValidationMode(int validationMode);

    EntityResolver getEntityResolver();

    void setEntityResolver(EntityResolver entityResolver);

    ErrorHandler getErrorHandler();

    void setErrorHandler(ErrorHandler errorHandler);

    void setXMLSchema(URL url) throws XMLPlatformException;

    void setXMLSchemas(Object[] schemas) throws XMLPlatformException;

    void setXMLSchema(Schema schema) throws XMLPlatformException;

    Schema getXMLSchema() throws XMLPlatformException;

    Document parse(InputSource inputSource) throws XMLPlatformException;

    Document parse(File file) throws XMLPlatformException;

    Document parse(InputStream inputStream) throws XMLPlatformException;

    Document parse(Reader reader) throws XMLPlatformException;

    Document parse(Source source) throws XMLPlatformException;

    Document parse(URL url) throws XMLPlatformException;
}
