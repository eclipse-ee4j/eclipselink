/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * <p>This {@link JPQLGrammar} provides support for parsing JPQL queries defined in <a
 * href="http://jcp.org/en/jsr/detail?id=317">JSR-338 - Java Persistence 2.1</a> and the additional
 * support provided by EclipseLink 2.5.</p>
 *
 * The BNFs of the additional support are the following:
 * <pre><code> from_clause = FROM identification_variable_declaration {, {identification_variable_declaration | collection_member_declaration}}*
 *                    [hierarchical_query_clause]
 *                    [asof_clause]
 *
 * in_expression ::= { in_expression_expression | nested_array_expression } [NOT] IN { ( in_item {, in_item}* ) | (subquery) | ( nested_array_item {, nested_array_item}+ ) | collection_valued_input_parameter }
 *
 * in_expression_expression ::= { state_field_path_expression | type_discriminator |
 *                                single_valued_input_parameter | identification_variable |
 *                                scalar_expression }
 *
 * nested_array_expression ::= ( in_expression_expression {, in_expression_expression}+ )
 *
 * nested_array_item ::= ( in_item {, in_item}+ )
 *
 * hierarchical_query_clause ::= [start_with_clause] connectby_clause [order_siblings_by_clause]
 *
 * start_with_clause ::= START WITH conditional_expression
 *
 * connectby_clause ::= CONNECT BY { single_valued_object_path_expression | collection_valued_path_expression }
 *
 * order_siblings_by_clause ::= ORDER SIBLINGS BY orderby_item {, orderby_item}*
 *
 * asof_clause ::= AS OF { SCN | TIMESTAMP } scalar_expression</code></pre>
 *
 * <p>Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.</p>
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLGrammar2_5 extends AbstractJPQLGrammar {

    /**
     * The singleton instance of this {@link EclipseLinkJPQLGrammar2_5}.
     */
    private static final JPQLGrammar INSTANCE = new EclipseLinkJPQLGrammar2_5();

    /**
     * The EclipseLink version, which is 2.5.
     */
    public static final EclipseLinkVersion VERSION = EclipseLinkVersion.VERSION_2_5;

    /**
     * Creates a new <code>EclipseLinkJPQLGrammar2_5</code>.
     */
    public EclipseLinkJPQLGrammar2_5() {
        super();
    }

    /**
     * Creates a new <code>EclipseLinkJPQLGrammar2_5</code>.
     *
     * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
     * instantiating the base {@link JPQLGrammar}
     */
    public EclipseLinkJPQLGrammar2_5(AbstractJPQLGrammar jpqlGrammar) {
        super(jpqlGrammar);
    }

    /**
     * Extends the given {@link JPQLGrammar} with the information of this one without instantiating
     * the base {@link JPQLGrammar}.
     *
     * @param jpqlGrammar The {@link JPQLGrammar} to extend with the content of this one without
     * instantiating the base {@link JPQLGrammar}
     */
    public static void extend(AbstractJPQLGrammar jpqlGrammar) {
        new EclipseLinkJPQLGrammar2_5(jpqlGrammar);
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return The singleton instance of {@link EclipseLinkJPQLGrammar2_5}
     */
    public static JPQLGrammar instance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLGrammar buildBaseGrammar() {
        return new EclipseLinkJPQLGrammar2_4();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPAVersion getJPAVersion() {
        return JPAVersion.VERSION_2_1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProvider() {
        return DefaultEclipseLinkJPQLGrammar.PROVIDER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProviderVersion() {
        return VERSION.getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeBNFs() {

        registerBNF(new AsOfClauseBNF());
        registerBNF(new ConnectByClauseBNF());
        registerBNF(new HierarchicalQueryClauseBNF());
        registerBNF(new InternalConnectByClauseBNF());
        registerBNF(new OrderSiblingsByClauseBNF());
        registerBNF(new StartWithClauseBNF());

        // Add nested array support to the IN expression
        setHandleNestedArray(InExpressionExpressionBNF.ID, true);
        setHandleNestedArray(InExpressionItemBNF.ID,       true);

        // Extend x in 'x IN y'
        addChildBNF(InExpressionExpressionBNF.ID, ScalarExpressionBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeExpressionFactories() {

        registerFactory(new AsOfClauseFactory());
        registerFactory(new ConnectByClauseFactory());
        registerFactory(new HierarchicalQueryClauseFactory());
        registerFactory(new OrderSiblingsByClauseFactory());
        registerFactory(new StartWithClauseFactory());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeIdentifiers() {

        // Add support for HQL query, which allows the SELECT clause to be optional
        addIdentifier(SelectStatementFactory.ID, FROM);

        registerIdentifierRole(AS_OF,                IdentifierRole.CLAUSE);
        registerIdentifierRole(CONNECT_BY,           IdentifierRole.CLAUSE);
        registerIdentifierRole(SCN,                  IdentifierRole.COMPLEMENT);
        registerIdentifierRole(START_WITH,           IdentifierRole.CLAUSE);
        registerIdentifierRole(ORDER_SIBLINGS_BY,    IdentifierRole.CLAUSE);
        registerIdentifierRole(TIMESTAMP,            IdentifierRole.COMPLEMENT);

        registerIdentifierVersion(CONNECT_BY,        JPAVersion.VERSION_2_1);
        registerIdentifierVersion(ORDER_SIBLINGS_BY, JPAVersion.VERSION_2_1);
        registerIdentifierVersion(SCN,               JPAVersion.VERSION_2_1);
        registerIdentifierVersion(START_WITH,        JPAVersion.VERSION_2_1);

        registerIdentifierVersion("SIBLINGS",        JPAVersion.VERSION_2_1);
        registerIdentifierVersion("START",           JPAVersion.VERSION_2_1);
        registerIdentifierVersion("WITH",            JPAVersion.VERSION_2_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "EclipseLink 2.5";
    }
}
