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
package org.eclipse.persistence.platform.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.xml.sax.SAXParseException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

public class XMLPlatformException extends EclipseLinkException {
    public static final int XML_PLATFORM_CLASS_NOT_FOUND = 27001;
    public static final int XML_PLATFORM_COULD_NOT_INSTANTIATE = 27002;
    public static final int XML_PLATFORM_COULD_NOT_CREATE_DOCUMENT = 27003;
    public static final int XML_PLATFORM_INVALID_XPATH = 27004;
    public static final int XML_PLATFORM_VALIDATION_EXCEPTION = 27005;
    public static final int XML_PLATFORM_PARSER_ERROR_RESOLVING_XML_SCHEMA = 27006;
    public static final int XML_PLATFORM_PARSE_EXCEPTION = 27101;
    public static final int XML_PLATFORM_PARSER_FILE_NOT_FOUND_EXCEPTION = 27102;
    public static final int XML_PLATFORM_PARSER_SAX_PARSE_EXCEPTION = 27103;
    public static final int XML_PLATFORM_TRANSFORM_EXCEPTION = 27201;
    public static final int XML_PLATFORM_INVALID_TYPE = 27202;

    protected XMLPlatformException(String message) {
        super(message);
    }

    public static XMLPlatformException xmlPlatformClassNotFound(String xmlPlatformClassName, Exception nestedException) {
        Object[] args = { xmlPlatformClassName };
        int errorCode = XML_PLATFORM_CLASS_NOT_FOUND;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLPlatformException xmlPlatformCouldNotInstantiate(String xmlPlatformClassName, Exception nestedException) {
        Object[] args = { xmlPlatformClassName };
        int errorCode = XML_PLATFORM_COULD_NOT_INSTANTIATE;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLPlatformException xmlPlatformCouldNotCreateDocument(Exception nestedException) {
        Object[] args = {  };
        int errorCode = XML_PLATFORM_COULD_NOT_CREATE_DOCUMENT;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        return exception;
    }

    public static XMLPlatformException xmlPlatformInvalidXPath(Exception nestedException) {
        Object[] args = {  };
        int errorCode = XML_PLATFORM_INVALID_XPATH;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLPlatformException xmlPlatformValidationException(Exception nestedException) {
        Object[] args = {  };
        int errorCode = XML_PLATFORM_VALIDATION_EXCEPTION;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        exception.setInternalException(nestedException);
        return exception;
    }

    /**
     * Takes an error messsage string
     */
    public static XMLPlatformException xmlPlatformValidationException(String errorMessage) {
        int errorCode = XML_PLATFORM_VALIDATION_EXCEPTION;
        XMLPlatformException exception = new XMLPlatformException(errorMessage);
        exception.setErrorCode(errorCode);
        return exception;
    }

    /**
     * Handles an invalid type setting in a schema reference.
     *
     * @see org.eclipse.persistence.platform.xml.XMLSchemaReference.getType()
     */
    public static XMLPlatformException xmlPlatformInvalidTypeException(int type) {
        Object[] args = { Integer.valueOf(type) };
        int errorCode = XML_PLATFORM_INVALID_TYPE;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        return exception;
    }

    public static XMLPlatformException xmlPlatformParseException(Exception nestedException) {
        Object[] args = {  };
        int errorCode = XML_PLATFORM_PARSE_EXCEPTION;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLPlatformException xmlPlatformFileNotFoundException(File file, IOException nestedException) {
        Object[] args = { file.getAbsolutePath() };
        int errorCode = XML_PLATFORM_PARSER_FILE_NOT_FOUND_EXCEPTION;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLPlatformException xmlPlatformSAXParseException(SAXParseException nestedException) {
        Object[] args = { Integer.valueOf(nestedException.getLineNumber()), nestedException.getSystemId(), nestedException.getMessage() };
        int errorCode = XML_PLATFORM_PARSER_SAX_PARSE_EXCEPTION;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLPlatformException xmlPlatformErrorResolvingXMLSchema(URL url, Exception nestedException) {
        Object[] args = { url };
        int errorCode = XML_PLATFORM_PARSER_ERROR_RESOLVING_XML_SCHEMA;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLPlatformException xmlPlatformErrorResolvingXMLSchemas(Object[] schemas, Exception nestedException) {
        Object[] args = {  };
        int errorCode = XML_PLATFORM_PARSER_ERROR_RESOLVING_XML_SCHEMA;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLPlatformException xmlPlatformTransformException(Exception nestedException) {
        Object[] args = {  };
        int errorCode = XML_PLATFORM_TRANSFORM_EXCEPTION;
        XMLPlatformException exception = new XMLPlatformException(ExceptionMessageGenerator.buildMessage(XMLPlatformException.class, errorCode, args));
        exception.setErrorCode(errorCode);
        exception.setInternalException(nestedException);
        return exception;
    }
}
