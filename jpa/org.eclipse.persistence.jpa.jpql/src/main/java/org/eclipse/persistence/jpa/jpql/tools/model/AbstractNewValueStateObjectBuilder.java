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

import org.eclipse.persistence.jpa.jpql.tools.model.query.KeywordExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.UpdateItemStateObject;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractNewValueStateObjectBuilder extends AbstractScalarExpressionStateObjectBuilder<INewValueStateObjectBuilder>
                                                         implements INewValueStateObjectBuilder {

    /**
     * Creates a new <code>AbstractNewValueStateObjectBuilder</code>.
     *
     * @param parent The parent of the expression to build, which is only required when a JPQL
     * fragment needs to be parsed
     */
    protected AbstractNewValueStateObjectBuilder(UpdateItemStateObject parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() {
        getParent().setNewValue(pop());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UpdateItemStateObject getParent() {
        return (UpdateItemStateObject) super.getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public INewValueStateObjectBuilder NULL() {
        StateObject stateObject = new KeywordExpressionStateObject(getParent(), NULL);
        add(stateObject);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public INewValueStateObjectBuilder variable(String variable) {
        StateObject stateObject = buildIdentificationVariable(variable);
        add(stateObject);
        return this;
    }
}
