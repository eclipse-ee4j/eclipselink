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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.platform.xml.jaxp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.validation.Schema;

import org.eclipse.persistence.internal.helper.XMLHelper;
import org.eclipse.persistence.platform.xml.DefaultErrorHandler;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <p><b>Purpose</b>:  An implementation of XMLParser using JAXP 1.3 APIs.</p>
 *
 * <p>JAXPParser is NOT thread safe.</p>
 */
public class JAXPParser implements XMLParser {
    private static final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    private DocumentBuilderFactory documentBuilderFactory;
    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;
    private DocumentBuilder documentBuilder;
    private TransformerFactory transformerFactory;

    /**
     * Default constructor.
     */
    public JAXPParser() {
        super();
        errorHandler = DefaultErrorHandler.getInstance();
    }

    /**
     * This constructor can increase performance by providing existing documentBuilderFactory and errorHandler.
     *
     * @param documentBuilderFactory existing document builder factory
     * @param errorHandler existing error handler
     */
    public JAXPParser(DocumentBuilderFactory documentBuilderFactory, ErrorHandler errorHandler) {
        super();

        this.documentBuilderFactory = documentBuilderFactory;
        if (null != errorHandler) {
            this.errorHandler = errorHandler;
        } else {
            this.errorHandler = DefaultErrorHandler.getInstance();
        }
    }

    /**
     * This constructor provides way to specify features for parser.
     *
     * @param parserFeatures features for parser
     */
    public JAXPParser(Map<String, Boolean> parserFeatures) {
        this();
        loadDocumentBuilderFactory();
        try {
            if(null != parserFeatures) {
                for(Entry<String, Boolean> entry : parserFeatures.entrySet()) {
                    documentBuilderFactory.setFeature(entry.getKey(), entry.getValue());
                }
            }
        } catch(Exception e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        }
    }

    private void loadDocumentBuilderFactory() {
        documentBuilderFactory = XMLHelper.createDocumentBuilderFactory(false);
        setNamespaceAware(true);
        setWhitespacePreserving(false);
    }

    /**
     * Changes namespaceAware behavior of the parser.
     *
     * @param isNamespaceAware if the parser should be namespace aware
     */
    @Override
    public void setNamespaceAware(boolean isNamespaceAware) {
        if (null == documentBuilderFactory) {
            loadDocumentBuilderFactory();
        }
        documentBuilderFactory.setNamespaceAware(isNamespaceAware);
    }

    /**
     * Changes preservation of white spaces.
     *
     * @param isWhitespacePreserving if the parser should preserve white spaces
     */
    @Override
    public void setWhitespacePreserving(boolean isWhitespacePreserving) {
        if (null == documentBuilderFactory) {
            loadDocumentBuilderFactory();
        }
        documentBuilderFactory.setIgnoringElementContentWhitespace(!isWhitespacePreserving);
    }

