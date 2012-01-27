/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.LiteralBNF;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * This validator is responsible to gather the problems found in a JPQL query by validating the
 * content to make sure it is semantically valid. The grammar is not validated by this visitor.
 * <p>
 * For instance, the function <b>AVG</b> accepts a state field path. The property it represents has
 * to be of numeric type. <b>AVG(e.name)</b> is parsable but is not semantically valid because the
 * type of name is a string (the property signature is: "<code>private String name</code>").
 *
 * @see DefaultGrammarValidator
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class DefaultSemanticValidator extends AbstractSemanticValidator {

	/**
	 * Creates a new <code>DefaultSemanticValidator</code>.
	 *
	 * @param context The context used to query information about the JPQL query
	 * @exception AssertException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	public DefaultSemanticValidator(JPQLQueryContext context) {
		super(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateIdentificationVariable(IdentificationVariable expression, String variable) {

		for (IEntity entity : getProvider().entities()) {

			// An identification variable must not have the same name as any entity in the same
			// persistence unit, unless it's representing an entity literal
			String entityName = entity.getName();

			if (variable.equalsIgnoreCase(entityName)) {

				// An identification variable could represent an entity type literal,
				// validate the parent to make sure it allows it
				if (!isValidWithFindQueryBNF(expression, LiteralBNF.ID)) {
					int startIndex = position(expression);
					int endIndex   = startIndex + variable.length();
					addProblem(expression, startIndex, endIndex, IdentificationVariable_EntityName);
					break;
				}
			}
		}
	}
}