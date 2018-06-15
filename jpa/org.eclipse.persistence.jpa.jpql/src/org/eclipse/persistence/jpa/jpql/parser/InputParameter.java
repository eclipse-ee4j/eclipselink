/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Either positional or named parameters may be used. Positional and named parameters may not be
 * mixed in a single query. Input parameters can only be used in the <b>WHERE</b> clause or
 * <b>HAVING</b> clause of a query.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class InputParameter extends AbstractExpression {

    /**
     * The cached parameter name without the identifier.
     */
    private String parameterName;

    /**
     * Flag caching the type of the input parameter, which is either positional or named.
     */
    private Boolean positional;

    /**
     * Creates a new <code>InputParameter</code>.
     *
     * @param parent The parent of this expression
     * @param parameter The input parameter, which starts with either '?' or ':'
     */
    public InputParameter(AbstractExpression parent, String parameter) {
        super(parent, parameter);
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
    public void acceptChildren(ExpressionVisitor visitor) {
        // Does not have children
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {
        children.add(buildStringExpression(getText()));
    }

    /**
     * Returns the positional parameter or the named parameter, which includes the identifier.
     *
     * @return The parameter following the constant used to determine if it's a positional or named parameter
     * @see #getParameterName()
     */
    public String getParameter() {
        return getText();
    }

    /**
     * Returns the positional parameter or the named parameter without the identifier.
     *
     * @return The parameter following the constant used to determine if it's a positional or named parameter
     * @see #getParameter()
     * @since 2.5
     */
    public String getParameterName() {
        if (parameterName == null) {
            parameterName = getText().substring(1);
        }
        return parameterName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(InputParameterBNF.ID);
    }

    /**
     * Determines whether this parameter is a positional parameter, i.e. the parameter type is '?'.
     *
     * @return <code>true</code> if the parameter type is '?'; <code>false</code> if it's ':'
     */
    public boolean isNamed() {
        if (positional == null) {
            positional = getText().charAt(0) == '?';
        }
        return (positional == Boolean.FALSE);
    }

    /**
     * Determines whether this parameter is a positional parameter, i.e. the parameter type is ':'.
     *
     * @return <code>true</code> if the parameter type is ':'; <code>false</code> if it's '?'
     */
    public boolean isPositional() {
        if (positional == null) {
            positional = getText().charAt(0) == '?';
        }
        return (positional == Boolean.TRUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {
        wordParser.moveForward(getText());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toActualText() {
        return getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toParsedText() {
        return getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {
        writer.append(getText());
    }
}
