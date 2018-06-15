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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpressionFactory;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticTermBNF;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpressionFactory;
import org.eclipse.persistence.jpa.jpql.parser.ConditionalExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionFactory;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionRegistry;
import org.eclipse.persistence.jpa.jpql.parser.GeneralIdentificationVariableBNF;
import org.eclipse.persistence.jpa.jpql.parser.GroupByItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.IdentifierRole;
import org.eclipse.persistence.jpa.jpql.parser.InternalAggregateFunctionBNF;
import org.eclipse.persistence.jpa.jpql.parser.InternalConcatExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.InternalCountBNF;
import org.eclipse.persistence.jpa.jpql.parser.InternalFromClauseBNF;
import org.eclipse.persistence.jpa.jpql.parser.InternalSimpleFromClauseBNF;
import org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.jpql.parser.PatternValueBNF;
import org.eclipse.persistence.jpa.jpql.parser.ScalarExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SelectExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.StringExpressionBNF;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ArrayIterable;

/**
 * This accessor is used to easily retrieve the JPQL identifiers registered with various {@link
 * JPQLQueryBNF}. Note: the methods are added as needed.
 *
 * @version 2.5
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

    public Iterable<String> arithmetics() {
        ExpressionFactory factory = getExpressionFactory(ArithmeticExpressionFactory.ID);
        return new ArrayIterable<String>(factory.identifiers());
    }

    public Iterable<String> arithmeticTermFunctions() {
        return functions(arithmeticTermIdentifiers());
    }

    public Iterable<String> arithmeticTermIdentifiers() {
        return getIdentifiers(ArithmeticTermBNF.ID);
    }

    public Iterable<String> clauses(Iterable<String> identifiers) {
        return filter(identifiers, IdentifierRole.CLAUSE);
    }

    public Iterable<String> collectionMemberDeclarationParameters() {
        return getIdentifiers(CollectionValuedPathExpressionBNF.ID);
    }

    public Iterable<String> collectionValuedPathExpressionFunctions() {
        return functions(collectionValuedPathExpressionIdentifiers());
    }

    public Iterable<String> collectionValuedPathExpressionIdentifiers() {
        return getIdentifiers(CollectionValuedPathExpressionBNF.ID);
    }

    public Iterable<String> comparators() {
        ExpressionFactory factory = getExpressionFactory(ComparisonExpressionFactory.ID);
        return new ArrayIterable<String>(factory.identifiers());
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

    public Iterable<String> conditionalExpressionsFunctions() {
        return functions(conditionalExpressionsIdentifiers());
    }

    public Iterable<String> conditionalExpressionsIdentifiers() {
        return getIdentifiers(ConditionalExpressionBNF.ID);
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

    public Iterable<String> generalIdentificationVariableFunctions() {
        return functions(generalIdentificationVariableIdentifiers());
    }

    public Iterable<String> generalIdentificationVariableIdentifiers() {
        return getIdentifiers(GeneralIdentificationVariableBNF.ID);
    }

    public ExpressionFactory getExpressionFactory(String expressionFactoryId) {
        return registry.getExpressionFactory(expressionFactoryId);
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

    public Iterable<String> groupByItemFunctions() {
        return functions(groupByItemIdentifiers());
    }

    public Iterable<String> groupByItemIdentifiers() {
        return getIdentifiers(GroupByItemBNF.ID);
    }

    public Iterable<String> internalAggregateFunctionFunctions() {
        return functions(internalAggregateFunctionIdentifiers());
    }

    public Iterable<String> internalAggregateFunctionIdentifiers() {
        return getIdentifiers(InternalAggregateFunctionBNF.ID);
    }

    public Iterable<String> internalConcatExpressionClauses() {
        return clauses(internalConcatExpressionIdentifiers());
    }

    public Iterable<String> internalConcatExpressionFunctions() {
        return functions(internalConcatExpressionIdentifiers());
    }

    private Iterable<String> internalConcatExpressionIdentifiers() {
        return getIdentifiers(InternalConcatExpressionBNF.ID);
    }

    public Iterable<String> internalFromClauseIdentifiers() {
        return getIdentifiers(InternalFromClauseBNF.ID);
    }

    public Iterable<String> internalSimpleFromClauseIdentifiers() {
        return getIdentifiers(InternalSimpleFromClauseBNF.ID);
    }

    public Iterable<String> logicalIdentifiers() {
        return CollectionTools.list(Expression.AND, Expression.OR);
    }

    public Iterable<String> patternValueFunctions() {
        return functions(patternValueIdentifiers());
    }

    public Iterable<String> patternValueIdentifiers() {
        return getIdentifiers(PatternValueBNF.ID);
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

    public Iterable<String> stringExpressionFunctions() {
        return functions(stringExpressionIdentifiers());
    }

    private Iterable<String> stringExpressionIdentifiers() {
        return getIdentifiers(StringExpressionBNF.ID);
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
