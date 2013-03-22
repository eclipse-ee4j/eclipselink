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
package org.eclipse.persistence.exceptions;

import java.net.URL;

import org.eclipse.persistence.exceptions.i18n.*;

public class XMLParseException extends EclipseLinkException {

    public static final int EXCEPTION_CREATING_DOCUMENT_BUILDER = 34000;
    public static final int EXCEPTION_READING_XML_DOCUMENT = 34001;
    public static final int EXCEPTION_CREATING_SAX_PARSER = 34002;
    public static final int EXCEPTION_CREATING_XML_READER = 34003;
    public static final int EXCEPTION_SETTING_SCHEMA_SOURCE = 34004;

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    public XMLParseException() {
        super();
    }

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    protected XMLParseException(String message) {
        super(message);
    }

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    protected XMLParseException(String message, Throwable internalException) {
        super(message);
        setInternalException(internalException);
    }

	/**
	 * INTERNAL:
	 */
    public static XMLParseException exceptionCreatingDocumentBuilder(String xmlDocument, Exception cause) {
    	return XMLParseException.getXMLParseException(new Object[] {xmlDocument}, cause, EXCEPTION_CREATING_DOCUMENT_BUILDER);
    }

	/**
	 * INTERNAL:
	 */
    public static XMLParseException exceptionCreatingSAXParser(URL url, Exception cause) {
    	return XMLParseException.getXMLParseException(new Object[] {url}, cause, EXCEPTION_CREATING_SAX_PARSER);
    }

	/**
	 * INTERNAL:
	 */
    public static XMLParseException exceptionCreatingXMLReader(URL url, Exception cause) {
    	return XMLParseException.getXMLParseException(new Object[] {url}, cause, EXCEPTION_CREATING_XML_READER);
    }

    /**
	 * INTERNAL:
	 */
    public static XMLParseException exceptionReadingXMLDocument(String xmlDocument, Exception cause) {
    	return XMLParseException.getXMLParseException(new Object[] {xmlDocument}, cause, EXCEPTION_READING_XML_DOCUMENT);
    }

	/**
	 * INTERNAL:
	 */
    public static XMLParseException exceptionSettingSchemaSource(URL baseUrl, URL schemaUrl, Exception cause) {
    	return XMLParseException.getXMLParseException(new Object[] {baseUrl, schemaUrl}, cause, EXCEPTION_SETTING_SCHEMA_SOURCE);
    }

    /*
     * INTERNAL: 
     */
    private static XMLParseException getXMLParseException(Object[] args, Exception cause, int errorCode) {
        XMLParseException parseException = new XMLParseException(ExceptionMessageGenerator.buildMessage(XMLParseException.class, errorCode, args), cause);
        parseException.setErrorCode(errorCode);
        return parseException;
    }
}
