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
package org.eclipse.persistence.jpa.tests.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionValuedPathExpressionBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.ComparisonExpressionBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.ConditionalExpressionBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.ConstructorItemBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentifierRole;
import org.eclipse.persistence.jpa.internal.jpql.parser.InternalCountBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.ScalarExpressionBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectItemBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectExpressionBNF;

import java.util.ArrayList;
import java.util.List;

/**
 * This gives access to the BNF (which is package protected) to other unit-tests.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class JPQLQueryBNFAccessor {

	private static Iterable<String> aggregates(Iterable<String> identifiers) {
		return filter(identifiers, IdentifierRole.AGGREGATE);
	}

	private static Iterable<String> clauses(Iterable<String> identifiers) {
		return filter(identifiers, IdentifierRole.CLAUSE);
	}

	public static Iterable<String> collectionMemberDeclarationParameters() {
		return identifiers(CollectionValuedPathExpressionBNF.ID);
	}

	public static Iterable<String> comparisonExpressionClauses() {
		return clauses(comparisonExpressionIdentifiers());
	}

	public static Iterable<String> comparisonExpressionFunctions() {
		return functions(comparisonExpressionIdentifiers());
	}

	public static Iterable<String> comparisonExpressionIdentifiers() {
		return identifiers(ComparisonExpressionBNF.ID);
	}

	public static Iterable<String> constructorItemFunctions() {
		return functions(constructorItemIdentifiers());
	}

	public static Iterable<String> constructorItemIdentifiers() {
		return identifiers(ConstructorItemBNF.ID);
	}

	public static Iterable<String> countFunctions() {
		return functions(countIdentifiers());
	}

	private static Iterable<String> countIdentifiers() {
		return identifiers(InternalCountBNF.ID);
	}

	private static Iterable<String> filter(Iterable<String> identifiers,
	                                       IdentifierRole identifierRole) {

		List<String> items = new ArrayList<String>();

		for (String identifier : identifiers) {
			if (identifierRole(identifier) == identifierRole) {
				items.add(identifier);
			}
		}

		return items;
	}

	private static Iterable<String> functions(Iterable<String> identifiers) {
		return filter(identifiers, IdentifierRole.FUNCTION);
	}

	public static IdentifierRole identifierRole(String identifier) {
		return AbstractExpression.identifierRole(identifier);
	}

	public static Iterable<String> identifiers(String queryBNFId) {
		return queryBNF(queryBNFId).identifiers();
	}

	private static JPQLQueryBNF queryBNF(String queryBNFId) {
		return AbstractExpression.queryBNF(queryBNFId);
	}

	public static Iterable<String> scalarExpressionFunctions() {
		return functions(scalarExpressionIdentifiers());
	}

	public static Iterable<String> scalarExpressionIdentifiers() {
		return identifiers(ScalarExpressionBNF.ID);
	}

	public static Iterable<String> selectItemAggregates() {
		return aggregates(selectItemIdentifiers());
	}

	public static Iterable<String> selectItemFunctions() {
		return functions(selectItemIdentifiers());
	}

	public static Iterable<String> selectItemIdentifiers() {
		return identifiers(SelectItemBNF.ID);
	}

	public static Iterable<String> subSelectFunctions() {
		return functions(subSelectIdentifiers());
	}

	public static Iterable<String> subSelectIdentifiers() {
		return identifiers(SimpleSelectExpressionBNF.ID);
	}

	public static Iterable<String> whereClauseFunctions() {
		return functions(whereClauseIdentifiers());
	}

	public static Iterable<String> whereClauseIdentifiers() {
		return identifiers(ConditionalExpressionBNF.ID);
	}
}