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

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * <P><B>Purpose</B>: EJBQL parsing and resolution problems will raise this exception
 */
public class JPQLException extends EclipseLinkException {
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
    public static final int unsupportJoinArgument = 8031;
    public static final int invalidSetClauseTarget = 8032;
    public static final int invalidSetClauseNavigation = 8033;
    /**
     * @see JPQLException#entityTypeNotFound(String, String)
     */
    public static final int entityTypeNotFound = 8034;
    public static final int invalidEnumEqualExpression = 8035;
    public static final int invalidCollectionNavigation = 8036;
    public static final int entityTypeNotFound2 = 8037;
    public static final int resolutionClassNotFoundException2 = 8038;
    public static final int variableCannotHaveMapKey = 8039;
    public static final int nonExistantOrderByAlias = 8040;
    public static final int indexOnlyAllowedOnVariable = 8041;

    public Collection internalExceptions = null;
    
    /**
    * INTERNAL
    * Only TopLink can throw and create these excpetions
    */
    protected JPQLException() {
        super();
    }

    /**
    * INTERNAL
    * Only TopLink can throw and create these excpetions
    */
    public JPQLException(String theMessage) {
        super(theMessage);
    }

    /**
    * INTERNAL
    * Only TopLink can throw and create these excpetions
    */
    public JPQLException(String message, Exception internalException) {
        super(message, internalException);
    }

    /**
    * INTERNAL
    * Only TopLink can throw and create these excpetions
    */
    protected JPQLException(String message, Exception internalException, int theErrorCode) {
        this(message, internalException);
        this.setErrorCode(theErrorCode);
    }

    /**
     * INTERNAL
     * Create an exception to wrap the recognition exception thrown
     */
    public static JPQLException recognitionException(String theEjbql, String theMessage) {
        Object[] args = { theEjbql, theMessage };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, recognitionException, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(recognitionException);
        return exception;
    }

