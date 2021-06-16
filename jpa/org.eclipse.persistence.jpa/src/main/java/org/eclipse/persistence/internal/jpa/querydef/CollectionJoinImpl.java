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

import jakarta.persistence.criteria.CollectionJoin;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the CollectionJoin interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This class represents a join of an attribute through a collection .
 * <p>
 *
 * @see jakarta.persistence.criteria From
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
