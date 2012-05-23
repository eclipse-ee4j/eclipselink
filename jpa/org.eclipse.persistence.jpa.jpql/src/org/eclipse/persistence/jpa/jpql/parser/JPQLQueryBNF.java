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
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.jpql.util.filter.Filter;
import org.eclipse.persistence.jpa.jpql.util.filter.NullFilter;

/**
 * This defines a single Backus-Naur Form (BNF) of the JPQL grammar. The Java Persistence functional
 * specification are:
 * <p>
 * {@link JPQLGrammar1_0}: <a href="http://jcp.org/en/jsr/detail?id=220">JSR 220: Enterprise JavaBeans&trade; version 3.0</a>
 * <br>
 * {@link JPQLGrammar2_0}: <a href="http://jcp.org/en/jsr/detail?id=317">JSR 317: Java&trade; Persistence 2.0</a>
 * <p>
 * {@link JPQLGrammar2_1}: <a href="http://jcp.org/en/jsr/detail?id=338">JSR 338: Java&trade; Persistence 2.1</a>
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
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
	 * Determines whether this BNF has child BNFs registered only to properly parse a query or if the
	 * child BNFs are part of the BNF. An example if a compound BNF is {@link BetweenExpressionBNF},
	 * it registers a series of children BNFs but they shouldn't be used to determine if they are
	 * part of that BNF since the comparator identifiers are.
	 */
	private boolean compound;

	/**
	 * The list of identifiers that are part of the this BNF rule.
	 */
	private List<String> expressionFactories;

	/**
	 * The {@link ExpressionRegistry} with which this {@link JPQLQueryBNF} was registered.
	 */
	private ExpressionRegistry expressionRegistry;

	/**
	 * The unique identifier of the {@link JPQLQueryBNF} to use in the last resort.
	 */
	private String fallbackBNFId;

	/**
	 * The unique identifier of the {@link ExpressionFactory} to use when no other factories can be
	 * used automatically.
	 */
	private String fallbackExpressionFactoryId;

	/**
	 * Caches the property since any BNF rule is static.
	 */
	private Boolean handleAggregate;

	/**
	 * Caches the property since any BNF rule is static.
	 */
	private Boolean handleCollection;

	/**
	 * This flag can be used to determine if this BNF handles parsing a sub-expression, i.e. an
	 * expression encapsulated by parenthesis. See {@link #setHandleSubExpression(boolean)} for more
	 * details.
	 */
	private boolean handleSubExpression;

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
	 * @exception NullPointerException The given unique identifier cannot be <code>null</code>
	 */
	protected JPQLQueryBNF(String id) {
		super();
		initialize(id);
	}

	/**
	 * Adds to the given set the child BNF rules and requests them to add their children as long as
	 * they have not been traversed already.
	 *
	 * @param queryBNFs The set to add the child BNF rules
	 * @param filter The {@link Filter} determines if the children of a given BNF should be added
	 */
	protected void addChildren(Set<JPQLQueryBNF> queryBNFs, Filter<JPQLQueryBNF> filter) {

		if (children != null) {
			for (String id : children) {
				JPQLQueryBNF queryBNF = expressionRegistry.getQueryBNF(id);

				if (queryBNFs.add(queryBNF) && filter.accept(queryBNF)) {
					queryBNF.addChildren(queryBNFs, filter);
				}
			}
		}
	}

	private Set<JPQLQueryBNF> buildChildren(Filter<JPQLQueryBNF> filter) {
		Set<JPQLQueryBNF> queryBNFs = new HashSet<JPQLQueryBNF>();
		queryBNFs.add(this);
		addChildren(queryBNFs, buildNonCompoundFilter());
		return queryBNFs;
	}

	private Map<String, ExpressionFactory> buildIdentifiers() {

		Map<String, ExpressionFactory> identifiers = new HashMap<String, ExpressionFactory>();

		for (String expressionFactoryId : getExpressionFactoryIds()) {
			ExpressionFactory expressionFactory = expressionRegistry.getExpressionFactory(expressionFactoryId);

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
	public Set<JPQLQueryBNF> children() {
		if (childQueryBNFs == null) {
			childQueryBNFs = buildChildren(NullFilter.<JPQLQueryBNF>instance());
			childQueryBNFs = Collections.unmodifiableSet(childQueryBNFs);
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
	public ExpressionFactory getExpressionFactory(String identifier) {
		populateIdentifiers();
		return cachedIdentifiers.get(identifier.toUpperCase());
	}

	/**
	 * Returns the unique identifiers of the {@link ExpressionFactory expression factories} handled
	 * by this BNF rule.
	 *
	 * @return The list of unique identifiers for each {@link ExpressionFactory}
	 */
	public Set<String> getExpressionFactoryIds() {

		if (cachedFactories == null) {

			// First add all the children
			cachedFactories = new HashSet<String>();
			cachedFactories.addAll(expressionFactories);

			for (JPQLQueryBNF queryBNF : children()) {
				if (queryBNF != this) {
					cachedFactories.addAll(queryBNF.getExpressionFactoryIds());
				}
			}

			cachedFactories = Collections.unmodifiableSet(cachedFactories);
		}

		return cachedFactories;
	}

	/**
	 * Returns the registry containing the {@link JPQLQueryBNF JPQLQueryBNFs} and the {@link
	 * org.eclipse.persistence.jpa.jpql.parser.ExpressionFactory ExpressionFactories} that are used
	 * to properly parse a JPQL query.
	 *
	 * @return The registry containing the information related to the JPQL grammar
	 */
	public ExpressionRegistry getExpressionRegistry() {
		return expressionRegistry;
	}

	/**
	 * When parsing the query and no {@link JPQLQueryBNF JPQLQueryBNFs} can help to parse the query,
	 * then it will fall back on this one.
	 *
	 * @return The unique identifier of the {@link JPQLQueryBNF} to use in the last resort
	 */
	public String getFallbackBNFId() {
		return fallbackBNFId;
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
	public String getFallbackExpressionFactoryId() {
		return fallbackExpressionFactoryId;
	}

	/**
	 * Returns the unique identifier of this {@link JPQLQueryBNF}.
	 *
	 * @return The identifier used to register this {@link JPQLQueryBNF} with {@link AbstractExpression}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Retrieves the identifiers that are supported by this BNF.
	 *
	 * @return The list of JPQL identifiers that are supported by this BNF
	 */
	public Set<String> getIdentifiers() {
		populateIdentifiers();
		return cachedIdentifiers.keySet();
	}

	/**
	 * Determines whether the {@link Expression} handles a collection of sub-expressions that
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
	 * Determines whether this BNF handles parsing a sub-expression, i.e. parsing an expression
	 * encapsulated by parenthesis. See {@link #setHandleSubExpression(boolean)} for more details.
	 *
	 * @return <code>true</code> if this BNF handles parsing a sub-expression; <code>false</code>
	 * otherwise
	 */
	public boolean handlesSubExpression() {
		return handleSubExpression;
	}

	/**
	 * Determines if this query BNF support the given word, which can be an identifier.
	 *
	 * @param word A word that could be a JPQL identifier or anything else
	 * @return <code>true</code> if the given word is a JPQL identifier and it is supported by this
	 * BNF; <code>false</code> otherwise
	 */
	public boolean hasIdentifier(String word) {
		populateIdentifiers();
		return cachedIdentifiers.containsKey(word);
	}

	/**
	 * Initializes this BNF by registering child {@link JPQLQueryBNF JPQLQueryBNFs} and {@link
	 * ExpressionFactory ExpressionFactories}.
	 */
	protected void initialize() {
	}

	/**
	 * Initializes this <code>JPQLQueryBNF</code>.
	 *
	 * @param id The unique identifier of this BNF rule
	 * @exception NullPointerException The given unique identifier cannot be <code>null</code>
	 */
	private void initialize(String id) {

		if (id == null) {
			throw new NullPointerException("The unique identifier of this JPQLQueryBNF cannot be null");
		}

		this.id = id;
		this.expressionFactories = new LinkedList<String>();

		initialize();
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
		return compound;
	}

	/**
	 * Returns the set of all the query BNFs that are part of this BNF. The set always include this
	 * BNF as well.
	 *
	 * @return The children BNFs describing this BNF rule. The set excludes BNF rules that are used
	 * to complete a BNF, such as the BNF rules defined for <b>BETWEEN</b> since they are required
	 * to properly parse the query
	 */
	public Set<JPQLQueryBNF> nonCompoundChildren() {
		if (childNonCompoundQueryBNFs == null) {
			childNonCompoundQueryBNFs = buildChildren(buildNonCompoundFilter());
			childNonCompoundQueryBNFs = Collections.unmodifiableSet(childNonCompoundQueryBNFs);
		}
		return childNonCompoundQueryBNFs;
	}

	private void populateIdentifiers() {
		if (cachedIdentifiers == null) {
			cachedIdentifiers = buildIdentifiers();
		}
	}

	/**
	 * Registers the unique identifier of the BNF rule as a child of this BNF rule.
	 *
	 * @param queryBNFId The unique identifier of the BNF rule to add as a child
	 * @exception NullPointerException The <code>queryBNFId</code> cannot be <code>null</code>
	 */
	protected final void registerChild(String queryBNFId) {
		if (queryBNFId == null) {
			throw new NullPointerException("The queryBNFId cannot be null");
		}
		if (children == null) {
			children = new ArrayList<String>();
		}
		children.add(queryBNFId);
	}

	/**
	 * Registers a unique identifier that will be used to create the {@link Expression} representing
	 * this BNF rule.
	 *
	 * @param expressionFactory The unique identifier that is responsible to create the
	 * {@link Expression} for this BNF rule
	 */
	protected final void registerExpressionFactory(String expressionFactory) {
		expressionFactories.add(expressionFactory);
	}

	/**
	 * Determines whether this BNF has child BNFs registered only to properly parse a query or if the
	 * child BNFs are part of the BNF. An example if a compound BNF is {@link BetweenExpressionBNF},
	 * it registers a series of children BNFs but they shouldn't be used to determine if they are
	 * part of that BNF since the comparator identifiers are.
	 *
	 * @param compound <code>true</code> if this BNF represents a compound BNF and its children are
	 * not part of this BNF but only to support compound expression; <code>false</code> otherwise
	 */
	public void setCompound(boolean compound) {
		this.compound = compound;
	}

	/**
	 * Sets the backpointer to the {@link ExpressionRegistry} with which this {@link JPQLQueryBNF}
	 * was registered.
	 *
	 * @param expressionRegistry The registry for a given JPQL grammar
	 */
	final void setExpressionRegistry(ExpressionRegistry expressionRegistry) {
		this.expressionRegistry = expressionRegistry;
	}

	/**
	 * When parsing the query and no {@link JPQLQueryBNF JPQLQueryBNFs} can help to parse the query,
	 * then it will fall back on the given one.
	 *
	 * @param fallbackBNFId The unique identifier of the {@link JPQLQueryBNF} to use in the last resort
	 */
	public void setFallbackBNFId(String fallbackBNFId) {
		this.fallbackBNFId = fallbackBNFId;
	}

	/**
	 * Sets the unique identifier of the {@link ExpressionFactory} to use when the fall back BNF
	 * ID is not <code>null</code>. This will be used to parse a portion of the query when the
	 * registered {@link ExpressionFactory expression factories} cannot parse it.
	 * <p>
	 * Note: This method is only called if {@link #getFallbackBNFId()} does not return <code>null</code>.
	 *
	 * @param fallbackExpressionFactoryId The unique identifier of the {@link ExpressionFactory}
	 */
	public void setFallbackExpressionFactoryId(String fallbackExpressionFactoryId) {
		this.fallbackExpressionFactoryId = fallbackExpressionFactoryId;
	}

	/**
	 * Sets whether the {@link Expression} handles a collection of sub-expressions that are
	 * aggregated by logical or arithmetic operators.
	 *
	 * @param handleAggregate <code>true</code> if the sub-expression to parse might have several
	 * logical and/or arithmetic expressions; <code>false</code> otherwise
	 */
	public void setHandleAggregate(boolean handleAggregate) {
		this.handleAggregate = handleAggregate;
	}

	/**
	 * Sets whether the <code>Expression</code> handles a collection of sub-expressions that are
	 * separated by commas.
	 *
	 * @param handleCollection <code>true</code> if the sub-expression to parse might have several
	 * sub-expressions separated by commas; <code>false</code> otherwise
	 */
	public void setHandleCollection(boolean handleCollection) {
		this.handleCollection = handleCollection;
	}

	/**
	 * Sets whether this BNF handles parsing a sub-expression, i.e. parsing an expression
	 * encapsulated by parenthesis. Which in fact would be handled by the fallback {@link
	 * ExpressionFactory}. The default behavior is to not handle it.
	 * <p>
	 * A good example for using this option is when an {@link Expression} cannot use any {@link
	 * ExpressionFactory} for creating a child object, parsing will use the fallback {@link
	 * ExpressionFactory}, if one was specified. So when this is set to <code>true</code>, the
	 * fallback {@link ExpressionFactory} will be immediately invoked.
	 * <p>
	 * Let's say we want to parse "SELECT e FROM (SELECT a FROM Address a) e", {@link FromClause}
	 * cannot use a factory for parsing the entity name (that's what usually the <code>FROM</code>
	 * clause has) so it uses the fallback factory to create {@link IdentificationVariableDeclaration}.
	 * Then <code>IdentificationVariableDeclaration</code> also cannot use any factory to create its
	 * child object so it uses the fallback factory to create {@link RangeVariableDeclaration}.
	 * By changing the status of for handling the sub-expression for the BNFs for those objects, then
	 * a subquery can be created by <code>RangeVariableDeclaration</code>.
	 *
	 * <pre><code>FromClause
	 *  |- IdentificationVariableDeclaration
	 *       |- RangeVariableDeclaration
	 *            |- SubExpression(subquery)</code></pre>
	 *
	 * In order to get this working, the following would have to be done into the grammar:
	 *
	 * <pre><code> public class MyJPQLGrammar extends AbstractJPQLGrammar {
	 *   &#64;Override
	 *   protected void initializeBNFs() {
	 *      setHandleSubExpression(InternalFromClauseBNF.ID,                true);
	 *      setHandleSubExpression(InternalSimpleFromClauseBNF.ID,          true);
	 *      setHandleSubExpression(IdentificationVariableDeclarationBNF.ID, true);
	 *      setHandleSubExpression(RangeVariableDeclarationBNF.ID,          true);
	 *   }
	 * }</code></pre>
	 *
	 * @param handleSubExpression <code>true</code> to let the creation of a sub-expression be
	 * created by the fallback {@link ExpressionFactory} registered with this BNF; <code>false</code>
	 * otherwise (which is the default value)
	 * @return <code>true</code> if the fallback {@link ExpressionFactory} registered with this BNF
	 * handles parsing a sub-expression; <code>false</code> otherwise
	 */
	public void setHandleSubExpression(boolean handleSubExpression) {
		this.handleSubExpression = handleSubExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		toString(sb);
		return sb.toString();
	}

	/**
	 * Adds to the given builder more information about this {@link JPQLQueryBNF}.
	 *
	 * @param sb The builder used to add information about this class
	 */
	protected void toString(StringBuilder sb) {
		sb.append("(id=");
		sb.append(id);
		sb.append(", identifiers=");
		sb.append(getIdentifiers());
		sb.append(", expressionFactories=");
		sb.append(getExpressionFactoryIds());
		sb.append(")");
	}
}