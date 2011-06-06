/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.platform.xml.xdk;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.validation.Schema;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import oracle.xml.jaxp.JXDocumentBuilderFactory;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformException;

/**
 * <p><b>Purpose</b>:  An implementation of XMLParser using Oracle XDK APIs.</p>
 */

public class XDKParser implements XMLParser {
    private static final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    private DocumentBuilderFactory documentBuilderFactory;
    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;

    public XDKParser() {
        super();
        documentBuilderFactory = new JXDocumentBuilderFactory();
        setNamespaceAware(true);
        setWhitespacePreserving(false);
    }

    public void setNamespaceAware(boolean isNamespaceAware) {
        documentBuilderFactory.setNamespaceAware(isNamespaceAware);
    }

    public void setWhitespacePreserving(boolean isWhitespacePreserving) {
        documentBuilderFactory.setIgnoringElementContentWhitespace(!isWhitespacePreserving);
    }

    public int getValidationMode() {
        if (!documentBuilderFactory.isValidating()) {
            return XMLParser.NONVALIDATING;
        }

        try {
            if (null == documentBuilderFactory.getAttribute(SCHEMA_LANGUAGE)) {
                return XMLParser.DTD_VALIDATION;
            }
        } catch (IllegalArgumentException e) {
            return XMLParser.DTD_VALIDATION;
        }

        return XMLParser.SCHEMA_VALIDATION;
    }

    public void setValidationMode(int validationMode) {
        switch (validationMode) {
        case XMLParser.NONVALIDATING: {
            documentBuilderFactory.setValidating(false);
            // documentBuilderFactory.setAttribute(SCHEMA_LANGUAGE, null);			
            return;
        }
        case XMLParser.DTD_VALIDATION: {
            documentBuilderFactory.setValidating(true);
            // documentBuilderFactory.setAttribute(SCHEMA_LANGUAGE, null);
            return;
        }
        case XMLParser.SCHEMA_VALIDATION: {
            try {
                documentBuilderFactory.setAttribute(SCHEMA_LANGUAGE, XML_SCHEMA);
                documentBuilderFactory.setValidating(true);
            } catch (IllegalArgumentException e) {
                // This parser does not support XML Schema validation so leave it as
                // a non-validating parser.
            }
            return;
        }
        }
    }

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setXMLSchema(URL url) throws XMLPlatformException {
        if (null == url) {
            return;
        }
        try {
            documentBuilderFactory.setAttribute(SCHEMA_LANGUAGE, XML_SCHEMA);
            documentBuilderFactory.setAttribute(JAXP_SCHEMA_SOURCE, url.toString());
        } catch (IllegalArgumentException e) {
            // The attribute isn't supported so do nothing
        } catch (Exception e) {
            XMLPlatformException.xmlPlatformErrorResolvingXMLSchema(url, e);
        }
    }

    public void setXMLSchemas(Object[] schemas) throws XMLPlatformException {
        if ((null == schemas) || (schemas.length == 0)) {
            return;
        }
        try {
            documentBuilderFactory.setAttribute(SCHEMA_LANGUAGE, XML_SCHEMA);
            documentBuilderFactory.setAttribute(JAXP_SCHEMA_SOURCE, schemas);
        } catch (IllegalArgumentException e) {
            // The attribute isn't supported so do nothing
        } catch (Exception e) {
            XMLPlatformException.xmlPlatformErrorResolvingXMLSchemas(schemas, e);
        }
    }

    public Document parse(InputSource inputSource) throws XMLPlatformException {
        try {
            return getDocumentBuilder().parse(inputSource);
        } catch (SAXException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        } catch (IOException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        }
    }

    public Document parse(File file) throws XMLPlatformException {
        try {
            return getDocumentBuilder().parse(file);
        } catch (SAXParseException e) {
            throw XMLPlatformException.xmlPlatformSAXParseException(e);
        } catch (SAXException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        } catch (IOException e) {
            throw XMLPlatformException.xmlPlatformFileNotFoundException(file, e);
        }
    }

    public Document parse(InputStream inputStream) throws XMLPlatformException {
        try {
            return getDocumentBuilder().parse(inputStream);
        } catch (SAXParseException e) {
            throw XMLPlatformException.xmlPlatformSAXParseException(e);
        } catch (SAXException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        } catch (IOException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        }
    }

    public Document parse(Reader reader) throws XMLPlatformException {
        InputSource inputSource = new InputSource(reader);
        return parse(inputSource);
    }

    public Document parse(Source source) throws XMLPlatformException {
        XDKTransformer xformer = new XDKTransformer();
        DOMResult domResult = new DOMResult();
        xformer.transform(source, domResult);
        return (Document)domResult.getNode();
    }

    public Document parse(URL url) throws XMLPlatformException {
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
        } catch(IOException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        }
        
        boolean hasThrownException = false;
        try {
            return parse(inputStream);
        } catch(RuntimeException e) {
            hasThrownException = true;
            throw e;
        } finally {
            try {
                inputStream.close();
            } catch(IOException e) {
                if(!hasThrownException) {
                    //don't override runtime exception
                    throw XMLPlatformException.xmlPlatformParseException(e);
                }
            }
        }
    }

    private DocumentBuilder getDocumentBuilder() {
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(entityResolver);
            documentBuilder.setErrorHandler(errorHandler);
            return documentBuilder;
        } catch (ParserConfigurationException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        }
    }
    
    public void setXMLSchema(Schema schema) {
        this.documentBuilderFactory.setSchema(schema);
    }
    
    public Schema getXMLSchema() {
        return documentBuilderFactory.getSchema();
    }
}
