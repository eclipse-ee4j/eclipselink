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

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * <p><b>PUBLIC</b>: runtime exception for EclipseLink DBWS Service
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
@SuppressWarnings("serial")
public class DBWSException extends EclipseLinkException {

    public final static int COULD_NOT_LOCATE_FILE = 47000;
    public final static int COULD_NOT_LOCATE_DESCRIPTOR = 47001;
    public final static int COULD_NOT_LOCATE_QUERY_FOR_DESCRIPTOR = 47002;
    public final static int COULD_NOT_LOCATE_QUERY_FOR_SESSION = 47003;
    public final static int PARAMETER_DOES_NOT_EXIST_FOR_OPERATION = 47004;
    public final static int PARAMETER_HAS_NO_MAPPING = 47005;
    public final static int RESULT_DOES_NOT_EXIST_FOR_OPERATION = 47006;
    public final static int RESULT_HAS_NO_MAPPING = 47007;
    public final static int MULTIPLE_OUTPUT_ARGUMENTS_ONLY_FOR_SIMPLE_XML = 47008;
    public final static int INOUT_CURSOR_ARGUMENTS_NOT_SUPPORTED = 47009;
    public final static int COULD_NOT_LOCATE_OR_SESSION_FOR_SERVICE = 47010;
    public final static int COULD_NOT_LOCATE_OX_SESSION_FOR_SERVICE = 47011;
    public final static int COULD_NOT_PARSE_DBWS_FILE = 47012;

    public DBWSException() {
        super();
    }

    public DBWSException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBWSException(String message) {
        super(message);
    }
    
    /**
     * INTERNAL:
     * Exception when reading DBWS metadata and the given file cannot be located
     */
    public static DBWSException couldNotLocateFile(String missingFile) {
        Object[] args = { missingFile };
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, COULD_NOT_LOCATE_FILE, args));
        exception.setErrorCode(COULD_NOT_LOCATE_FILE);
        return exception;
    }
    
    /**
     * INTERNAL:
     * Exception when processing DBWS metadata and the descriptor cannot be located for the operation
     */
    public static DBWSException couldNotLocateDescriptorForOperation(String descriptor, String operation) {
        Object[] args = { descriptor, operation };
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, COULD_NOT_LOCATE_DESCRIPTOR, args));
        exception.setErrorCode(COULD_NOT_LOCATE_DESCRIPTOR);
        return exception;
    }
    
    /**
     * INTERNAL:
     * Exception when processing DBWS metadata and the descriptor cannot be located for the operation
     */
    public static DBWSException couldNotLocateQueryForDescriptor(String query, String descriptor) {
        Object[] args = { query, descriptor };
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, COULD_NOT_LOCATE_QUERY_FOR_DESCRIPTOR, args));
        exception.setErrorCode(COULD_NOT_LOCATE_QUERY_FOR_DESCRIPTOR);
        return exception;
    }
    
    /**
     * INTERNAL:
     * Exception when processing DBWS metadata and the descriptor cannot be located for the operation
     */
    public static DBWSException couldNotLocateQueryForSession(String query, String session) {
        Object[] args = { query, session };
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, COULD_NOT_LOCATE_QUERY_FOR_SESSION, args));
        exception.setErrorCode(COULD_NOT_LOCATE_QUERY_FOR_SESSION);
        return exception;
    }
    
    /**
     * INTERNAL:
     * Exception when processing DBWS metadata and the descriptor cannot be located for the operation
     */
    public static DBWSException parameterDoesNotExistForOperation(String parameterType, String operation) {
        Object[] args = { parameterType, operation };
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, PARAMETER_DOES_NOT_EXIST_FOR_OPERATION, args));
        exception.setErrorCode(PARAMETER_DOES_NOT_EXIST_FOR_OPERATION);
        return exception;
    }
    
    /**
     * INTERNAL:
     * Exception when processing DBWS metadata and the descriptor cannot be located for the operation
     */
    public static DBWSException parameterHasNoMapping(String parameterType, String operation) {
        Object[] args = { parameterType, operation };
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, PARAMETER_HAS_NO_MAPPING, args));
        exception.setErrorCode(PARAMETER_HAS_NO_MAPPING);
        return exception;
    }
    
    /**
     * INTERNAL:
     * Exception when processing DBWS metadata and the descriptor cannot be located for the operation
     */
    public static DBWSException resultDoesNotExistForOperation(String resultType, String operation) {
        Object[] args = { resultType, operation };
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, RESULT_DOES_NOT_EXIST_FOR_OPERATION, args));
        exception.setErrorCode(RESULT_DOES_NOT_EXIST_FOR_OPERATION);
        return exception;
    }
    
    /**
     * INTERNAL:
     * Exception when processing DBWS metadata and the descriptor cannot be located for the operation
     */
    public static DBWSException resultHasNoMapping(String resultType, String operation) {
        Object[] args = { resultType, operation };
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, RESULT_HAS_NO_MAPPING, args));
        exception.setErrorCode(RESULT_HAS_NO_MAPPING);
        return exception;
    }
    
    /**
     * INTERNAL:
     * Exception when processing DBWS metadata and the descriptor cannot be located for the operation
     */
    public static DBWSException multipleOutputArgumentsOnlySupportedForSimpleXML() {
        Object[] args = {};
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, MULTIPLE_OUTPUT_ARGUMENTS_ONLY_FOR_SIMPLE_XML, args));
        exception.setErrorCode(MULTIPLE_OUTPUT_ARGUMENTS_ONLY_FOR_SIMPLE_XML);
        return exception;
    }
    
    /**
     * INTERNAL:
     * Exception when processing DBWS metadata and the descriptor cannot be located for the operation
     */
    public static DBWSException inoutCursorArgumentsNotSupported() {
        Object[] args = {};
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, INOUT_CURSOR_ARGUMENTS_NOT_SUPPORTED, args));
        exception.setErrorCode(INOUT_CURSOR_ARGUMENTS_NOT_SUPPORTED);
        return exception;
    }

    /**
     * INTERNAL:
     * Exception when reading DBWS metadata and the given file cannot be located
     */
    public static DBWSException couldNotLocateORSessionForService(String serviceName) {
        Object[] args = { serviceName };
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, COULD_NOT_LOCATE_OR_SESSION_FOR_SERVICE, args));
        exception.setErrorCode(COULD_NOT_LOCATE_OR_SESSION_FOR_SERVICE);
        return exception;
    }

    /**
     * INTERNAL:
     * Exception when reading DBWS metadata and the given file cannot be located
     */
    public static DBWSException couldNotLocateOXSessionForService(String serviceName) {
        Object[] args = { serviceName };
        DBWSException exception =
        new DBWSException(ExceptionMessageGenerator.buildMessage(
            DBWSException.class, COULD_NOT_LOCATE_OX_SESSION_FOR_SERVICE, args));
        exception.setErrorCode(COULD_NOT_LOCATE_OX_SESSION_FOR_SERVICE);
        return exception;
    }

    public static DBWSException couldNotParseDBWSFile() {
        Object[] args = {};
        DBWSException exception =
            new DBWSException(ExceptionMessageGenerator.buildMessage(
                DBWSException.class, COULD_NOT_PARSE_DBWS_FILE, args));
            exception.setErrorCode(COULD_NOT_PARSE_DBWS_FILE);
            return exception;
    }

}
