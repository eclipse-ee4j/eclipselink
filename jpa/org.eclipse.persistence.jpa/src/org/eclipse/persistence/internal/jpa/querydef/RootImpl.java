/*******************************************************************************
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Gordon Yorke - Initial development
 *
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.querydef;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Root interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This class represents root of a path. All paths are
 * created from roots and they correspond to ExpressionBuilders.
 * <p>
 *
 * @see javax.persistence.criteria Path
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
