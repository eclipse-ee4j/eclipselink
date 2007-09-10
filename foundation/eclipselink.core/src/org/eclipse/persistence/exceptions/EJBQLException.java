/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.exceptions;

import java.util.*;
import org.eclipse.persistence.exceptions.i18n.*;

/**
 * <P><B>Purpose</B>: EJBQL parsing and resolution problems will raise this exception
 */
public class EJBQLException extends EclipseLinkException {
    public static final int recognitionException = 8001;
    public static final int generalParsingException = 8002;
    public static final int classNotFoundException = 8003;
    public static final int aliasResolutionException = 8004;
    public static final int resolutionClassNotFoundException = 8005;
    public static final int missingDescriptorException = 8006;
    public static final int missingMappingException = 8007;
    public static final int invalidContextKeyException = 8008;
    public static final int expressionNotSupported = 8009;
    public static final int generalParsingException2 = 8010;
    public static final int invalidCollectionMemberDecl = 8011;
    public static final int notYetImplemented = 8012;
    public static final int constructorClassNotFound = 8013;
    public static final int invalidSizeArgument = 8014;
    public static final int invalidEnumLiteral = 8015;
    public static final int invalidSelectForGroupByQuery = 8016;
    public static final int invalidHavingExpression = 8017;
    public static final int invalidMultipleUseOfSameParameter = 8018;
    public static final int multipleVariableDeclaration = 8019;
    public static final int invalidFunctionArgument = 8020;
    public static final int expectedOrderableOrderByItem = 8021;
    public static final int invalidExpressionArgument = 8022;
    public static final int syntaxError = 8023;
    public static final int syntaxErrorAt = 8024;
    public static final int unexpectedToken = 8025;
    public static final int unexpectedChar = 8026;
    public static final int expectedCharFound = 8027;
    public static final int unexpectedEOF = 8028;
    public static final int invalidNavigation = 8029;
    public static final int unknownAttribute = 8030;
    public Collection internalExceptions = null;

    /**
    * INTERNAL
    * Only TopLink can throw and create these excpetions
    */
    protected EJBQLException() {
        super();
    }

    /**
    * INTERNAL
    * Only TopLink can throw and create these excpetions
    */
    protected EJBQLException(String theMessage) {
        super(theMessage);
    }

    /**
    * INTERNAL
    * Only TopLink can throw and create these excpetions
    */
    protected EJBQLException(String message, Exception internalException) {
        super(message, internalException);
    }

    /**
    * INTERNAL
    * Only TopLink can throw and create these excpetions
    */
    protected EJBQLException(String message, Exception internalException, int theErrorCode) {
        this(message, internalException);
        this.setErrorCode(theErrorCode);
    }

