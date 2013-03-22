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

import java.io.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * <p>
 * <b>Purpose</b>: Any exception raised by EclipseLink should be a subclass of this exception class.
 */
public abstract class EclipseLinkException extends RuntimeException {
    protected transient AbstractSession session;
    protected Throwable internalException;
    protected static Boolean shouldPrintInternalException = null;
    protected String indentationString;
    protected int errorCode;
    protected static final String CR = System.getProperty("line.separator");
    //Bug#3559280  Added to avoid logging an exception twice
    protected boolean hasBeenLogged;

    /**
     * INTERNAL:
     * Return a new exception.
     */
    public EclipseLinkException() {
        this("");
    }

    /**
     * INTERNAL:
     * EclipseLink exception should only be thrown by EclipseLink.
     */
    public EclipseLinkException(String theMessage) {
        super(theMessage);
        this.indentationString = "";
        hasBeenLogged = false;
    }

    /**
     * INTERNAL:
     * EclipseLink exception should only be thrown by EclipseLink.
     */
    public EclipseLinkException(String message, Throwable internalException) {
        this(message);
        setInternalException(internalException);
    }

    /**
     * INTERNAL:
     * Convenience method - return a platform-specific line-feed.
     */
    protected static String cr() {
        return org.eclipse.persistence.internal.helper.Helper.cr();
    }

    /**
     * PUBLIC:
     * Return the exception error code.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * INTERNAL:
     * Used to print things nicely in the testing tool.
     */
    public String getIndentationString() {
        return indentationString;
    }

    /**
     * PUBLIC:
     * Return the internal native exception.
     * EclipseLink frequently catches Java exceptions and wraps them in its own exception
     * classes to provide more information.
     * The internal exception can still be accessed if required.
     */
    public Throwable getInternalException() {
        return internalException;
    }

    /**
     * PUBLIC:
     * Return the exception error message.
     * EclipseLink error messages are multi-line so that detail descriptions of the exception are given.
     */
    public String getMessage() {
        StringWriter writer = new StringWriter(100);

        // Avoid printing internal exception error message twice.
        if ((getInternalException() == null) || ((super.getMessage() != null) && !super.getMessage().equals(getInternalException().toString()))) {
            writer.write(cr());
            writer.write(getIndentationString());
            writer.write(ExceptionMessageGenerator.getHeader("DescriptionHeader"));
            writer.write(super.getMessage());
        }

        if (getInternalException() != null) {
            writer.write(cr());
            writer.write(getIndentationString());
            writer.write(ExceptionMessageGenerator.getHeader("InternalExceptionHeader"));
            writer.write(getInternalException().toString());

            if ((getInternalException() instanceof java.lang.reflect.InvocationTargetException) && ((((java.lang.reflect.InvocationTargetException)getInternalException()).getTargetException()) != null)) {
                writer.write(cr());
                writer.write(getIndentationString());
                writer.write(ExceptionMessageGenerator.getHeader("TargetInvocationExceptionHeader"));
                writer.write(((java.lang.reflect.InvocationTargetException)getInternalException()).getTargetException().toString());
            }
        }

        return writer.toString();
    }

    /**
     * PUBLIC:
     * Return the session.
     */
    public AbstractSession getSession() {
        return session;
    }

    /**
     * INTERNAL:
     * Return if this exception has been logged to avoid being logged more than once.
     */
    public boolean hasBeenLogged() {
        return hasBeenLogged;
    }

    /**
     * PUBLIC:
     * Print both the normal and internal stack traces.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * PUBLIC:
     * Print both the normal and internal stack traces.
     */
    public void printStackTrace(PrintStream outStream) {
        printStackTrace(new PrintWriter(outStream));
    }

    /**
     * PUBLIC:
     * Print both the normal and internal stack traces.
     */
    public void printStackTrace(PrintWriter writer) {
        writer.write(ExceptionMessageGenerator.getHeader("LocalExceptionStackHeader"));
        writer.write(cr());
        super.printStackTrace(writer);

        if ((getInternalException() != null) && shouldPrintInternalException()) {
            writer.write(ExceptionMessageGenerator.getHeader("InternalExceptionStackHeader"));
            writer.write(cr());
            getInternalException().printStackTrace(writer);

            if ((getInternalException() instanceof java.lang.reflect.InvocationTargetException) && ((((java.lang.reflect.InvocationTargetException)getInternalException()).getTargetException()) != null)) {
                writer.write(ExceptionMessageGenerator.getHeader("TargetInvocationExceptionStackHeader"));
                writer.write(cr());
                ((java.lang.reflect.InvocationTargetException)getInternalException()).getTargetException().printStackTrace(writer);
            }
        }
        writer.flush();
    }

    /**
     * INTERNAL:
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * INTERNAL:
     * Set this flag to avoid logging an exception more than once.
     */
    public void setHasBeenLogged(boolean logged) {
        this.hasBeenLogged = logged;
    }

    /**
     * INTERNAL:
     * Used to print things nicely in the testing tool.
     */
    public void setIndentationString(String indentationString) {
        this.indentationString = indentationString;
    }

    /**
     * INTERNAL:
     * Used to specify the internal exception.
     */
    public void setInternalException(Throwable exception) {
        internalException = exception;
        if (getCause() == null) {
            initCause(exception);
        }
    }

    /**
     *  INTERNAL:
     */
    public void setSession(AbstractSession session) {
        this.session = session;
    }

    /**
     * PUBLIC:
     * Allows overriding of EclipseLink's exception chaining detection.
     * @param booleam printException - If printException is true, the EclipseLink-stored
     * Internal exception will be included in a stack trace or in the exception message of a EclipseLinkException.
     * If printException is false, the EclipseLink-stored Internal Exception will not be included
     * in the stack trace or the exception message of EclipseLinkExceptions
     */
    public static void setShouldPrintInternalException(boolean printException) {
        shouldPrintInternalException = Boolean.valueOf(printException);
    }

    /**
     * INTERNAL
     * Check to see if the EclipseLink-stored internal exception should be printed in this
     * a EclipseLinkException's stack trace.  This method will check the static ShouldPrintInternalException
     * variable and if it is not set, estimate based on the JDK version used.
     */
    public static boolean shouldPrintInternalException() {
        if (shouldPrintInternalException == null) {
            shouldPrintInternalException = Boolean.FALSE;
        }
        return shouldPrintInternalException.booleanValue();
    }

    /**
     * INTERNAL:
     */
    public String toString() {
        return getIndentationString() + ExceptionMessageGenerator.getHeader("ExceptionHeader") + getErrorCode() + "] (" + org.eclipse.persistence.sessions.DatabaseLogin.getVersion() + "): " + getClass().getName() + getMessage();
    }
}
