/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

/**
 * The abstract definition of a {@link JPQLGrammar}. The grammar defines how a JPQL query is parsed,
 * which is based on the BNF defined for that grammar.
 *
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractJPQLGrammar implements JPQLGrammar {

	/**
	 * The base {@link JPQLGrammar} this one extends or <code>null</code> if this is the base grammar.
	 */
	private JPQLGrammar jpqlGrammar;

	/**
	 * The registry contains the {@link JPQLQueryBNF JPQLQueryBNFs} and the {@link ExpressionFactory
	 * ExpressionFactories} and the support JPQL identifiers.
	 */
	private ExpressionRegistry registry;

	/**
	 * Creates a new <code>AbstractJPQLGrammar</code>.
	 */
	protected AbstractJPQLGrammar() {
		super();
		initialize();
	}

	/**
	 * Creates a new <code>AbstractJPQLGrammar</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
	 * instantiating the base {@link JPQLGrammar}
	 */
	protected AbstractJPQLGrammar(AbstractJPQLGrammar jpqlGrammar) {
		super();
		initialize(jpqlGrammar);
	}

	/**
	 * Adds to the given query BNF a child BNF.
	 *
	 * @param queryBNFId The unique identifier of the parent BNF to add a child BNF
	 * @param childQueryBNFId The unique identifier of the child to add to the parent BNF
	 */
	public void addChildBNF(String queryBNFId, String childQueryBNFId) {
		registry.addChildBNF(queryBNFId, childQueryBNFId);
	}

	/**
	 * Adds to the given unique identifier of an {@link ExpressionFactory} to the given query BNF.
	 *
	 * @param queryBNFId The unique identifier of the parent BNF
	 * @param childExpressionFactoryId The unique identifier of the {@link ExpressionFactory} to add
	 * to the given query BNF
	 */
	public void addChildFactory(String queryBNFId, String childExpressionFactoryId) {
		registry.addChildFactory(queryBNFId, childExpressionFactoryId);
	}

	/**
	 * Adds the given JPQL identifier to this factory.
	 *
	 * @param expressionFactoryId The unique identifier of the {@link ExpressionFactory} to add more
	 * JPQL identifiers
	 * @param identifier The JPQL identifier this factory will parse
	 */
	public void addIdentifier(String expressionFactoryId, String identifier) {
		registry.addIdentifier(expressionFactoryId, identifier);
	}

	/**
	 * Adds the given JPQL identifiers to this factory.
	 *
	 * @param expressionFactoryId The unique identifier of the {@link ExpressionFactory} to add more
	 * JPQL identifiers
	 * @param identifiers The JPQL identifiers this factory will parse
	 */
	public void addIdentifiers(String expressionFactoryId, String... identifiers) {
		registry.addIdentifiers(expressionFactoryId, identifiers);
	}

	/**
	 * Creates the base {@link JPQLGrammar} this one extends, if one exists.
	 * <p>
	 * <b>IMPORTANT:</b> The singleton instance of any {@link JPQLGrammar} (for example {@link
	 * JPQLGrammar1_0#instance() JPQLGrammar1_0.instance()} cannot be used, the API does not support
	 * extending it, a new instance has to be created.
	 *
	 * @return The base {@link JPQLGrammar} or <code>null</code> if there is no base grammar
	 */
	protected abstract JPQLGrammar buildBaseGrammar();

	/**
	 * Creates a new {@link ExpressionRegistry} that will be used to store the definition of the JPQL
	 * grammar. This method is invoked if {@link #buildJPQLGrammar()} returns <code>null</code>.
	 *
	 * @return The registry of {@link JPQLQueryBNF JPQLQueryBNFs}, {@link ExpressionFactory
	 * ExpressionFactories} and the JPQL identifiers
	 */
	protected ExpressionRegistry buildExpressionRegistry() {
		return new ExpressionRegistry();
	}

	/**
	 * Creates the base {@link JPQLGrammar} this one extends.
	 *
	 * @return The base {@link JPQLGrammar} or <code>null</code> if this is the base grammar
	 */
	public JPQLGrammar getBaseGrammar() {
		return jpqlGrammar;
	}

	/**
	 * {@inheritDoc}
	 */
	public ExpressionRegistry getExpressionRegistry() {
		return registry;
	}

	/**
	 * Initializes this JPQL grammar.
	 */
	protected void initialize() {

		// Create the base grammar, if this one is an extension
		jpqlGrammar = buildBaseGrammar();

		// There is no base grammar, create the local ExpressionRegistry
		if (jpqlGrammar == null) {
			registry = buildExpressionRegistry();
		}
		// This grammar extends another grammar, simply use the base grammar's
		// ExpressionRegistry so this grammar can modify it
		else {
			registry = jpqlGrammar.getExpressionRegistry();
		}

		// Add the support provided by this grammar
		initializeIdentifiers();
		initializeBNFs();
		initializeExpressionFactories();
	}

	/**
	 * This method simply retrieves the {@link ExpressionRegistry} from the given {@link JPQLGrammar}
	 * and extend its grammar by calling the initialize methods from this one.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
	 * instantiating the base {@link JPQLGrammar}
	 */
	private void initialize(AbstractJPQLGrammar jpqlGrammar) {

		registry = jpqlGrammar.registry;

		// Add the support provided by this grammar
		initializeIdentifiers();
		initializeBNFs();
		initializeExpressionFactories();
	}

	/**
	 * Registers the JPQL query BNFs defining the JPQL grammar.
	 */
	protected abstract void initializeBNFs();

	/**
	 * Registers the {@link ExpressionFactory ExpressionFactories} required to properly parse JPQL
	 * queries. An {@link ExpressionFactory} is responsible to create an {@link Expression} object
	 * that represents a portion of the JPQL query.
	 */
	protected abstract void initializeExpressionFactories();

	/**
	 * Registers the JPQL identifiers support by this {@link IJPQLExtension}. The registration
	 * involves registering the {@link JPAVersion} and the {@link IdentifierRole}.
	 */
	protected abstract void initializeIdentifiers();

	/**
	 * Registers the given {@link JPQLQueryBNF}. The BNF will be registered using its unique
	 * identifier.
	 *
	 * @param queryBNF The {@link JPQLQueryBNF} to store
	 */
	protected void registerBNF(JPQLQueryBNF queryBNF) {
		registry.registerBNF(queryBNF);
	}

	/**
	 * Registers the given {@link ExpressionFactory} by storing it for all its identifiers.
	 *
	 * @param expressionFactory The {@link ExpressionFactory} to store
	 */
	protected void registerFactory(ExpressionFactory expressionFactory) {
		registry.registerFactory(expressionFactory);
	}

	/**
	 * Registers the {@link IdentifierRole} for the given JPQL identifier.
	 *
	 * @param identifier The JPQL identifier to register its role within a JPQL query
	 * @param role The role of the given JPQL identifier
	 */
	protected void registerIdentifierRole(String identifier, IdentifierRole role) {
		registry.registerIdentifierRole(identifier, role);
	}

	/**
	 * Registers the {@link JPAVersion} for which the given JPQL identifier was defined.
	 *
	 * @param identifier The JPQL identifier to register in which version it was added to the grammar
	 * @param version The version when the JPQL identifier was added to the grammar
	 */
	protected void registerIdentifierVersion(String identifier, JPAVersion version) {
		registry.registerIdentifierVersion(identifier, version);
	}

	/**
	 * When parsing the query and no {@link JPQLQueryBNF JPQLQueryBNFs} can help to parse the query,
	 * then it will fall back on the given one.
	 *
	 * @param queryBNFId The unique identifier of the BNF to modify its fallback BNF unique identifier
	 * @param fallbackBNFId The unique identifier of the {@link JPQLQueryBNF} to use in the last resort
	 */
	public void setFallbackBNFId(String queryBNFId, String fallbackBNFId) {
		registry.setFallbackBNFId(queryBNFId, fallbackBNFId);
	}

	public void setHandleSubExpression(String queryBNFId, boolean handleSubExpression) {
		registry.setHandleSubExpression(queryBNFId, handleSubExpression);
	}

	/**
	 * Sets the unique identifier of the {@link ExpressionFactory} to use when the fall back BNF
	 * ID is not <code>null</code>. This will be used to parse a portion of the query when the
	 * registered {@link ExpressionFactory expression factories} cannot parse it.
	 * <p>
	 * Note: This method is only called if {@link JPQLQueryBNF#getFallbackBNFId() JPQLQueryBNF.
	 * getFallbackBNFId()} does not return <code>null</code>.
	 *
	 * @param queryBNFId The unique identifier of the BNF to modify its fallback expression factory
	 * unique identifier
	 */
	public void setFallbackExpressionFactoryId(String queryBNFId, String fallbackExpressionFactoryId) {
		registry.setFallbackExpressionFactoryId(queryBNFId, fallbackExpressionFactoryId);
	}
}