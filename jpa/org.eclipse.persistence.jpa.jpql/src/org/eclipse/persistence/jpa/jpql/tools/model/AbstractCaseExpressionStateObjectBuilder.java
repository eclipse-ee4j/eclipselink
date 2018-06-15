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

import org.eclipse.persistence.jpa.jpql.tools.model.query.CaseExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.WhenClauseStateObject;

/**
 * This abstract implementation of {@link ICaseExpressionStateObjectBuilder} adds support for
 * creating a <code><b>CASE</b></code> expression.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractCaseExpressionStateObjectBuilder extends AbstractConditionalExpressionStateObjectBuilder<ICaseExpressionStateObjectBuilder>
                                                               implements ICaseExpressionStateObjectBuilder {

    /**
     * Keeps track of the actual {@link CaseExpressionStateObject} since it is needed when creating
     * new {@link WhenClauseStateObject} due to strongly typed parent.
     */
    private CaseExpressionStateObject caseExpressionStateObject;

    /**
     * Creates a new <code>AbstractCaseExpressionStateObjectBuilder</code>.
     *
     * @param parent The parent of the <code><b>CASE</b></code> expression to build, which is only
     * required when a JPQL fragment needs to be parsed
     */
    protected AbstractCaseExpressionStateObjectBuilder(StateObject parent) {
        super(parent);
        caseExpressionStateObject = new CaseExpressionStateObject(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaseExpressionStateObject buildStateObject() {
        caseExpressionStateObject.setElse(pop());
        if (hasStateObjects()) {
            caseExpressionStateObject.setCaseOperand(pop());
        }
        return caseExpressionStateObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICaseExpressionStateObjectBuilder when(ICaseExpressionStateObjectBuilder when,
                                                  ICaseExpressionStateObjectBuilder then) {

        checkBuilders(when, then);

        StateObject thenStateObject = pop();
        StateObject whenStateObject = pop();

        WhenClauseStateObject stateObject = new WhenClauseStateObject(
            caseExpressionStateObject,
            whenStateObject,
            thenStateObject
        );

        caseExpressionStateObject.addItem(stateObject);
        return this;
    }
}
