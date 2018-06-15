/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.JPAVersion;

/**
 * This registry contains the necessary information used by Hermes parser. When parsing a JPQL query,
 * the {@link JPQLGrammar} given to {@link JPQLExpression} will give access to this registry.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see JPQLGrammar
 * @see ExpressionFactory
 * @see JPQLQueryBNF
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class ExpressionRegistry {

    /**
     * The map of {@link ExpressionFactory ExpressionFactories} that have been registered and
     * required for parsing a JPQL query, they are mapped with their unique identifier.
     */
    private Map<String, ExpressionFactory> expressionFactories;

    /**
     * The set of the JPQL identifiers defined by the grammar.
     */
    private Map<String, IdentifierRole> identifiers;

    /**
     * This table specify in which JPA version the identifiers was introduced.
     */
    private Map<String, JPAVersion> identifierVersions;

    /**
     * The {@link JPQLQueryBNF} unique identifiers mapped to the only instance of the BNF rule.
     */
    private Map<String, JPQLQueryBNF> queryBNFs;

    /**
     * Creates the only instance of <code>ExpressionRegistry</code>.
     */
    public ExpressionRegistry() {
        super();
        initialize();
    }

    /**
     * Adds to the given query BNF a child BNF.
     *
     * @param queryBNFId The unique identifier of the parent BNF to add a child BNF
     * @param childQueryBNFId The unique identifier of the child to add to the parent BNF
     * @exception NullPointerException The {@link JPQLQueryBNF} identified by the given ID does not exist
     */
    public void addChildBNF(String queryBNFId, String childQueryBNFId) {
        JPQLQueryBNF queryBNF = queryBNFs.get(queryBNFId);
        Assert.isNotNull(queryBNF, "The JPQLQueryBNF identified with '" + queryBNFId + "' does not exist.");
        queryBNF.registerChild(childQueryBNFId);
    }

    /**
     * Adds to the given unique identifier of an {@link ExpressionFactory} to the given query BNF.
     *
     * @param queryBNFId The unique identifier of the parent BNF
     * @param childExpressionFactoryId The unique identifier of the {@link ExpressionFactory} to add
     * to the given query BNF
     * @exception NullPointerException The {@link JPQLQueryBNF} identified by the given ID does not exist
     */
    public void addChildFactory(String queryBNFId, String childExpressionFactoryId) {
        JPQLQueryBNF queryBNF = queryBNFs.get(queryBNFId);
        Assert.isNotNull(queryBNF, "The JPQLQueryBNF identified with '" + queryBNFId + "' does not exist.");
        queryBNF.registerExpressionFactory(childExpressionFactoryId);
    }

    /**
     * Adds the given JPQL identifier to this factory.
     *
     * @param expressionFactoryId The unique identifier of the {@link ExpressionFactory} to add more
     * JPQL identifiers
     * @param identifier The JPQL identifier this factory will parse
     */
    public void addIdentifier(String expressionFactoryId, String identifier) {
        getExpressionFactory(expressionFactoryId).addIdentifier(identifier);
    }

    /**
     * Adds the given JPQL identifiers to this factory.
     *
     * @param expressionFactoryId The unique identifier of the {@link ExpressionFactory} to add more
     * JPQL identifiers
     * @param identifiers The JPQL identifiers this factory will parse
     */
    public void addIdentifiers(String expressionFactoryId, String... identifiers) {
        getExpressionFactory(expressionFactoryId).addIdentifiers(identifiers);
    }

    /**
     * Retrieves the {@link ExpressionFactory} that is responsible for creating the {@link Expression}
     * object that represents the given JPQL identifier.
     *
     * @param identifier The JPQL identifier for which its factory is searched
     * @return Either the {@link ExpressionFactory} that creates the {@link Expression} or
     * <code>null</code> if none was found
     */
    public ExpressionFactory expressionFactoryForIdentifier(String identifier) {

        identifier = identifier.toUpperCase();

        if (Expression.SELECT.equalsIgnoreCase(identifier)) {
            return getExpressionFactory(SimpleSelectStatementFactory.ID);
        }

        for (ExpressionFactory expressionFactory : expressionFactories.values()) {
            boolean found = Arrays.binarySearch(expressionFactory.identifiers(), identifier) > -1;
            if (found) {
                return expressionFactory;
            }
        }

        return null;
    }

    /**
     * Retrieves the registered {@link ExpressionFactory} that was registered for the given unique identifier.
     *
     * @param expressionFactoryId The unique identifier of the {@link ExpressionFactory} to retrieve
     * @return The {@link ExpressionFactory} mapped with the given unique identifier
     */
    public ExpressionFactory getExpressionFactory(String expressionFactoryId) {
        return expressionFactories.get(expressionFactoryId);
    }

    /**
     * Retrieves the {@link IdentifierRole} of the given JPQL identifier. A role helps to describe
     * the purpose of the identifier in a JPQL query.
     *
     * @param identifier The JPQL identifier for which its role is returned
     * @return The {@link IdentifierRole} of the given JPQL identifier
     */
    public IdentifierRole getIdentifierRole(String identifier) {
        return identifiers.get(identifier.toUpperCase());
    }

    /**
     * Returns the JPQL identifiers that are supported by the {@link JPQLGrammar JPQL grammar}.
     *
     * @return The supported JPQL identifiers
     */
    public Set<String> getIdentifiers() {
        return Collections.unmodifiableSet(identifiers.keySet());
    }

    /**
     * Retrieves the JPQL identifiers that are supported by the BNF rule with the given unique
     * identifier. The JPQL identifiers are retrieved by scanning the {@link ExpressionFactory}
     * registered with the BNF rule and its child BNF rules.
     *
     * @return The list of JPQL identifiers that are supported by a BNF rule
     */
    public Iterable<String> getIdentifiers(String queryBNFId) {
        return getQueryBNF(queryBNFId).getIdentifiers();
    }

    /**
     * Retrieves the JPA version in which the identifier was first introduced.
     *
     * @return The version in which the identifier was introduced
     */
    public JPAVersion getIdentifierVersion(String identifier) {
        JPAVersion version = identifierVersions.get(identifier.toUpperCase());
        return (version != null) ? version : JPAVersion.VERSION_1_0;
    }

    /**
     * Retrieves the BNF object that was registered for the given unique identifier.
     *
     * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} to retrieve
     * @return The {@link JPQLQueryBNF} representing a section of the grammar
     */
    public JPQLQueryBNF getQueryBNF(String queryBNFId) {
        return queryBNFs.get(queryBNFId);
    }

    /**
     * Instantiates the only instance of various API used by the parser.
     */
    protected void initialize() {
        queryBNFs           = new HashMap<String, JPQLQueryBNF>();
        identifiers         = new HashMap<String, IdentifierRole>();
        expressionFactories = new HashMap<String, ExpressionFactory>();
        identifierVersions  = new HashMap<String, JPAVersion>();
    }

    /**
     * Determines if the given word is a JPQL identifier. The check is case insensitive.
     *
     * @param text The string value to test if it's a JPQL identifier based on what is registered
     * with this <code>ExpressionRegistry</code>
     * @return <code>true</code> if the word is an identifier, <code>false</code> otherwise
     */
    public boolean isIdentifier(String text) {
        return identifiers.containsKey(text.toUpperCase());
    }

    /**
     * Registers the given {@link JPQLQueryBNF}. The BNF will be registered using its unique
     * identifier.
     *
     * @param queryBNF The {@link JPQLQueryBNF} to store
     * @exception NullPointerException The {@link JPQLQueryBNF} cannot be <code>null</code>
     */
    public void registerBNF(JPQLQueryBNF queryBNF) {

        Assert.isNotNull(queryBNF, "The JPQLQueryBNF cannot be null");

        queryBNFs.put(queryBNF.getId(), queryBNF);
        queryBNF.setExpressionRegistry(this);
    }

    /**
     * Registers the given {@link ExpressionFactory} by storing it for all its identifiers.
     *
     * @param expressionFactory The {@link ExpressionFactory} to store
     * @exception NullPointerException The {@link ExpressionFactory} cannot be <code>null</code>
     */
    public void registerFactory(ExpressionFactory expressionFactory) {

        Assert.isNotNull(expressionFactory, "The ExpressionFactory cannot be null");

        expressionFactories.put(expressionFactory.getId(), expressionFactory);
        expressionFactory.setExpressionRegistry(this);
    }

    /**
     * Registers the {@link IdentifierRole} for the given JPQL identifier.
     *
     * @param identifier The JPQL identifier to register its role within a JPQL query
     * @param role The role of the given JPQL identifier
     * @exception NullPointerException The JPQL identifier and the {@link IdentifierRole} cannot be
     * <code>null</code>
     */
    public void registerIdentifierRole(String identifier, IdentifierRole role) {

        Assert.isNotNull(identifier, "The JPQL identifier cannot be null");
        Assert.isNotNull(role,       "The IdentifierRole cannot be null");

        identifiers.put(identifier, role);
    }

    /**
     * Registers the {@link JPAVersion} for which the given JPQL identifier was defined.
     *
     * @param identifier The JPQL identifier to register in which version it was added to the grammar
     * @param version The version when the JPQL identifier was added to the grammar
     * @exception NullPointerException The JPQL identifier and the {@link JPAVersion} cannot be
     * <code>null</code>
     */
    public void registerIdentifierVersion(String identifier, JPAVersion version) {

        Assert.isNotNull(identifier, "The JPQL identifier cannot be null");
        Assert.isNotNull(version,    "The JPAVersion cannot be null");

        identifierVersions.put(identifier, version);
    }

    /**
     * When parsing the query and no {@link JPQLQueryBNF JPQLQueryBNFs} can help to parse the query,
     * then it will fall back on the given one.
     *
     * @param queryBNFId The unique identifier of the BNF to modify its fallback BNF unique identifier
     * @param fallbackBNFId The unique identifier of the {@link JPQLQueryBNF} to use in the last resort
     * @exception NullPointerException The {@link JPQLQueryBNF} identified by the given ID does not exist
     */
    public void setFallbackBNFId(String queryBNFId, String fallbackBNFId) {
        JPQLQueryBNF queryBNF = queryBNFs.get(queryBNFId);
        Assert.isNotNull(queryBNF, "The JPQLQueryBNF identified with '" + queryBNFId + "' does not exist.");
        queryBNF.setFallbackBNFId(queryBNFId);
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
     * @exception NullPointerException The {@link JPQLQueryBNF} identified by the given ID does not exist
     */
    public void setFallbackExpressionFactoryId(String queryBNFId, String fallbackExpressionFactoryId) {
        JPQLQueryBNF queryBNF = queryBNFs.get(queryBNFId);
        Assert.isNotNull(queryBNF, "The JPQLQueryBNF identified with '" + queryBNFId + "' does not exist.");
        queryBNF.setFallbackExpressionFactoryId(fallbackExpressionFactoryId);
    }

    /**
     * Determines whether the <code>Expression</code> handles a collection of sub-expressions that
     * are separated by commas.
     *
     * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF}
     * @param handleCollection <code>true</code> if the sub-expression to parse might have several
     * sub-expressions separated by commas; <code>false</code> otherwise
     */
    public void setHandleCollection(String queryBNFId, boolean handleCollection) {
        JPQLQueryBNF queryBNF = queryBNFs.get(queryBNFId);
        Assert.isNotNull(queryBNF, "The JPQLQueryBNF identified with '" + queryBNFId + "' does not exist.");
        queryBNF.setHandleCollection(handleCollection);
    }

    /**
     * Sets whether the BNF with the given ID supports nested array or not. A nested array is a sub-
     * expression with its child being a collection expression: (item_1, item_2, ..., item_n).
     *
     * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF}
     * @param handleNestedArray <code>true</code> if the expression represented by this BNF can be
     * a nested array; <code>false</code> otherwise
     * @since 2.5
     */
    public void setHandleNestedArray(String queryBNFId, boolean handleNestedArray) {
        JPQLQueryBNF queryBNF = queryBNFs.get(queryBNFId);
        Assert.isNotNull(queryBNF, "The JPQLQueryBNF identified with '" + queryBNFId + "' does not exist.");
        queryBNF.setHandleNestedArray(handleNestedArray);
    }

    /**
     * Sets whether the query BNF with the given ID handles parsing a sub-expression, i.e. parsing an
     * expression encapsulated by parenthesis. Which in fact would be handled by the fallback {@link
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
     * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF}
     * @param handleSubExpression <code>true</code> to let the creation of a sub-expression be
     * created by the fallback {@link ExpressionFactory} registered with this BNF; <code>false</code>
     * otherwise (which is the default value)
     */
    public void setHandleSubExpression(String queryBNFId, boolean handleSubExpression) {
        JPQLQueryBNF queryBNF = queryBNFs.get(queryBNFId);
        Assert.isNotNull(queryBNF, "The JPQLQueryBNF identified with '" + queryBNFId + "' does not exist.");
        queryBNF.setHandleSubExpression(handleSubExpression);
    }

    /**
     * Unregisters the given {@link JPQLQueryBNF}.
     *
     * @param queryBNF The {@link JPQLQueryBNF} to unregister
     * @exception NullPointerException The {@link JPQLQueryBNF} cannot be <code>null</code>
     */
    public void unregisterBNF(JPQLQueryBNF queryBNF) {

        Assert.isNotNull(queryBNF, "The JPQLQueryBNF cannot be null");

        queryBNFs.remove(queryBNF.getId());
        queryBNF.setExpressionRegistry(null);
    }
}
