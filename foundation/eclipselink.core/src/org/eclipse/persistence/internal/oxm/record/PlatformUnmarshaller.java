/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

public interface PlatformUnmarshaller {
    public abstract EntityResolver getEntityResolver();

    public abstract void setEntityResolver(EntityResolver entityResolver);

    public abstract ErrorHandler getErrorHandler();

    public abstract void setErrorHandler(ErrorHandler errorHandler);

    public abstract int getValidationMode();

    public abstract void setValidationMode(int validationMode);

    public abstract void setWhitespacePreserving(boolean isWhitespacePreserving);

    public abstract void setSchemas(Object[] schemas);

    public abstract Object unmarshal(File file, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(File file, Class clazz, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(InputStream inputStream, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(InputStream inputStream, Class clazz, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(InputSource inputSource, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(InputSource inputSource, Class clazz, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(Node node, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(Node node, Class clazz, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(Reader reader, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(Reader reader, Class clazz, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(Source source, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(Source source, Class clazz, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(URL url, XMLUnmarshaller unmarshaller);

    public abstract Object unmarshal(URL url, Class clazz, XMLUnmarshaller unmarshaller);
    
    public abstract void setResultAlwaysXMLRoot(boolean alwaysReturnRoot);
    
    public abstract boolean isResultAlwaysXMLRoot();
}