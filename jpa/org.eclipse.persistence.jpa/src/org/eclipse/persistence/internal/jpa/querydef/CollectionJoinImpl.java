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
//     Gordon Yorke - Initial development
//

package org.eclipse.persistence.internal.jpa.querydef;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the CollectionJoin interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This class represents a join of an attribute through a collection .
 * <p>
 *
 * @see javax.persistence.criteria From
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */

public class CollectionJoinImpl<Z, X>  extends JoinImpl<Z, X> implements CollectionJoin<Z, X> {

    public <T> CollectionJoinImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact){
        this(parentPath, managedType, metamodel, javaClass, expressionNode, modelArtifact,JoinType.INNER);
    }

    public <T> CollectionJoinImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact, JoinType joinType){
        super(parentPath, managedType, metamodel, javaClass, expressionNode, modelArtifact, joinType);
    }

    public <T> CollectionJoinImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact, JoinType joinType, FromImpl correlatedParent){
        super(parentPath, managedType, metamodel, javaClass, expressionNode, modelArtifact, joinType, correlatedParent);
    }

    /**
    * Return the metamodel representation for the collection.
    * @return metamodel type representing the Collection that is
    * the target of the join
    */
    @Override
    public CollectionAttribute<? super Z, X> getModel(){
        return (CollectionAttribute<? super Z, X>)this.modelArtifact;
    }

    @Override
    public CollectionJoinImpl<Z, X> on(Expression<Boolean> restriction) {
        return (CollectionJoinImpl<Z, X>)super.on(restriction);
    }

    @Override
    public CollectionJoinImpl<Z, X> on(Predicate... restrictions) {
        return (CollectionJoinImpl<Z, X>)super.on(restrictions);
    }
}
