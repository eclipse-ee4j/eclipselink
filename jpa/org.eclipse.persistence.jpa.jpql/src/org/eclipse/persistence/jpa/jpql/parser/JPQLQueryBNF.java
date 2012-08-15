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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.jpql.util.filter.Filter;
import org.eclipse.persistence.jpa.jpql.util.filter.NullFilter;
import org.eclipse.persistence.jpa.jpql.util.iterator.ArrayIterator;

/**
 * This defines a single Backus-Naur Form (BNF) of the JPQL grammar. The Java Persistence functional
 * specifications are:
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
 * @version 2.4.1
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class JPQLQueryBNF {

	/**
	 * Caches the {@link ExpressionFactory} mapped by their the JPQL identifier registered with those
	 * {@link ExpressionFactory}.
	 */
	private Map<String, ExpressionFactory> cachedExpressionFactories;

	/**
	 * Caches the collection of unique identifiers of the {@link ExpressionFactory} registered with
	 * this BNF rule.
	 */
	private String[] cachedExpressionFactoryIds;

	/**
	 * The list of JPQL identifiers that are known by this BNF rule and its children, they are
	 * retrieved by scanning the list of {@link ExpressionFactory} registered with this BNF rule
	 * and its children.
	 */
	private String[] cachedIdentifiers;

	/**
	 * Caches the children of this BNF rule (which actually includes this one as well).
	 */
	private JPQLQueryBNF[] childQueryBNFs;

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
	 * The list of unique identifiers for the {@link ExpressionFactory} that are registered with this
	 * BNF rule.
	 */
	private List<String> expressionFactoryIds;

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
	 * Caches the children of this BNF rule (which actually includes this one as well) but do not
	 * includes BNF rules that are used for compounding a rule.
	 */
	private JPQLQueryBNF[] nonCompoundChildren;

	/**
	 * Only keep one instance of the {@link Filter} used when initializing {@link #childNonCompoundQueryBNFs}.
	 * This should help with performance since the filter won't be created for each <code>JPQLQueryBNF</code>.
	 */
	private static final Filter<JPQLQueryBNF> nonCompoundFilter = buildNonCompoundFilter();

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

	private static Filter<JPQLQueryBNF> buildNonCompoundFilter() {
		return new Filter<JPQLQueryBNF>() {
			public boolean accept(JPQLQueryBNF queryBNF) {
				return !queryBNF.isCompound();
			}
		};
	}

	/**
	 * Adds to the given set the child BNF rules and requests them to add their children as long as
	 * they have not been traversed already.
	 *
	 * @param queryBNFs The set to add the child BNF rules
	 * @param filter The {@link Filter} determines if the children of a given BNF should be added
	 */
	private void addChildren(Set<JPQLQueryBNF> queryBNFs, Filter<JPQLQueryBNF> filter) {

		// null children means no child JPQLQueryBNF was registered
		if (children != null) {

			for (String id : children) {
				JPQLQueryBNF queryBNF = expressionRegistry.getQueryBNF(id);

				if (queryBNFs.add(queryBNF) && filter.accept(queryBNF)) {
					queryBNF.addChildren(queryBNFs, filter);
				}
			}
		}
	}

	private JPQLQueryBNF[] buildChildren(Filter<JPQLQueryBNF> filter) {

		Set<JPQLQueryBNF> queryBNFs = new HashSet<JPQLQueryBNF>();
		queryBNFs.add(this);
		addChildren(queryBNFs, filter);

		JPQLQueryBNF[] children = new JPQLQueryBNF[queryBNFs.size()];
		queryBNFs.toArray(children);

		return children;
	}

	private void calculateExpressionFactories() {

		synchronized (this) {

			if (cachedExpressionFactories == null) {

				Map<String, ExpressionFactory> factories = new HashMap<String, ExpressionFactory>();

				// Caches the ExpressionFactory mapped by all the JPQL identifiers that
				// are registered for that ExpressionFactory
				for (String expressionFactoryId : getExpressionFactoryIdsImp()) {
					ExpressionFactory expressionFactory = expressionRegistry.getExpressionFactory(expressionFactoryId);

					for (String identifier : expressionFactory.identifiers()) {
						factories.put(identifier, expressionFactory);
					}
				}

				cachedIdentifiers = new String[factories.size()];
				factories.keySet().toArray(cachedIdentifiers);

				cachedExpressionFactories = factories;
			}
		}
	}

	private void calculateExpressionFactoryIds(Set<JPQLQueryBNF> queryBNFs, Set<String> factoryIds) {

		if (expressionFactoryIds != null) {
			factoryIds.addAll(expressionFactoryIds);
		}

		for (JPQLQueryBNF queryBNF : getChildren()) {
			if ((queryBNF != this) && (queryBNFs.add(queryBNF))) {
				queryBNF.calculateExpressionFactoryIds(queryBNFs, factoryIds);
			}
		}
	}

	private Boolean calculateHandleAggregate(Set<JPQLQueryBNF> queryBNFs) {

		if (handleAggregate != null) {
			return handleAggregate;
		}

		for (JPQLQueryBNF queryBNF : getChildren()) {
			if ((queryBNF != this) && queryBNFs.add(queryBNF)) {
				Boolean result = queryBNF.calculateHandleAggregate(queryBNFs);
				if (result == Boolean.TRUE) {
					return result;
				}
			}
		}

		return Boolean.FALSE;
	}

	private Boolean calculateHandleCollection(Set<JPQLQueryBNF> queryBNFs) {

		if (handleCollection != null) {
			return handleCollection;
		}

		for (JPQLQueryBNF queryBNF : getChildren()) {
			if ((queryBNF != this) && queryBNFs.add(queryBNF)) {
				Boolean result = queryBNF.calculateHandleCollection(queryBNFs);
				if (result == Boolean.TRUE) {
					return result;
				}
			}
		}

		return Boolean.FALSE;
	}

	/**
	 * Returns the set of all the query BNFs that are part of this BNF. The set always include this
	 * BNF as well.
	 *
	 * @return The children BNFs describing this BNF rule
	 */
	public Iterable<JPQLQueryBNF> children() {
		return new ArrayIterator<JPQLQueryBNF>(getChildren());
	}

	private JPQLQueryBNF[] getChildren() {

		// No need to synchronize if the list of children was calculated
		if (childQueryBNFs != null) {
			return childQueryBNFs;
		}

		// Synchronize to make sure only one thread populates the list of children
		synchronized (this) {
			if (childQueryBNFs == null) {
				childQueryBNFs = buildChildren(NullFilter.<JPQLQueryBNF>instance());
			}
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

		// No need to synchronize if the map was calculated
		if (cachedExpressionFactories != null) {
			return cachedExpressionFactories.get(identifier.toUpperCase());
		}

		// Synchronize to make sure only one thread populates the list of JPQL identifiers
		calculateExpressionFactories();

		return cachedExpressionFactories.get(identifier.toUpperCase());
	}

	/**
	 * Returns the unique identifiers of the {@link ExpressionFactory} handled by this BNF rule,
	 * which includes those from the children as well.
	 *
	 * @return The list of unique identifiers of the {@link ExpressionFactory} registered with this
	 * BNF rule and with its children
	 */
	public Iterable<String> getExpressionFactoryIds() {
		return new ArrayIterator<String>(getExpressionFactoryIdsImp());
	}

	private String[] getExpressionFactoryIdsImp() {

		// No need to synchronize if the list of cached ExpressionFactory was calculated
		if (cachedExpressionFactoryIds != null) {
			return cachedExpressionFactoryIds;
		}

		// Synchronize to make sure only one thread populates the list of ExpressionFactory IDs
		synchronized (this) {

			if (cachedExpressionFactoryIds == null) {

				Set<JPQLQueryBNF> queryBNFs = new HashSet<JPQLQueryBNF>();
				Set<String> factoryIds = new HashSet<String>();
				calculateExpressionFactoryIds(queryBNFs, factoryIds);

				cachedExpressionFactoryIds = new String[factoryIds.size()];
				factoryIds.toArray(cachedExpressionFactoryIds);
			}
		}

		return cachedExpressionFactoryIds;
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
	 * Retrieves the JPQL identifiers that are supported by this BNF rule. The JPQL identifiers are
	 * retrieved by scanning the {@link ExpressionFactory} registered with this BNF rule and the
	 * child BNF rules.
	 *
	 * @return The list of JPQL identifiers that are supported by this BNF
	 */
	public Iterable<String> getIdentifiers() {

		// No need to synchronize if the cached JPQL identifiers was calculated
		// Note: it could be possible cachedIdentifiers was instantiated but the list of JPQL
		// identifiers has not been copied from cachedExpressionFactoryIds yet.
		// cachedExpressionFactoryIds is set at the end of the method, insuring proper initialization
		if (cachedExpressionFactoryIds != null) {
			return new ArrayIterator<String>(cachedIdentifiers);
		}

		// Synchronize to make sure only one thread populates the list of JPQL identifiers
		calculateExpressionFactories();

		return new ArrayIterator<String>(cachedIdentifiers);
	}

	/**
	 * Determines whether the {@link Expression} handles a collection of sub-expressions that
	 * are aggregated by logical or arithmetic operators.
	 *
	 * @return <code>true</code> if the sub-expression to parse might have several logical and/or
	 * arithmetic expressions; <code>false</code> otherwise
	 */
	public boolean handleAggregate() {

		// No need to synchronize if the property was calculated
		if (handleAggregate != null) {
			return handleAggregate;
		}

		// Synchronize to make sure only one thread calculates it
		synchronized (this) {
			if (handleAggregate == null) {
				Set<JPQLQueryBNF> children = new HashSet<JPQLQueryBNF>();
				handleAggregate = calculateHandleAggregate(children);
			}
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

		// No need to synchronize if the property was calculated
		if (handleCollection != null) {
			return handleCollection;
		}

		// Synchronize to make sure only one thread calculates it
		synchronized (this) {
			if (handleCollection == null) {
				Set<JPQLQueryBNF> children = new HashSet<JPQLQueryBNF>();
				handleCollection = calculateHandleCollection(children);
			}
		}

		return handleCollection;
	}

	/**
	 * Determines whether this BNF handles parsing a sub-expression, i.e. parsing an expression
	 * encapsulated by parenthesis. See {@link #setHandleSubExpression(boolean)} for more details.
	 *
	 * @return <code>true</code> if this BNF handles parsing a sub-expression; <code>false</code> otherwise
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

		// No need to synchronize if the map of cached ExpressionFactory was calculated
		if (cachedExpressionFactories != null) {
			return cachedExpressionFactories.containsKey(word);
		}

		// Synchronize to make sure only one thread calculates it
		calculateExpressionFactories();

		return cachedExpressionFactories.containsKey(word);
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
	public Iterable<JPQLQueryBNF> nonCompoundChildren() {

		// No need to synchronize if the list of child BNFs (non-compound) was calculated
		if (nonCompoundChildren != null) {
			return new ArrayIterator<JPQLQueryBNF>(nonCompoundChildren);
		}

		// Synchronize to make sure only one thread populates the list of child BNFs (non-compound)
		synchronized (this) {
			if (nonCompoundChildren == null) {
				nonCompoundChildren = buildChildren(nonCompoundFilter);
			}
		}

		return new ArrayIterator<JPQLQueryBNF>(nonCompoundChildren);
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
			children = new LinkedList<String>();
		}

		children.add(queryBNFId);
	}

	/**
	 * Registers a unique identifier of the {@link ExpressionFactory} to register with this BNF rule.
	 *
	 * @param expressionFactoryId The unique identifier of the {@link ExpressionFactory}
	 * @exception NullPointerException The <code>expressionFactoryId</code> cannot be <code>null</code>
	 */
	protected final void registerExpressionFactory(String expressionFactoryId) {

		if (expressionFactoryId == null) {
			throw new NullPointerException("The expressionFactoryId cannot be null");
		}

		if (expressionFactoryIds == null) {
			expressionFactoryIds = new LinkedList<String>();
		}

		expressionFactoryIds.add(expressionFactoryId);
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
		sb.append(getExpressionFactoryIdsImp());
		sb.append(")");
	}
}