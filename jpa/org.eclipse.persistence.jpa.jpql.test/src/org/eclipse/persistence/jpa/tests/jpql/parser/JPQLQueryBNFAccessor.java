/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ConditionalExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionRegistry;
import org.eclipse.persistence.jpa.jpql.parser.IdentifierRole;
import org.eclipse.persistence.jpa.jpql.parser.InternalAggregateFunctionBNF;
import org.eclipse.persistence.jpa.jpql.parser.InternalCountBNF;
import org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.jpql.parser.ScalarExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SelectExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectExpressionBNF;

/**
 * This accessor is used to easily retrieve the JPQL identifiers registered with various {@link
 * JPQLQueryBNF}. Note: the methods are added as needed.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class JPQLQueryBNFAccessor {

	protected ExpressionRegistry registry;

	/**
	 * Creates a new <code>JPQLQueryBNFAccessor</code>.
	 *
	 * @param registry
	 */
	public JPQLQueryBNFAccessor(ExpressionRegistry registry) {
		super();
		this.registry = registry;
	}

	private void addAll(Collection<String> list, Iterable<String> items) {
		for (String item : items) {
			list.add(item);
		}
	}

	public Iterable<String> aggregates(Iterable<String> identifiers) {
		return filter(identifiers, IdentifierRole.AGGREGATE);
	}

	public Iterable<String> clauses(Iterable<String> identifiers) {
		return filter(identifiers, IdentifierRole.CLAUSE);
	}

	public Iterable<String> collectionMemberDeclarationParameters() {
		return getIdentifiers(CollectionValuedPathExpressionBNF.ID);
	}

	public Iterable<String> comparisonExpressionClauses() {
		return clauses(comparisonExpressionIdentifiers());
	}

	public Iterable<String> comparisonExpressionFunctions() {
		return functions(comparisonExpressionIdentifiers());
	}

	public Iterable<String> comparisonExpressionIdentifiers() {
		return getIdentifiers(ComparisonExpressionBNF.ID);
	}

	private Iterable<String> conditionalExpressions(IdentifierRole role) {

		Set<String> identifiers = new HashSet<String>();
		JPQLQueryBNF queryBNF = getQueryBNF(ConditionalExpressionBNF.ID);

		for (JPQLQueryBNF child : queryBNF.children()) {
			addAll(identifiers, filter(child.getIdentifiers(), role));
		}

		identifiers.remove(Expression.IS);
		return identifiers;
	}

	public Iterable<String> conditionalExpressionsAggregates() {
		return conditionalExpressions(IdentifierRole.AGGREGATE);
	}

	public Iterable<String> conditionalExpressionsCompoundFunctions() {
		return conditionalExpressions(IdentifierRole.COMPOUND_FUNCTION);
	}

	public Iterable<String> constructorItemFunctions() {
		return functions(constructorItemIdentifiers());
	}

	public Iterable<String> constructorItemIdentifiers() {
		return getIdentifiers(ConstructorItemBNF.ID);
	}

	public Iterable<String> countFunctions() {
		return functions(countIdentifiers());
	}

	public Iterable<String> countIdentifiers() {
		return getIdentifiers(InternalCountBNF.ID);
	}

	public Iterable<String> filter(Iterable<String> identifiers, IdentifierRole identifierRole) {

		List<String> items = new ArrayList<String>();

		for (String identifier : identifiers) {
			if (getIdentifierRole(identifier) == identifierRole) {
				items.add(identifier);
			}
		}

		return items;
	}

	public Iterable<String> functions(Iterable<String> identifiers) {
		return filter(identifiers, IdentifierRole.FUNCTION);
	}

	public IdentifierRole getIdentifierRole(String identifier) {
		return registry.getIdentifierRole(identifier);
	}

	public Iterable<String> getIdentifiers(String queryBNFId) {
		return getQueryBNF(queryBNFId).getIdentifiers();
	}

	public JPQLQueryBNF getQueryBNF(String queryBNFId) {
		return registry.getQueryBNF(queryBNFId);
	}

	public ExpressionRegistry getRegistry() {
		return registry;
	}

	public Iterable<String> internalAggregateFunctionFunctions() {
		return functions(internalAggregateFunctionIdentifiers());
	}

	public Iterable<String> internalAggregateFunctionIdentifiers() {
		return getIdentifiers(InternalAggregateFunctionBNF.ID);
	}

	public Iterable<String> scalarExpressionAggregates() {
		return aggregates(scalarExpressionIdentifiers());
	}

	public Iterable<String> scalarExpressionFunctions() {
		return functions(scalarExpressionIdentifiers());
	}

	public Iterable<String> scalarExpressionIdentifiers() {
		return getIdentifiers(ScalarExpressionBNF.ID);
	}

	public Iterable<String> selectItemAggregates() {
		return aggregates(selectItemIdentifiers());
	}

	public Iterable<String> selectItemFunctions() {
		return functions(selectItemIdentifiers());
	}

	public Iterable<String> selectItemIdentifiers() {
		return getIdentifiers(SelectExpressionBNF.ID);
	}

	public Iterable<String> subSelectFunctions() {
		return functions(subSelectIdentifiers());
	}

	public Iterable<String> subSelectIdentifiers() {
		return getIdentifiers(SimpleSelectExpressionBNF.ID);
	}

	public Iterable<String> whereClauseFunctions() {
		return functions(whereClauseIdentifiers());
	}

	public Iterable<String> whereClauseIdentifiers() {
		return getIdentifiers(ConditionalExpressionBNF.ID);
	}
}