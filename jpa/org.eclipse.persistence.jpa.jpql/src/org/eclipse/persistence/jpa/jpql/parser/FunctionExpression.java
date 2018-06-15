/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpressionFactory.ParameterCount;

/**
 * This expression adds support to call native database functions.
 * <p>
 * New to JPA 2.1.
 *
 * <div><b>BNF:</b> <code>func_expression ::= &lt;identifier&gt;('function_name' {, func_item}*)</code><p></div>
 *
 * @version 2.5
 * @since 2.4
 * @author James
 */
public final class FunctionExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * The name of the SQL function.
     */
    private String functionName;

    /**
     * Determines whether the comma separating the function name and the first expression.
     */
    private boolean hasComma;

    /**
     * Determines whether a whitespace is following the comma.
     */
    private boolean hasSpaceAfterComma;

    /**
     * The number of {@link ParameterCount parameters} a {@link FunctionExpression} can have.
     *
     * @since 2.4
     */
    private ParameterCount parameterCount;

    /**
     * The unique identifier of the {@link JPQLQueryBNF} that will be used to parse the arguments of
     * the function expression.
     *
     * @since 2.4
     */
    private String parameterQueryBNFId;

    /**
     * Creates a new <code>FuncExpression</code>.
     *
     * @param parent The parent of this expression
     * @param identifier The JPQL identifier
     */
    public FunctionExpression(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    /**
     * Creates a new <code>FunctionExpression</code>.
     *
     * @param parent The parent of this expression
     * @param identifier The JPQL identifier
     * @param parameterCount The number of {@link ParameterCount parameters} a {@link
     * FunctionExpression} can have
     * @param parameterQueryBNFId The unique identifier of the {@link JPQLQueryBNF} that will be used
     * to parse the arguments of the function expression
     */
    public FunctionExpression(AbstractExpression parent,
                              String identifier,
                              ParameterCount parameterCount,
                              String parameterQueryBNFId) {

        this(parent, identifier);
        this.parameterCount      = parameterCount;
        this.parameterQueryBNFId = parameterQueryBNFId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedEncapsulatedExpressionTo(List<Expression> children) {

        children.add(buildStringExpression(functionName));

        if (hasComma) {
            children.add(buildStringExpression(COMMA));
        }

        if (hasSpaceAfterComma) {
            children.add(buildStringExpression(SPACE));
        }

        super.addOrderedEncapsulatedExpressionTo(children);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncapsulatedExpressionQueryBNFId() {
        return parameterQueryBNFId;
    }

    /**
     * Returns the name of the SQL function.
     *
     * @return The name of the SQL function
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * Returns the number of parameters a {@link FunctionExpression} can have, which will be during
     * validation.
     *
     * @return The number of parameters (encapsulated expressions) allowed by this expression
     * @since 2.4
     */
    public ParameterCount getParameterCount() {
        return parameterCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionExpressionBNF.ID);
    }

    /**
     * Returns the name of the SQL function.
     *
     * @return The name of the SQL function
     */
    public String getUnquotedFunctionName() {
        return ExpressionTools.unquote(functionName);
    }

    /**
     * Determines whether the comma was parsed after the function name.
     *
     * @return <code>true</code> if a comma was parsed after the function name and the first
     * expression; <code>false</code> otherwise
     */
    public boolean hasComma() {
        return hasComma;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEncapsulatedExpression() {
        return hasFunctionName() || hasComma || super.hasEncapsulatedExpression();
    }

    /**
     * Determines whether the function name was parsed.
     *
     * @return <code>true</code> if the function name was parsed; <code>false</code> otherwise
     */
    public boolean hasFunctionName() {
        return ExpressionTools.stringIsNotEmpty(functionName);
    }

    /**
     * Determines whether a whitespace was parsed after the comma.
     *
     * @return <code>true</code> if there was a whitespace after the comma; <code>false</code> otherwise
     */
    public boolean hasSpaceAfterComma() {
        return hasSpaceAfterComma;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parseEncapsulatedExpression(WordParser wordParser,
                                               int whitespaceCount,
                                               boolean tolerant) {

        int count = 0;

        // Parse the function name outside of a CollectionExpression so we can retrieve it
        // with getFunctionName()
        if (wordParser.startsWith(SINGLE_QUOTE)) {
            functionName = wordParser.word();
            wordParser.moveForward(functionName);
            count = wordParser.skipLeadingWhitespace();
        }
        else {
            functionName = ExpressionTools.EMPTY_STRING;
        }

        hasComma = wordParser.startsWith(COMMA);

        if (hasComma) {
            wordParser.moveForward(1);
            count = wordParser.skipLeadingWhitespace();
            hasSpaceAfterComma = count > 0;
        }

        // Parse the arguments
        if (!wordParser.isTail() &&
             wordParser.character() != RIGHT_PARENTHESIS) {

            super.parseEncapsulatedExpression(wordParser, whitespaceCount, tolerant);
        }

        // A space was parsed but no expression, let the space belong to the parent
        if (count > 0           &&
           !hasExpression()     &&
           !wordParser.isTail() &&
            wordParser.character() != RIGHT_PARENTHESIS) {

            hasSpaceAfterComma = false;
            wordParser.moveBackward(count);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {

        writer.append(functionName);

        if (hasComma) {
            writer.append(COMMA);
        }

        if (hasSpaceAfterComma) {
            writer.append(SPACE);
        }

        super.toParsedTextEncapsulatedExpression(writer, actual);
    }
}
