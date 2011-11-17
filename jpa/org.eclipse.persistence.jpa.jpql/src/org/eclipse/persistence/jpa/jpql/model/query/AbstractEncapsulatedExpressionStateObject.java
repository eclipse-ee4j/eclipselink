/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.model.query;

import java.io.IOException;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEncapsulatedExpression;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * This expression handles parsing the identifier followed by an expression encapsulated within
 * parenthesis.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(expression)</code><p>
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