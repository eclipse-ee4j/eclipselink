/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.exceptions;

import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

public class JPARSException extends EclipseLinkException {
    // Next range should start from LAST_ERROR_CODE (62000). 
    // The JPA-RS uses error codes between 61000-61999 (both inclusive).
    public enum ErrorCode {
        ENTITY_NOT_FOUND(61000),
        OBJECT_REFERRED_BY_LINK_DOES_NOT_EXIST(61001),
        INVALID_CONFIGURATION(61002),

        // wraps eclipselink exceptions    
        AN_EXCEPTION_OCCURRED(61999),

        // end marker for JPA-RS error codes    
        LAST_ERROR_CODE(62000);
        private int value;

        private ErrorCode(int value) {
            this.value = value;
        }

        /**
         * Value.
         *
         * @return the string
         */
        public String value() {
            return String.valueOf(value);
        }
    };

    private int httpStatusCode;

    /**
     * Instantiates a new JPARS exception.
     */
    public JPARSException() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.exceptions.EclipseLinkException#getMessage()
     */
    @Override
    public String getMessage() {
        return super.getUnformattedMessage();
    }

    /**
     * Gets the http status code.
     *
     * @return the http status code
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Sets the http status code.
     *
     * @param httpStatusCode the new http status code
     */
    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    private JPARSException(String message) {
        super(message);
    }

    private JPARSException(Throwable internalException) {
        super(ExceptionMessageGenerator.buildMessage(JPARSException.class, ErrorCode.AN_EXCEPTION_OCCURRED.value, new Object[] {}), internalException);
    }

    private JPARSException(String msg, Throwable internalException) {
        super(msg, internalException);
    }

    /**
     * Entity not found.
     *
     * @param requestId the request id
     * @param httpStatusCode the http status code
     * @param entityType the entity type
     * @param entityId the entity id
     * @param persistenceUnit the persistence unit
     * @return the jPARS exception
     */
    public static JPARSException entityNotFound(int httpStatusCode, String entityType, String entityId, String persistenceUnit) {
        Object[] args = { entityType, entityId, persistenceUnit };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, ErrorCode.ENTITY_NOT_FOUND.value, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(ErrorCode.ENTITY_NOT_FOUND.value);
        exception.setHttpStatusCode(httpStatusCode);

        return exception;
    }

    /**
     * Object referred by link does not exist.
     *
     * @param requestId the request id
     * @param httpStatusCode the http status code
     * @param entityType the entity type
     * @param entityId the entity id
     * @return the jPARS exception
     */
    public static JPARSException objectReferredByLinkDoesNotExist(int httpStatusCode, String entityType, Object entityId) {
        Object[] args = { entityType, entityId };

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, ErrorCode.OBJECT_REFERRED_BY_LINK_DOES_NOT_EXIST.value, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(ErrorCode.OBJECT_REFERRED_BY_LINK_DOES_NOT_EXIST.value);
        exception.setHttpStatusCode(httpStatusCode);

        return exception;
    }

    /**
     * Invalid configuration.
     *
     * @param requestId the request id
     * @param httpStatusCode the http status code
     * @return the jPARS exception
     */
    public static JPARSException invalidConfiguration(int httpStatusCode) {
        Object[] args = {};

        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, ErrorCode.INVALID_CONFIGURATION.value, args);
        JPARSException exception = new JPARSException(msg);
        exception.setErrorCode(ErrorCode.INVALID_CONFIGURATION.value);
        exception.setHttpStatusCode(httpStatusCode);

        return exception;
    }

    /**
     * Exception occurred.
     *
     * @param requestId the request id
     * @param httpStatusCode the http status code
     * @param exception the exception
     * @return the jPARS exception
     */
    public static JPARSException exceptionOccurred(Exception exception) {
        int errorCode = ErrorCode.AN_EXCEPTION_OCCURRED.value;
        String msg = ExceptionMessageGenerator.buildMessage(JPARSException.class, ErrorCode.AN_EXCEPTION_OCCURRED.value, new Object[] { exception.getClass().getSimpleName() }).trim();

        if (exception instanceof EclipseLinkException) {
            errorCode = ((EclipseLinkException) exception).getErrorCode();
            msg = ((EclipseLinkException) exception).getClass().getName().trim();
        } else if (exception.getCause() instanceof EclipseLinkException) {
            errorCode = ((EclipseLinkException) (exception.getCause())).getErrorCode();
            msg = ((EclipseLinkException) (exception.getCause())).getClass().getName().trim();
        }

        JPARSException jparsException = new JPARSException(msg, exception);
        jparsException.setErrorCode(errorCode);
        jparsException.setInternalException(exception);

        return jparsException;
    }
}