    /**
     * Returns validtion mode of the parser.
     *
     * @return validation mode of the parser
     */
    @Override
    public int getValidationMode() {
        if (null == documentBuilderFactory) {
            loadDocumentBuilderFactory();
        }
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

    /**
     * Sets validation mode of the parser.
     *
     * @param validationMode validation mode to set
     */
    @Override
    public void setValidationMode(int validationMode) {
        if (null == documentBuilderFactory) {
            loadDocumentBuilderFactory();
        }
        switch (validationMode) {
        case XMLParser.NONVALIDATING: {
            documentBuilderFactory.setValidating(false);
            // documentBuilderFactory.setAttribute(SCHEMA_LANGUAGE, null);
            return;
        }
        case XMLParser.DTD_VALIDATION: {
            documentBuilderFactory.setValidating(true);
            XMLHelper.allowExternalDTDAccess(documentBuilderFactory, "all", false);
            // documentBuilderFactory.setAttribute(SCHEMA_LANGUAGE, null);
            return;
        }
        case XMLParser.SCHEMA_VALIDATION: {
            try {
                documentBuilderFactory.setAttribute(SCHEMA_LANGUAGE, XML_SCHEMA);
                documentBuilderFactory.setValidating(true);
                XMLHelper.allowExternalAccess(documentBuilderFactory, "all", false);
            } catch (IllegalArgumentException e) {
                // This parser does not support XML Schema validation so leave it as
                // a non-validating parser.
            }
            return;
        }
        }
    }

    /**
     * Returns entity resolver of the parser.
     *
     * @return entity resolver of the parser
     */
    @Override
    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    /**
     * Sets entity resolver for the parser.
     *
     * @param entityResolver entity resolver to set
     */
    @Override
    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    /**
     * Returns error handler of the parser.
     *
     * @return error handler of the parser
     */
    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Sets error handler for the parser.
     *
     * @param errorHandler error handler for the parser
     */
    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Sets XML Schema for the parser.
     *
     * @param url url of the XMLSchema
     * @throws XMLPlatformException exception occurred while setting XMLSchema
     */
    @Override
    public void setXMLSchema(URL url) throws XMLPlatformException {
        if (null == url) {
            return;
        }
        if (null == documentBuilderFactory) {
            loadDocumentBuilderFactory();
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

    /**
     * Sets XML Schema for the parser.
     *
     * @param schema schema for the parser
     * @throws XMLPlatformException exception occurred while setting XMLSchema
     */
    @Override
    public void setXMLSchema(Schema schema) throws XMLPlatformException {
        documentBuilderFactory.setSchema(schema);
    }

    /**
     * Returns XML Schema of the parser.
     *
     * @return schema of the parser
     * @throws XMLPlatformException exception occurred while getting XMLSchema
     */
    @Override
    public Schema getXMLSchema() throws XMLPlatformException {
        return documentBuilderFactory.getSchema();
    }

    /**
     * Sets XML Schema(s) for the parser.
     *
     * @param schemas XML schemas to set
     * @throws XMLPlatformException exception occurred while setting XMLSchema(s)
     */
    @Override
    public void setXMLSchemas(Object[] schemas) throws XMLPlatformException {
        if ((null == schemas) || (schemas.length == 0)) {
            return;
        }
        if (null == documentBuilderFactory) {
            loadDocumentBuilderFactory();
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

    /**
     * Parses given input source.
     *
     * @param inputSource input source to parse
     * @return parsed document
     * @throws XMLPlatformException exception occurred while parsing input source
     */
    @Override
    public Document parse(InputSource inputSource) throws XMLPlatformException {
        try {
            return getDocumentBuilder().parse(inputSource);
        } catch (SAXException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        } catch (IOException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        }
    }

    /**
     * Parses given file.
     *
     * @param file file to parse
     * @return parsed document
     * @throws XMLPlatformException exception occurred while parsing given file
     */
    @Override
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

    /**
     * Parses given input stream.
     *
     * @param inputStream input stream to parse
     * @return parsed document
     * @throws XMLPlatformException exception occurred while parsing input stream
     */
    @Override
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

    /**
     * Parses given reader.
     *
     * @param reader reader to parse
     * @return parsed document
     * @throws XMLPlatformException exception occurred while parsing given reader
     */
    @Override
    public Document parse(Reader reader) throws XMLPlatformException {
        InputSource inputSource = new InputSource(reader);
        return parse(inputSource);
    }

    /**
     * Parses given source.
     *
     * @param source source to parse
     * @return parsed document
     * @throws XMLPlatformException exception occurred while parsing given source
     */
    @Override
    public Document parse(Source source) throws XMLPlatformException {
        try {
            if (null == transformerFactory) {
                transformerFactory = XMLHelper.createTransformerFactory(false);
            }
            Transformer transformer = transformerFactory.newTransformer();
            SAXResult saxResult = new SAXResult();
            SAXDocumentBuilder builder = new SAXDocumentBuilder();
            saxResult.setHandler(builder);
            transformer.transform(source, saxResult);
            return builder.getDocument();
        } catch (TransformerException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        }
    }

    /**
     * Parses given url.
     *
     * @param url url to parse
     * @return parsed document
     * @throws XMLPlatformException exception occurred while parsing stream with given url
     */
    @Override
    public Document parse(URL url) throws XMLPlatformException {
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
        } catch (IllegalArgumentException | IOException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        }

        boolean hasThrownException = false;
        try {
            return parse(inputStream);
        } catch (RuntimeException e) {
            hasThrownException = true;
            throw e;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                if (!hasThrownException) {
                    //don't override runtime exception
                    throw XMLPlatformException.xmlPlatformParseException(e);
                }
            }
        }
    }

    private DocumentBuilder getDocumentBuilder() {
        try {
            if (null == documentBuilder) {
                documentBuilder = getNewDocumentBuilder();
            } else {
                try {
                    documentBuilder.reset();
                } catch (UnsupportedOperationException uoe) {
                    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=490229
                    // if reset is not supported by the parser, just return new one
                    documentBuilder = getNewDocumentBuilder();
                }
            }

            return documentBuilder;
        } catch (ParserConfigurationException e) {
            throw XMLPlatformException.xmlPlatformParseException(e);
        }
    }

    private DocumentBuilder getNewDocumentBuilder() throws ParserConfigurationException {
        if (null == documentBuilderFactory) {
            loadDocumentBuilderFactory();
        }
        DocumentBuilder newDb = documentBuilderFactory.newDocumentBuilder();
        newDb.setEntityResolver(entityResolver);
        newDb.setErrorHandler(errorHandler);
        return newDb;
    }
}
