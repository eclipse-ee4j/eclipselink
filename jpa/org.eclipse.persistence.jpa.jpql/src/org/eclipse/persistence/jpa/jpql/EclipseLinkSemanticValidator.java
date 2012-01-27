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
package org.eclipse.persistence.jpa.jpql;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;

/**
 * This validator is responsible to gather the problems found in a JPQL query by validating the
 * content to make sure it is semantically valid for EclipseLink. The grammar is not validated by
 * this visitor.
 * <p>
 * For instance, the function <b>AVG</b> accepts a state field path. The property it represents has
 * to be of numeric type. <b>AVG(e.name)</b> is parsable but is not semantically valid because the
 * type of name is a string (the property signature is: "<code>private String name</code>").
 *
 * @see EclipseLinkGrammarValidator
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkSemanticValidator extends AbstractSemanticValidator
                                          implements EclipseLinkExpressionVisitor {

	/**
	 * Creates a new <code>EclipseLinkSemanticValidator</code>.
	 *
	 * @param context The context used to query information about the JPQL query
	 * @exception AssertException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	public EclipseLinkSemanticValidator(JPQLQueryContext context) {
		super(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void collectDeclarationIdentificationVariables(JPQLQueryContext queryContext,
	                                                         Map<String, List<IdentificationVariable>> identificationVariables) {

		super.collectDeclarationIdentificationVariables(queryContext, identificationVariables);

		// Add the result variables
		for (IdentificationVariable identificationVariable : resultVariables(queryContext)) {
			addIdentificationVariable(identificationVariable, identificationVariables);
		}
	}

	protected Set<IdentificationVariable> resultVariables(JPQLQueryContext queryContext) {
		return queryContext.getActualDeclarationResolver().getResultVariablesMap().keySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateIdentificationVariable(IdentificationVariable expression, String variable) {
		// No need to validate the identification variable, EclipseLink supports an identification
		// variable with the same name as any entity in the same persistence unit
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FuncExpression expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}
}