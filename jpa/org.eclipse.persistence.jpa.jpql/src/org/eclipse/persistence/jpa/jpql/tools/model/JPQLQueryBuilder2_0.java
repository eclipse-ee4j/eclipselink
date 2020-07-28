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
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.tools.model.query.AbstractConditionalClauseStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.SimpleSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.UpdateItemStateObject;

/**
 * An implementation of {@link IJPQLQueryBuilder} that provides support based on the Java Persistence
 * functional specification defined in <a href="http://jcp.org/en/jsr/detail?id=317">JSR-337 - Java
 * Persistence 2.0</a>.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLQueryBuilder2_0 extends AbstractJPQLQueryBuilder {

    /**
     * Creates a new <code>JPQLQueryBuilder2_0</code>.
     */
    public JPQLQueryBuilder2_0() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DefaultStateObjectBuilder buildStateObjectBuilder() {
        return new DefaultStateObjectBuilder();
    }

    /**
     * {@inheritDoc}
     */
    public DefaultConditionalExpressionStateObjectBuilder buildStateObjectBuilder(AbstractConditionalClauseStateObject stateObject) {
        return new DefaultConditionalExpressionStateObjectBuilder(stateObject);
    }

    /**
     * {@inheritDoc}
     */
    public DefaultSelectExpressionStateObjectBuilder buildStateObjectBuilder(SelectClauseStateObject stateObject) {
        return new DefaultSelectExpressionStateObjectBuilder(stateObject);
    }

    /**
     * {@inheritDoc}
     */
    public DefaultSimpleSelectExpressionStateObjectBuilder buildStateObjectBuilder(SimpleSelectClauseStateObject stateObject) {
        return new DefaultSimpleSelectExpressionStateObjectBuilder(stateObject);
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
        return JPQLGrammar2_0.instance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "JPQLQueryBuilder2_0 using " + getGrammar().toString();
    }
}
