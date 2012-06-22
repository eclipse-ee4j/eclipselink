/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.internal.jpql.util.Filter;
import org.eclipse.persistence.jpa.internal.jpql.util.NullFilter;

/**
 * This defines the Backus-Naur Form (BNF) of the <a href="http://jcp.org/en/jsr/detail?id=317">
 * JSR 317: Java&trade; Persistence 2.0</a>.
 *
 * @see Expression Expression for the complete grammar
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class JPQLQueryBNF {

	/**
	 * Caches the collection of unique identifiers matching the expression factories since any BNF
	 * rule is static.
	 */
	private Set<String> cachedFactories;

	/**
	 * Caches the identifier retrieves from all the {@link ExpressionFactory expression factories}.
	 */
	private Map<String, ExpressionFactory> cachedIdentifiers;

	/**
	 * Caches the children of this BNF rule (which actually includes this one as well) but do not
	 * includes BNF rules that are used for compounding a rule.
	 */
	private Set<JPQLQueryBNF> childNonCompoundQueryBNFs;

	/**
	 * Caches the children of this BNF rule (which actually includes this one as well).
	 */
	private Set<JPQLQueryBNF> childQueryBNFs;

	/**
	 * The children BNF of this one.
	 */
	private List<String> children;

	/**
	 * The list of identifiers that are part of the this BNF rule.
	 */
	private List<String> expressionFactories;

	/**
	 * Caches the property since any BNF rule is static.
	 */
	private Boolean handleAggregate;

	/**
	 * Caches the property since any BNF rule is static.
	 */
	private Boolean handleCollection;

	/**
	 * The unique identifier of this BNF rule.
	 */
	private String id;

	/**
	 * This flag is used to prevent cyclical loop when retrieving some properties from the child
	 * BNF rules.
	 */
	private boolean traversed;

	/**
	 * Creates a new <code>JPQLQueryBNF</code>.
	 *
	 * @param id The unique identifier of this BNF rule
	 */
	JPQLQueryBNF(String id) {
		super();
		this.id = id;
		this.expressionFactories = new ArrayList<String>();
		initialize();
	}

	/**
	 * Adds to the given set the child BNF rules and requests them to add their children as long as
	 * they have not been traversed already.
	 *
	 * @param queryBNFs The set to add the child BNF rules
	 * @param filter The {@link Filter} determines if the children of a given BNF should be added
	 */
	void addChildren(Set<JPQLQueryBNF> queryBNFs, Filter<JPQLQueryBNF> filter) {

		if (children != null) {
			for (String id : children) {
				JPQLQueryBNF queryBNF = queryBNF(id);

				if (queryBNFs.add(queryBNF) && filter.accept(queryBNF)) {
					queryBNF.addChildren(queryBNFs, filter);
				}
			}
		}
	}

	private Set<JPQLQueryBNF> buildChildren(Filter<JPQLQueryBNF> filter) {
		HashSet<JPQLQueryBNF> queryBNFs = new HashSet<JPQLQueryBNF>();
		queryBNFs.add(this);
		addChildren(queryBNFs, buildNonCompoundFilter());
		return queryBNFs;
	}

	private Map<String, ExpressionFactory> buildIdentifiers() {

		Map<String, ExpressionFactory> identifiers = new HashMap<String, ExpressionFactory>();

		for (String expressionFactoryId : expressionFactoryIds()) {
			ExpressionFactory expressionFactory = JPQLExpression.expressionFactory(expressionFactoryId);

			for (String identifier : expressionFactory.identifiers()) {
				identifiers.put(identifier, expressionFactory);
			}
		}

		return Collections.unmodifiableMap(identifiers);
	}

	private Filter<JPQLQueryBNF> buildNonCompoundFilter() {
		return new Filter<JPQLQueryBNF>() {
			public boolean accept(JPQLQueryBNF queryBNF) {
				return !queryBNF.isCompound();
			}
		};
	}

	private boolean calculateHandleAggregate() {

		for (JPQLQueryBNF queryBNF : children()) {
			if (queryBNF == this ||
			    queryBNF.traversed) {
				continue;
			}

			queryBNF.traversed = true;

			if (queryBNF.handleAggregate()) {
				queryBNF.traversed = false;
				return true;
			}

			queryBNF.traversed = false;
		}

		return false;
	}

	private boolean calculateHandleCollection() {

		for (JPQLQueryBNF queryBNF : children()) {
			if (queryBNF == this ||
			    queryBNF.traversed) {
				continue;
			}

			queryBNF.traversed = true;

			if (queryBNF.handleCollection()) {
				queryBNF.traversed = false;
				return true;
			}

			queryBNF.traversed = false;
		}

		return false;
	}

	/**
	 * Returns the set of all the query BNFs that are part of this BNF. The set always include this
	 * BNF as well.
	 *
	 * @return The children BNFs describing this BNF rule
	 */
	final Set<JPQLQueryBNF> children() {
		if (childQueryBNFs == null) {
			childQueryBNFs = buildChildren(NullFilter.<JPQLQueryBNF>instance());
		}
		return childQueryBNFs;
	}

	/**
	 * Retrieves the {@link ExpressionFactory} that is associated with the given identifier, if the
	 * given string is indeed a JPQL identifier.
	 *
	 * @param identifier The JPQL identifier (in theory) that is used to retrieve the factory
	 * responsible to parse a portion of the query starting with that identifier
	 * @return The {@link ExpressionFactory} responsible to parse a portion of the query starting
	 * with the given identifier; <code>null</code> if nothing was registered for it
	 */
	public final ExpressionFactory expressionFactory(String identifier) {
		populateIdentifiers();
		return cachedIdentifiers.get(identifier.toUpperCase());
	}

	/**
	 * Returns the unique identifiers of the {@link ExpressionFactory expression
	 * factories} handled by this BNF rule.
	 *
	 * @return The list of unique identifiers for each {@link ExpressionFactory}
	 */
	public final Set<String> expressionFactoryIds() {

		if (cachedFactories == null) {

			// First add all the children
			cachedFactories = new HashSet<String>();
			cachedFactories.addAll(expressionFactories);

			for (JPQLQueryBNF queryBNF : children()) {
				if (queryBNF != this) {
					cachedFactories.addAll(queryBNF.expressionFactoryIds());
				}
			}
		}

		return Collections.unmodifiableSet(cachedFactories);
	}

	/**
	 * When parsing the query and no {@link JPQLQueryBNF JPQLQueryBNFs} can help to parse the query,
	 * then it will fall back on this one.
	 *
	 * @return The unique identifier of the {@link JPQLQueryBNF} to use in the last resort
	 */
	String getFallbackBNFId() {
		return null;
	}

	/**
	 * Returns the unique identifier of the {@link ExpressionFactory} to use when the fall back BNF
	 * ID is not <code>null</code>. This will be used to parse a portion of the query when the
	 * registered {@link ExpressionFactory expression factories} cannot parse it.
	 * <p>
	 * Note: This method is only called if {@link #getFallbackBNFId()} does not return <code>null</code>.
	 *
	 * @return The unique identifier of the {@link ExpressionFactory}
	 */
	String getFallbackExpressionFactoryId() {
		return null;
	}

	/**
	 * Returns the unique identifier of this {@link JPQLQueryBNF}.
	 *
	 * @return The identifier used to register this {@link JPQLQueryBNF} with {@link AbstractExpression}
	 */
	public final String getId() {
		return id;
	}

	/**
	 * Determines whether the <code>Expression</code> handles a collection of sub-expressions that
	 * are aggregated by logical or arithmetic operators.
	 *
	 * @return <code>true</code> if the sub-expression to parse might have several logical and/or
	 * arithmetic expressions; <code>false</code> otherwise
	 */
	public boolean handleAggregate() {
		if (handleAggregate == null) {
			handleAggregate = calculateHandleAggregate();
		}
		return handleAggregate;
	}

	/**
	 * Determines whether the <code>Expression</code> handles a collection of sub-expressions that
	 * are separated by commas.
	 *
	 * @return <code>true</code> if the sub-expression to parse might have several sub-expressions
	 * separated by commas; <code>false</code> otherwise
	 */
	public boolean handleCollection() {
		if (handleCollection == null) {
			handleCollection = calculateHandleCollection();
		}
		return handleCollection;
	}

	/**
	 * Determines if this query BNF support the given word, which can be an identifier.
	 *
	 * @param word A word that could be a JPQL identifier or anything else
	 * @return <code>true</code> if the given word is a JPQL identifier and it is supported by this
	 * BNF; <code>false</code> otherwise
	 */
	public final boolean hasIdentifier(String word) {
		populateIdentifiers();
		return cachedIdentifiers.containsKey(word);
	}

	/**
	 * Retrieves the identifiers that are supported by this BNF.
	 *
	 * @return The list of JPQL identifiers that are supported by this BNF
	 */
	public final Iterable<String> identifiers() {
		populateIdentifiers();
		return cachedIdentifiers.keySet();
	}

	/**
	 * Initializes this BNF rule by registering child {@link JPQLQueryBNF BNF rules} and {@link
	 * ExpressionFactory expression factories}.
	 */
	void initialize() {
	}

	/**
	 * Determines whether this BNF has child BNFs registered only to properly parse a query or if the
	 * child BNFs are part of the BNF. An example if a compound BNF is {@link BetweenExpressionBNF},
	 * it registers a series of children BNFs but they shouldn't be used to determine if they are
	 * part of that BNF since the comparator identifiers are.
	 *
	 * @return <code>false</code> by default
	 */
	public boolean isCompound() {
		return false;
	}

	/**
	 * Returns the set of all the query BNFs that are part of this BNF. The set always include this
	 * BNF as well.
	 *
	 * @return The children BNFs describing this BNF rule. The set excludes BNF rules that are used
	 * to complete a BNF, such as the BNF rules defined for <b>BETWEEN</b> since they are required
	 * to properly parse the query
	 */
	public final Set<JPQLQueryBNF> nonCompoundChildren() {
		if (childNonCompoundQueryBNFs == null) {
			childNonCompoundQueryBNFs = buildChildren(buildNonCompoundFilter());
		}
		return childNonCompoundQueryBNFs;
	}

	private void populateIdentifiers() {
		if (cachedIdentifiers == null) {
			cachedIdentifiers = buildIdentifiers();
		}
	}

	private JPQLQueryBNF queryBNF(String id) {
		return AbstractExpression.queryBNF(id);
	}

	/**
	 * Registers the unique identifier of the BNF rule as a child of this BNF rule.
	 *
	 * @param queryBNF The unique identifier of the BNF rule
	 */
	final void registerChild(String queryBNF) {
		if (children == null) {
			children = new ArrayList<String>();
		}
		children.add(queryBNF);
	}

	/**
	 * Registers a unique identifier that will be used to create the {@link Expression representing
	 * this BNF rule.
	 *
	 * @param expressionFactory The unique identifier that is responsible to create the
	 * {@link Expression} for this BNF rule
	 */
	final void registerExpressionFactory(String expressionFactory) {
		expressionFactories.add(expressionFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("(id=");
		sb.append(id);
		sb.append(", expressionFactories=");
		sb.append(expressionFactories);
		sb.append(")");
		return sb.toString();
	}
}