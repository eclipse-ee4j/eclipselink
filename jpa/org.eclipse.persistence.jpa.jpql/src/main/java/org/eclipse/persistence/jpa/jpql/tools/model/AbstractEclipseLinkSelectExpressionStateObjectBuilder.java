/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model;

import org.eclipse.persistence.jpa.jpql.tools.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractEclipseLinkSelectExpressionStateObjectBuilder extends AbstractSelectExpressionStateObjectBuilder
                                                                            implements IEclipseLinkSelectExpressionStateObjectBuilder {

    /**
     * Creates a new <code>AbstractEclipseLinkSelectExpressionStateObjectBuilder</code>.
     *
     * @param parent The select clause for which this builder can create a select expression
     */
    protected AbstractEclipseLinkSelectExpressionStateObjectBuilder(SelectClauseStateObject parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEclipseLinkSelectExpressionStateObjectBuilder append() {
        return (IEclipseLinkSelectExpressionStateObjectBuilder) super.append();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEclipseLinkSelectExpressionStateObjectBuilder new_(String className,
                                                               ISelectExpressionStateObjectBuilder... parameters) {

        return (IEclipseLinkSelectExpressionStateObjectBuilder) super.new_(className, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEclipseLinkSelectExpressionStateObjectBuilder object(String identificationVariable) {
        return (IEclipseLinkSelectExpressionStateObjectBuilder) super.object(identificationVariable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEclipseLinkSelectExpressionStateObjectBuilder resultVariable(String resultVariable) {
        return (IEclipseLinkSelectExpressionStateObjectBuilder) super.resultVariable(resultVariable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEclipseLinkSelectExpressionStateObjectBuilder resultVariableAs(String resultVariable) {
        return (IEclipseLinkSelectExpressionStateObjectBuilder) super.resultVariableAs(resultVariable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEclipseLinkSelectExpressionStateObjectBuilder variable(String variable) {
        StateObject stateObject = buildIdentificationVariable(variable);
        add(stateObject);
        return this;
    }
}
