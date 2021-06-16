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
//     Gordon Yorke - Initial development
//
package org.eclipse.persistence.internal.jpa.querydef;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Root interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This class represents root of a path. All paths are
 * created from roots and they correspond to ExpressionBuilders.
 * <p>
 *
 * @see jakarta.persistence.criteria Path
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */
public class RootImpl<X> extends FromImpl<X, X> implements Root<X> {

    public <T> RootImpl(ManagedType managedType, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable modelArtifact) {
        super((Path) null, managedType, metamodel, javaClass, expressionNode, modelArtifact);
    }

    public <T> RootImpl(ManagedType managedType, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable modelArtifact, FromImpl correlatedParent) {
        super((Path) null, managedType, metamodel, javaClass, expressionNode, modelArtifact, correlatedParent);
    }

    /**
     * Return the metamodel entity corresponding to the root.
     *
     * @return metamodel entity corresponding to the root
     */
    @Override
    public EntityType<X> getModel() {
        return (EntityType<X>) this.modelArtifact;
    }

    @Override
    public void findRootAndParameters(CommonAbstractCriteriaImpl query) {
        query.integrateRoot(this);
    }

    @Override
    public boolean isRoot() {
        return true;
    }

}
