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

import org.eclipse.persistence.exceptions.i18n.*;
import java.util.*;
import java.io.*;

public class SessionLoaderException extends EclipseLinkException {
    public static final int FINAL_EXCEPTION = 9000;
    public static final int UNKNOWN_TAG = 9001;
    public static final int UNABLE_TO_LOAD_PROJECT_CLASS = 9002;
    public static final int UNABLE_TO_PROCESS_TAG = 9003;
    public static final int COULD_NOT_FIND_PROJECT_XML = 9004;
    public static final int FAILED_TO_LOAD_PROJECT_XML = 9005;
    public static final int UNABLE_TO_PARSE_XML = 9006;
    public static final int NON_PARSE_EXCEPTION = 9007;
    public static final int UN_EXPECTED_VALUE_OF_TAG = 9008;
    public static final int UNKNOWN_ATTRIBUTE_OF_TAG = 9009;
    public static final int XML_SCHEMA_PARSING_ERROR = 9010;
    public static final int SERVER_PLATFORM_NO_LONGER_SUPPORTED = 9011;
    public static final int INVALID_SESSION_XML = 9012;
    private Vector exceptionList;

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    public SessionLoaderException() {
        super();
    }

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    protected SessionLoaderException(String message) {
        super(message);
    }

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    protected SessionLoaderException(String message, Throwable internalException) {
        super(message);
        setInternalException(internalException);
    }

    public static SessionLoaderException couldNotFindProjectXml(String fileName) {
        Object[] args = { fileName };

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, COULD_NOT_FIND_PROJECT_XML, args));
        sessionLoaderException.setErrorCode(COULD_NOT_FIND_PROJECT_XML);
        return sessionLoaderException;
    }

    public static SessionLoaderException unkownTagAtNode(String tagName, String nodeName, Throwable exception) {
        Object[] args = { tagName, nodeName };

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, UNKNOWN_TAG, args), exception);
        sessionLoaderException.setErrorCode(UNKNOWN_TAG);
        return sessionLoaderException;
    }

    public static SessionLoaderException failedToLoadProjectClass(String className, Throwable exception) {
        Object[] args = { className };

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, UNABLE_TO_LOAD_PROJECT_CLASS, args), exception);
        sessionLoaderException.setErrorCode(UNABLE_TO_LOAD_PROJECT_CLASS);
        return sessionLoaderException;
    }

    public static SessionLoaderException failedToLoadProjectXml(String fileName, Throwable exception) {
        Object[] args = { fileName };

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, FAILED_TO_LOAD_PROJECT_XML, args), exception);
        sessionLoaderException.setErrorCode(FAILED_TO_LOAD_PROJECT_XML);
        return sessionLoaderException;
    }

    public static SessionLoaderException failedToLoadTag(String parentNode, String nodeValue, Throwable exception) {
        Object[] args = { parentNode, nodeValue };

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, UNABLE_TO_PROCESS_TAG, args), exception);
        sessionLoaderException.setErrorCode(UNABLE_TO_PROCESS_TAG);
        return sessionLoaderException;
    }

    public static SessionLoaderException finalException(Vector exceptionList) {
        Object[] args = { Integer.valueOf(exceptionList.size()) };

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, FINAL_EXCEPTION, args));
        sessionLoaderException.setErrorCode(FINAL_EXCEPTION);
        sessionLoaderException.setExceptionList(exceptionList);
        return sessionLoaderException;
    }

    //CR4142
    public static SessionLoaderException failedToParseXML(String message, int lineNumber, int columnNumber, Throwable exception) {
        Object[] args = { message, Integer.valueOf(lineNumber), Integer.valueOf(columnNumber) };

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, UNABLE_TO_PARSE_XML, args), exception);
        sessionLoaderException.setErrorCode(UNABLE_TO_PARSE_XML);
        return sessionLoaderException;
    }

    public static SessionLoaderException failedToParseXML(String message, Throwable exception) {
        Object[] args = { message };
        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, XML_SCHEMA_PARSING_ERROR, args), exception);
        sessionLoaderException.setErrorCode(XML_SCHEMA_PARSING_ERROR);
        return sessionLoaderException;
    }

    public static SessionLoaderException nonParseException(Throwable exception) {
        Object[] args = {  };

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, NON_PARSE_EXCEPTION, args), exception);
        sessionLoaderException.setErrorCode(NON_PARSE_EXCEPTION);
        return sessionLoaderException;
    }

    public static SessionLoaderException unexpectedValueOfTag(String nodeValue, String parentNode) {
        Object[] args = { nodeValue, parentNode };

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, UN_EXPECTED_VALUE_OF_TAG, args));
        sessionLoaderException.setErrorCode(UN_EXPECTED_VALUE_OF_TAG);
        return sessionLoaderException;
    }

    public static SessionLoaderException unknownAttributeOfTag(String nodeName) {
        Object[] args = { nodeName };

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, UNKNOWN_ATTRIBUTE_OF_TAG, args));
        sessionLoaderException.setErrorCode(UNKNOWN_ATTRIBUTE_OF_TAG);
        return sessionLoaderException;
    }

    public static SessionLoaderException serverPlatformNoLongerSupported(String serverPlatformClassName) {
        Object[] args = { serverPlatformClassName };

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, SERVER_PLATFORM_NO_LONGER_SUPPORTED, args));
        sessionLoaderException.setErrorCode(SERVER_PLATFORM_NO_LONGER_SUPPORTED);
        return sessionLoaderException;
    }

    public static SessionLoaderException InvalidSessionXML() {
        Object[] args = {};

        SessionLoaderException sessionLoaderException = new SessionLoaderException(ExceptionMessageGenerator.buildMessage(SessionLoaderException.class, INVALID_SESSION_XML, args));
        sessionLoaderException.setErrorCode(INVALID_SESSION_XML);
        return sessionLoaderException;
    }

    
    public void setExceptionList(Vector list) {
        this.exceptionList = list;
    }

    public Vector getExceptionList() {
        return this.exceptionList;
    }

    /**
     * PUBLIC:
     * Iterate through the exception list printing out the stack traces.
     */
    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
        if (getExceptionList() != null) {
            writer.println("SessionLoaderExceptions:");
            Iterator exceptionList = getExceptionList().iterator();
            while (exceptionList.hasNext()) {
                writer.write("***");
                writer.write(cr());
                ((Throwable)exceptionList.next()).printStackTrace(writer);
                writer.write(cr());
            }
        }

        writer.flush();
    }

    public String toString() {
        if (getErrorCode() == FINAL_EXCEPTION) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(getMessage());
            buffer.append(cr());
            Iterator exceptionList = getExceptionList().iterator();
            while (exceptionList.hasNext()) {
                buffer.append("***");
                buffer.append(cr());
                buffer.append(exceptionList.next().toString());
                buffer.append(cr());
            }
            return buffer.toString();
        } else {
            return super.toString();
        }
    }
}
