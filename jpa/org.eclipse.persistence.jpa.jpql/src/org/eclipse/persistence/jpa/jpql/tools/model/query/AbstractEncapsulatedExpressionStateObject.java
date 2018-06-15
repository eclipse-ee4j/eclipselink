/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import java.io.IOException;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEncapsulatedExpression;
import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * This expression handles parsing the identifier followed by an expression encapsulated within
 * parenthesis.
 *
 * <div><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(expression)</code><p></div>
 *
 * @see AbstractEncapsulatedExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractEncapsulatedExpressionStateObject extends AbstractStateObject {

    /**
     * Creates a new <code>AbstractEncapsulatedExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    protected AbstractEncapsulatedExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractEncapsulatedExpression getExpression() {
        return (AbstractEncapsulatedExpression) super.getExpression();
    }

    /**
     * Returns the JPQL identifier of the expression represented by this {@link
     * AbstractSingleEncapsulatedExpressionStateObject}.
     *
     * @return The JPQL identifier that is shown before the left parenthesis
     */
    public abstract String getIdentifier();

    /**
     * Prints out a string representation of this encapsulated information, which should not be used
     * to define a <code>true</code> string representation of a JPQL query but should be used for
     * debugging purposes.
     *
     * @param writer The writer used to print out the string representation of the encapsulated
     * information
     * @throws IOException This should never happens, only required because {@link Appendable} is
     * used instead of {@link StringBuilder} for instance
     */
    protected abstract void toTextEncapsulatedExpression(Appendable writer) throws IOException;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toTextInternal(Appendable writer) throws IOException {
        writer.append(getIdentifier());
        writer.append(LEFT_PARENTHESIS);
        toTextEncapsulatedExpression(writer);
        writer.append(RIGHT_PARENTHESIS);
    }
}
