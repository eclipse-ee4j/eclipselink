/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model;

import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.tools.model.query.AbstractConditionalClauseStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.SimpleSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.UpdateItemStateObject;

/**
 * An implementation of {@link IJPQLQueryBuilder} that provides support based on the release
 * of the Java Persistence functional specification defined in <a href="http://jcp.org/en/jsr/detail?id=317">
 * JSR-337 - Java Persistence 2.0</a>. EclipseLink 2.1 provides additional support for 2 additional
 * JPQL identifiers: <code><b>FUNC</b></code> and <code><b>TREAT</b></code>.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLQueryBuilder extends AbstractJPQLQueryBuilder {

    /**
     * The {@link JPQLGrammar} that will be used to parse JPQL queries, which has EclipseLink support.
     */
    private final JPQLGrammar jpqlGrammar;

    /**
     * Creates a new <code>EclipseLinkJPQLQueryBuilder</code>.
     *
     * @param jpqlGrammar The {@link JPQLGrammar} that will be used to parse JPQL queries, which has
     * EclipseLink support
     */
    public EclipseLinkJPQLQueryBuilder(JPQLGrammar jpqlGrammar) {
        super();
        this.jpqlGrammar = jpqlGrammar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EclipseLinkStateObjectBuilder buildStateObjectBuilder() {
        return new EclipseLinkStateObjectBuilder();
    }

    /**
     * {@inheritDoc}
     */
    public IConditionalExpressionStateObjectBuilder buildStateObjectBuilder(AbstractConditionalClauseStateObject stateObject) {
        return new EclipseLinkConditionalStateObjectBuilder(stateObject);
    }

    /**
     * {@inheritDoc}
     */
    public EclipseLinkSelectExpressionStateObjectBuilder buildStateObjectBuilder(SelectClauseStateObject stateObject) {
        return new EclipseLinkSelectExpressionStateObjectBuilder(stateObject);
    }

    /**
     * {@inheritDoc}
     */
    public EclipseLinkSimpleSelectExpressionStateObjectBuilder buildStateObjectBuilder(SimpleSelectClauseStateObject stateObject) {
        return new EclipseLinkSimpleSelectExpressionStateObjectBuilder(stateObject);
    }

    /**
     * {@inheritDoc}
     */
    public INewValueStateObjectBuilder buildStateObjectBuilder(UpdateItemStateObject stateObject) {
        return new DefaultNewValueStateObjectBuilder(stateObject);
    }

    /**
     * {@inheritDoc}
     */
    public JPQLGrammar getGrammar() {
        return jpqlGrammar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "EclipseLinkJPQLQueryBuilder using " + jpqlGrammar.toString();
    }
}