    /**
     * INTERNAL
     * Create an exception to wrap a general parsing exception
     */
    public static JPQLException generalParsingException(String theEjbql, Exception theException) {
        Object[] args = { theEjbql, theException.getMessage() };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, generalParsingException, args);
        JPQLException exception = new JPQLException(message, theException, generalParsingException);
        exception.setErrorCode(generalParsingException);
        return exception;
    }

    /**
     * INTERNAL
     * Create an exception to wrap a general parsing exception
     */
    public static JPQLException generalParsingException(String theEjbql) {
        Object[] args = { theEjbql };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, generalParsingException2, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(generalParsingException);
        return exception;
    }

    public static JPQLException classNotFoundException(String theClassName, String theMessage, Exception theException) {
        Object[] args = { theClassName, theMessage };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, classNotFoundException, args);
        JPQLException exception = new JPQLException(message, theException, classNotFoundException);
        exception.setErrorCode(classNotFoundException);
        return exception;
    }

    public static JPQLException resolutionClassNotFoundException(String query, String theClassName) {
        Object[] args = { query, theClassName };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, resolutionClassNotFoundException, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(resolutionClassNotFoundException);
        return exception;
    }

    public static JPQLException resolutionClassNotFoundException2(String query, int line, int column, String theClassName) {
        Object[] args = { query, line, column, theClassName };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, resolutionClassNotFoundException2, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(resolutionClassNotFoundException2);
        return exception;
    }

    public static JPQLException missingDescriptorException(String query, String theClassName) {
        Object[] args = { query, theClassName };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, missingDescriptorException, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(missingDescriptorException);

        return exception;
    }

    public static JPQLException missingMappingException(String query, String theAttributeName) {
        Object[] args = { query, theAttributeName };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, missingMappingException, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(missingMappingException);

        return exception;
    }

    public static JPQLException aliasResolutionException(String query, int line, int column, String theAlias) {
        Object[] args = { query, line, column, theAlias };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, aliasResolutionException, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(aliasResolutionException);
        return exception;
    }

    public static JPQLException invalidContextKeyException(String query, String theKey) {
        Object[] args = { query, theKey };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, invalidContextKeyException, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidContextKeyException);
        return exception;
    }

    public static JPQLException expressionNotSupported(String query, String unsupportedExpression) {
        Object[] args = { query, unsupportedExpression };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, expressionNotSupported, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(expressionNotSupported);
        return exception;
    }

    public static JPQLException invalidCollectionMemberDecl(String query, int line, int column, String attributeName) {
        Object[] args = { query, line, column, attributeName };

        String message = ExceptionMessageGenerator.buildMessage(
                JPQLException.class, invalidCollectionMemberDecl, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidCollectionMemberDecl);
        return exception;
    }

    public static JPQLException notYetImplemented(String query, String detail) {
        Object[] args = { query, detail };

        String message = ExceptionMessageGenerator.buildMessage(
                JPQLException.class, notYetImplemented, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(notYetImplemented);
        return exception;
    }

    public static JPQLException constructorClassNotFound(String query, int line, int column, String className) {
        Object[] args = { query, line, column, className };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, constructorClassNotFound, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(constructorClassNotFound);
        return exception;
    }

    public static JPQLException invalidSizeArgument(String query, int line, int column, String attributeName) {
        Object[] args = { query, line, column, attributeName };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidSizeArgument, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidSizeArgument);
        return exception;
    }

    public static JPQLException invalidEnumLiteral(String query, int line, int column, String enumType, String literal) {
        Object[] args = { query, line, column, enumType, literal };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidEnumLiteral, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidEnumLiteral);
        return exception;
    }

    public static JPQLException invalidSelectForGroupByQuery(String query, int line, int column, String select, String groupBy) {
        Object[] args = { query, line, column, select, groupBy };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidSelectForGroupByQuery, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidSelectForGroupByQuery);
        return exception;
    }

    public static JPQLException invalidHavingExpression(String query, int line, int column, String having, String groupBy) {
        Object[] args = { query, line, column, having, groupBy };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidHavingExpression, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidHavingExpression);
        return exception;
    }

    public static JPQLException invalidMultipleUseOfSameParameter(
        String query, int line, int column, String parameter, String oldType, String newType) {
        Object[] args = { query, line, column , parameter, oldType, newType };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidMultipleUseOfSameParameter, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidMultipleUseOfSameParameter);
        return exception;
    }

    public static JPQLException multipleVariableDeclaration(
        String query, int line, int column, String variable, String oldDecl) {
        Object[] args = { query, line, column, variable, oldDecl };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, multipleVariableDeclaration, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(multipleVariableDeclaration);
        return exception;
    }

    public static JPQLException invalidFunctionArgument(String query, int line, int column, String functionName, String attributeName, String type) {
        Object[] args = { query, line, column, functionName, attributeName, type };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidFunctionArgument, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidFunctionArgument);
        return exception;
    }

    public static JPQLException invalidExpressionArgument(String query, int line, int column, String expression, String attributeName, String type) {
        Object[] args = { query, line, column, expression, attributeName, type };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidExpressionArgument, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidExpressionArgument);
        return exception;
    }

    public static JPQLException unsupportJoinArgument(String query, int line, int column, String join, String type) {
        Object[] args = { query, line, column, join, type };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, unsupportJoinArgument, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(unsupportJoinArgument);
        return exception;
    }

    public static JPQLException expectedOrderableOrderByItem(String query, int line, int column, String item, String type) {
        Object[] args = { query, line, column, item, type };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, expectedOrderableOrderByItem, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(expectedOrderableOrderByItem);
        return exception;
    }

    public static JPQLException syntaxError(String query, Exception ex) {
        Object[] args = { query };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, syntaxError, args);
        JPQLException exception = new JPQLException(message, ex);
        exception.setErrorCode(syntaxError);
        return exception;
    }

    public static JPQLException syntaxErrorAt(String query, int line, int column, String token, Exception ex) {
        Object[] args = { query, line, column, token };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, syntaxErrorAt, args);
        JPQLException exception = new JPQLException(message, ex);
        exception.setErrorCode(syntaxErrorAt);
        return exception;
    }

    public static JPQLException unexpectedToken(String query, int line, int column, String token, Exception ex) {
        Object[] args = { query, line, column, token };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, unexpectedToken, args);
        JPQLException exception = new JPQLException(message, ex);
        exception.setErrorCode(unexpectedToken);
        return exception;
    }

    public static JPQLException unexpectedChar(String query, int line, int column, String unexpected, Exception ex) {
        Object[] args = { query, line, column, unexpected };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, unexpectedChar, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(unexpectedChar);
        return exception;
    }

    public static JPQLException expectedCharFound(String query, int line, int column, String expected, String found, Exception ex) {
        Object[] args = { query, line, column, expected, found };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, expectedCharFound, args);
        JPQLException exception = new JPQLException(message, ex);
        exception.setErrorCode(expectedCharFound);
        return exception;
    }

    public static JPQLException unexpectedEOF(String query, int line, int column, Exception ex) {
        Object[] args = { query, line, column};

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, unexpectedEOF, args);
        JPQLException exception = new JPQLException(message, ex);
        exception.setErrorCode(unexpectedEOF);
        return exception;
    }

    public static JPQLException invalidNavigation(
        String query, int line, int column, String expr, String lhs, String type) {
        Object[] args = { query, line, column, expr, lhs, type };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidNavigation, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidNavigation);
        return exception;
    }

    public static JPQLException invalidCollectionNavigation(
        String query, int line, int column, String expr, String attribute) {
        Object[] args = { query, line, column, expr, attribute };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidCollectionNavigation, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidCollectionNavigation);
        return exception;
    }

    public static JPQLException invalidSetClauseTarget(
        String query, int line, int column, String expr, String attribute) {
        Object[] args = { query, line, column, attribute, expr };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidSetClauseTarget, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidSetClauseTarget);
        return exception;
    }

    public static JPQLException invalidSetClauseNavigation(
        String query, int line, int column, String expr, String relationship) {
        Object[] args = { query, line, column, expr, relationship };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidSetClauseNavigation, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidSetClauseNavigation);
        return exception;
    }

    public static JPQLException unknownAttribute(
        String query, int line, int column, String attribute, String type) {
        Object[] args = { query, line, column, attribute, type };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, unknownAttribute, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(unknownAttribute);
        return exception;
    }
    
    public static JPQLException invalidEnumEqualExpression(String query, int line, int column, String enumType, String type) {
        Object[] args = { query, line, column, enumType, type };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, invalidEnumEqualExpression, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(invalidEnumEqualExpression);
        return exception;
    }

    /**
     * <b>JPQLException[<a name="8034">8034</a>] Entity Type Not Found</b>
     * <p>
     * Indicates that a type specified in a JPQL query string cannot be found in
     * the current persistence unit. Ensure that the entity name is properly
     * spelled and matches the name of an entity in the persistence unit being
     * used.
     */
    public static JPQLException entityTypeNotFound(String query, String type) {
        Object[] args = { query, type };
        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, entityTypeNotFound, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(entityTypeNotFound);
        return exception;
    }

    public static JPQLException entityTypeNotFound2(String query, int line, int column, String type) {
        Object[] args = { query, line, column, type };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, entityTypeNotFound2, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(entityTypeNotFound2);
        return exception;
    }
    
    public static JPQLException variableCannotHaveMapKey(String query, int line, int column, String name) {
        Object[] args = { query, line, column, name };

        String message = ExceptionMessageGenerator.buildMessage(JPQLException.class, variableCannotHaveMapKey, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(variableCannotHaveMapKey);
        return exception;
    }

    public static JPQLException nonExistantOrderByAlias(String query, int line, int column, String alias) {
        Object[] args = { query, line, column, alias };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, nonExistantOrderByAlias, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(nonExistantOrderByAlias);
        return exception;
    }
    
    public static JPQLException indexOnlyAllowedOnVariable(String query, int line, int column, String node) {
        Object[] args = { query, line, column, node };

        String message = ExceptionMessageGenerator.buildMessage(
            JPQLException.class, indexOnlyAllowedOnVariable, args);
        JPQLException exception = new JPQLException(message);
        exception.setErrorCode(indexOnlyAllowedOnVariable);
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
     * PUBLIC
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
