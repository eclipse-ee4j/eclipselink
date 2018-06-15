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
package org.eclipse.persistence.jpa.jpql.tools.model;

import org.eclipse.persistence.jpa.jpql.tools.model.query.AbstractSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;

/**
 * The abstract implementation of {@link ISimpleSelectExpressionStateObjectBuilder} that supports
 * the creation of the select expression based on the JPQL grammar defined in JPA 2.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractSimpleSelectExpressionStateObjectBuilder extends AbstractScalarExpressionStateObjectBuilder<ISimpleSelectExpressionStateObjectBuilder>
                                                                       implements ISimpleSelectExpressionStateObjectBuilder {

    /**
     * Creates a new <code>AbstractSimpleSelectExpressionStateObjectBuilder</code>.
     *
     * @param parent The select clause for which this builder can create a select expression
     */
    protected AbstractSimpleSelectExpressionStateObjectBuilder(AbstractSelectClauseStateObject parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractSelectClauseStateObject getParent() {
        return (AbstractSelectClauseStateObject) super.getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISimpleSelectExpressionStateObjectBuilder variable(String variable) {
        StateObject stateObject = buildIdentificationVariable(variable);
        add(stateObject);
        return this;
    }
}
