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
package org.eclipse.persistence.internal.oxm.record;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public interface PlatformUnmarshaller {

    EntityResolver getEntityResolver();

    void setEntityResolver(EntityResolver entityResolver);

    ErrorHandler getErrorHandler();

    void setErrorHandler(ErrorHandler errorHandler);

    int getValidationMode();

    void setValidationMode(int validationMode);

    void setWhitespacePreserving(boolean isWhitespacePreserving);

    void setSchemas(Object[] schemas);

    void setSchema(Schema schema);

    Schema getSchema();

    Object unmarshal(File file);

    Object unmarshal(File file, Class clazz);

    Object unmarshal(InputStream inputStream);

    Object unmarshal(InputStream inputStream, Class clazz);

    Object unmarshal(InputSource inputSource);

    Object unmarshal(InputSource inputSource, Class clazz);

    Object unmarshal(Node node);

    Object unmarshal(Node node, Class clazz);

    Object unmarshal(Reader reader);

    Object unmarshal(Reader reader, Class clazz);

    Object unmarshal(Source source);

    Object unmarshal(Source source, Class clazz);

    Object unmarshal(URL url);

    Object unmarshal(URL url, Class clazz);

    Object unmarshal(XMLReader xmlReader, InputSource inputSource);

    Object unmarshal(XMLReader xmlReader, InputSource inputSource, Class clazz);

    void setResultAlwaysXMLRoot(boolean alwaysReturnRoot);

    boolean isResultAlwaysXMLRoot();

    void mediaTypeChanged();

    boolean isSecureProcessingDisabled();

    void setDisableSecureProcessing(boolean disableSecureProcessing);

}