    /**
     * INTERNAL
     * Create an exception to wrap the recognition exception thrown
     */
    public static EJBQLException recognitionException(String theEjbql, String theMessage) {
        Object[] args = { theEjbql, theMessage };

        String message = ExceptionMessageGenerator.buildMessage(EJBQLException.class, recognitionException, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(recognitionException);
        return exception;
    }

    /**
     * INTERNAL
     * Create an exception to wrap a general parsing exception
     */
    public static EJBQLException generalParsingException(String theEjbql, Exception theException) {
        Object[] args = { theEjbql, theException.getMessage() };

        String message = ExceptionMessageGenerator.buildMessage(EJBQLException.class, generalParsingException, args);
        EJBQLException exception = new EJBQLException(message, theException, generalParsingException);
        exception.setErrorCode(generalParsingException);
        return exception;
    }

    /**
     * INTERNAL
     * Create an exception to wrap a general parsing exception
     */
    public static EJBQLException generalParsingException(String theEjbql) {
        Object[] args = { theEjbql };

        String message = ExceptionMessageGenerator.buildMessage(EJBQLException.class, generalParsingException2, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(generalParsingException);
        return exception;
    }

    public static EJBQLException classNotFoundException(String theClassName, String theMessage, Exception theException) {
        Object[] args = { theClassName, theMessage };

        String message = ExceptionMessageGenerator.buildMessage(EJBQLException.class, classNotFoundException, args);
        EJBQLException exception = new EJBQLException(message, theException, classNotFoundException);
        exception.setErrorCode(classNotFoundException);
        return exception;
    }

    public static EJBQLException resolutionClassNotFoundException(String theClassName) {
        Object[] args = { theClassName };

        String message = ExceptionMessageGenerator.buildMessage(EJBQLException.class, resolutionClassNotFoundException, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(resolutionClassNotFoundException);
        return exception;
    }

    public static EJBQLException missingDescriptorException(String theClassName) {
        Object[] args = { theClassName };

        String message = ExceptionMessageGenerator.buildMessage(EJBQLException.class, missingDescriptorException, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(missingDescriptorException);

        return exception;
    }

    public static EJBQLException missingMappingException(String theAttributeName) {
        Object[] args = { theAttributeName };

        String message = ExceptionMessageGenerator.buildMessage(EJBQLException.class, missingMappingException, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(missingMappingException);

        return exception;
    }

    public static EJBQLException aliasResolutionException(String theAlias) {
        Object[] args = { theAlias };

        String message = ExceptionMessageGenerator.buildMessage(EJBQLException.class, aliasResolutionException, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(aliasResolutionException);
        return exception;
    }

    public static EJBQLException invalidContextKeyException(String theKey) {
        Object[] args = { theKey };

        String message = ExceptionMessageGenerator.buildMessage(EJBQLException.class, invalidContextKeyException, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(invalidContextKeyException);
        return exception;
    }

    public static EJBQLException expressionNotSupported(String unsupportedExpression) {
        Object[] args = { unsupportedExpression };

        String message = ExceptionMessageGenerator.buildMessage(EJBQLException.class, expressionNotSupported, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(expressionNotSupported);
        return exception;
    }

    public static EJBQLException invalidCollectionMemberDecl(String attributeName) {
        Object[] args = { attributeName };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, invalidCollectionMemberDecl, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(invalidCollectionMemberDecl);
        return exception;
    }

    public static EJBQLException notYetImplemented(String detail) {
        Object[] args = { detail };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, notYetImplemented, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(notYetImplemented);
        return exception;
    }

    public static EJBQLException constructorClassNotFound(String className) {
        Object[] args = { className };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, constructorClassNotFound, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(constructorClassNotFound);
        return exception;
    }

    public static EJBQLException invalidSizeArgument(String attributeName) {
        Object[] args = { attributeName };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, invalidSizeArgument, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(invalidSizeArgument);
        return exception;
    }

    public static EJBQLException invalidEnumLiteral(String enumType, String constant) {
        Object[] args = { enumType, constant };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, invalidEnumLiteral, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(invalidEnumLiteral);
        return exception;
    }

    public static EJBQLException invalidSelectForGroupByQuery(String select, String groupBy) {
        Object[] args = { select, groupBy };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, invalidSelectForGroupByQuery, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(invalidSelectForGroupByQuery);
        return exception;
    }

    public static EJBQLException invalidHavingExpression(String having, String groupBy) {
        Object[] args = { having, groupBy };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, invalidHavingExpression, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(invalidHavingExpression);
        return exception;
    }

    public static EJBQLException invalidMultipleUseOfSameParameter(
        String parameter, String oldType, String newType) {
        Object[] args = { parameter, oldType, newType };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, invalidMultipleUseOfSameParameter, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(invalidMultipleUseOfSameParameter);
        return exception;
    }

    public static EJBQLException multipleVariableDeclaration(
        String variable, String oldDecl) {
        Object[] args = { variable, oldDecl };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, multipleVariableDeclaration, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(multipleVariableDeclaration);
        return exception;
    }

    public static EJBQLException invalidFunctionArgument(String functionName, String attributeName, String type) {
        Object[] args = { functionName, attributeName, type };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, invalidFunctionArgument, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(invalidFunctionArgument);
        return exception;
    }

    public static EJBQLException invalidExpressionArgument(String functionName, String attributeName, String type) {
        Object[] args = { functionName, attributeName, type };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, invalidExpressionArgument, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(invalidExpressionArgument);
        return exception;
    }

    public static EJBQLException expectedOrderableOrderByItem(String item, String type) {
        Object[] args = { item, type };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, expectedOrderableOrderByItem, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(expectedOrderableOrderByItem);
        return exception;
    }

    public static EJBQLException syntaxError(String query) {
        Object[] args = { query };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, syntaxError, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(syntaxError);
        return exception;
    }

    public static EJBQLException syntaxErrorAt(String query, String token) {
        Object[] args = { query, token };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, syntaxErrorAt, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(syntaxErrorAt);
        return exception;
    }

    public static EJBQLException unexpectedToken(String query, String token) {
        Object[] args = { query, token };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, unexpectedToken, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(unexpectedToken);
        return exception;
    }

    public static EJBQLException unexpectedChar(String query, String unexpected) {
        Object[] args = { query, unexpected };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, unexpectedChar, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(unexpectedChar);
        return exception;
    }

    public static EJBQLException expectedCharFound(String query, String expected, String found) {
        Object[] args = { query, expected, found };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, expectedCharFound, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(expectedCharFound);
        return exception;
    }

    public static EJBQLException unexpectedEOF(String query) {
        Object[] args = { query };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, unexpectedEOF, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(unexpectedEOF);
        return exception;
    }

    public static EJBQLException invalidNavigation(
        String expr, String lhs, String type) {
        Object[] args = { expr, lhs, type };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, invalidNavigation, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(invalidNavigation);
        return exception;
    }

    public static EJBQLException unknownAttribute(String type, String name) {
        Object[] args = { type, name };

        String message = ExceptionMessageGenerator.buildMessage(
            EJBQLException.class, unknownAttribute, args);
        EJBQLException exception = new EJBQLException(message);
        exception.setErrorCode(unknownAttribute);
        return exception;
    }

    /**
     * INTERNAL
     * Add an internal Exception to the collection of
     * internal Exceptions
     */
    public Object addInternalException(Object theException) {
        getInternalExceptions().add(theException);
        return theException;
    }

    /**
     * INTERNAL
     * Does this exception have any internal errors?
     */
    public boolean hasInternalExceptions() {
        return !getInternalExceptions().isEmpty();
    }

    /**
     * INTERNAL
     * Return the collection of internal Exceptions.
     * Intialize if there are no exceptions
     */
    public Collection getInternalExceptions() {
        if (internalExceptions == null) {
            setInternalExceptions(new Vector());
        }
        return internalExceptions;
    }

    /**
     * INTERNAL
     * Store the exceptions related to this exception
     */
    public void setInternalExceptions(Collection theExceptions) {
        internalExceptions = theExceptions;
    }

    /**
     * PUBLIC:
     * Print the stack trace for each error generated by the
     * parser. This method is intended to assist in debugging
     * problems in EJBQL
     */
    public void printFullStackTrace() {
        if (hasInternalExceptions()) {
            Iterator exceptions = getInternalExceptions().iterator();
            while (exceptions.hasNext()) {
                Throwable error = (Throwable)exceptions.next();
                error.printStackTrace();
            }
        }
    }
}