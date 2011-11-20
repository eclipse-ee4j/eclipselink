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
package org.eclipse.persistence.jpa.jpql.model;

import org.eclipse.persistence.jpa.jpql.model.query.EclipseLinkStateObjectVisitor;
import org.eclipse.persistence.jpa.jpql.model.query.FuncExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TreatExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This {@link IJPQLQueryFormatter} is used to output a string representation of a {@link StateObject}
 * based on how it was parsed, which means this formatter can only be used when the {@link StateObject}
 * was created from parsing a JPQL query because it needs to retrieve parsing information from the
 * corresponding {@link Expression}.
 * <p>
 * This version adds support for EclipseLink extension.
 * <p>
 * It is possible to partially match the JPQL query that was parsed, the value of the <em>exactMatch</em>
 * will determine whether the string representation of any given {@link StateObject} should reflect
 * the exact string that was parsed. <code>true</code> will use every bit of information contained
 * in the corresponding {@link Expression} to perfectly match what was parsed; <code>false</code>
 * will only match the case sensitivity of the JPQL identifiers.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class EclipseLinkActualJPQLQueryFormatter extends AbstractActualJPQLQueryFormatter
                                                       implements EclipseLinkStateObjectVisitor {

	/**
	 * Creates a new <code>EclipseLinkActualJPQLQueryFormatter</code>.
	 *
	 * @param exactMatch Determines whether the string representation of any given {@link StateObject}
	 * should reflect the exact string that was parsed: <code>true</code> will use every bit of
	 * information contained in the corresponding {@link Expression} to perfectly match what was
	 * parsed; <code>false</code> will only match the case sensitivity of the JPQL identifiers
	 */
	public EclipseLinkActualJPQLQueryFormatter(boolean exactMatch) {
		super(exactMatch);
	}

	/**
	 * Creates a new <code>EclipseLinkActualJPQLQueryFormatter</code>.
	 *
	 * @param exactMatch Determines whether the string representation of any given {@link StateObject}
	 * should reflect the exact string that was parsed: <code>true</code> will use every bit of
	 * information contained in the corresponding {@link Expression} to perfectly match what was
	 * parsed (case of JPQL identifiers and the presence of whitespace); <code>false</code> will only
	 * match the case sensitivity of the JPQL identifiers
	 * @param style Determines how the JPQL identifiers are written out, which is used if the
	 * {@link StateObject} was modified after its creation
	 * @exception NullPointerException The IdentifierStyle cannot be <code>null</code>
	 */
	protected EclipseLinkActualJPQLQueryFormatter(boolean exactMatch, IdentifierStyle style) {
		super(exactMatch, style);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FuncExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			FuncExpression expression = stateObject.getExpression();

			// FUNC
			writer.append(expression.getActualIdentifier());

			// (
			if (!exactMatch | expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}
			else if (exactMatch && expression.hasSpaceAfterIdentifier()) {
				writer.append(SPACE);
			}

			// Function name
			if (stateObject.hasFunctionName()) {

				writer.append(stateObject.getFunctionName());

				if (!exactMatch | expression.hasComma()) {
					writer.append(COMMA);
				}

				if (!exactMatch | expression.hasSpaceAFterComma()) {
					writer.append(SPACE);
				}
			}

			// Arguments
			toStringChildren(stateObject, true);

			// )
			if (!exactMatch | expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			TreatExpression expression = stateObject.getExpression();

			// TREAT
			writer.append(expression.getActualIdentifier());

			// (
			if (!exactMatch | expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			// Join association path expression
			toText(stateObject.getJoinAssociationPathStateObject());

			if (!exactMatch | expression.hasSpaceAfterCollectionValuedPathExpression()) {
				writer.append(SPACE);
			}

			// AS
			if (stateObject.hasAs()) {

				appendIdentifier(expression.getActualAsIdentifier(), AS);

				if (!exactMatch | expression.hasSpaceAfterAs()) {
					writer.append(SPACE);
				}
			}

			// Entity type name
			writer.append(stateObject.getEntityTypeName());

			// )
			if (!exactMatch | expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}
}