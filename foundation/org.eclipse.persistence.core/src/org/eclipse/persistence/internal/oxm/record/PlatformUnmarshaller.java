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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.record;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import javax.xml.validation.Schema;

public interface PlatformUnmarshaller {

    public abstract EntityResolver getEntityResolver();

    public abstract void setEntityResolver(EntityResolver entityResolver);

    public abstract ErrorHandler getErrorHandler();

    public abstract void setErrorHandler(ErrorHandler errorHandler);

    public abstract int getValidationMode();

    public abstract void setValidationMode(int validationMode);

    public abstract void setWhitespacePreserving(boolean isWhitespacePreserving);

    public abstract void setSchemas(Object[] schemas);
    
    public abstract void setSchema(Schema schema);
    
    public abstract Schema getSchema();

    public abstract Object unmarshal(File file);

    public abstract Object unmarshal(File file, Class clazz);

    public abstract Object unmarshal(InputStream inputStream);

    public abstract Object unmarshal(InputStream inputStream, Class clazz);

    public abstract Object unmarshal(InputSource inputSource);

    public abstract Object unmarshal(InputSource inputSource, Class clazz);

    public abstract Object unmarshal(Node node);

    public abstract Object unmarshal(Node node, Class clazz);

    public abstract Object unmarshal(Reader reader);

    public abstract Object unmarshal(Reader reader, Class clazz);

    public abstract Object unmarshal(Source source);

    public abstract Object unmarshal(Source source, Class clazz);

    public abstract Object unmarshal(URL url);

    public abstract Object unmarshal(URL url, Class clazz);

    public abstract Object unmarshal(XMLReader xmlReader, InputSource inputSource);

    public abstract Object unmarshal(XMLReader xmlReader, InputSource inputSource, Class clazz);

    public abstract void setResultAlwaysXMLRoot(boolean alwaysReturnRoot);

    public abstract boolean isResultAlwaysXMLRoot();
    
    public abstract void mediaTypeChanged();

}
