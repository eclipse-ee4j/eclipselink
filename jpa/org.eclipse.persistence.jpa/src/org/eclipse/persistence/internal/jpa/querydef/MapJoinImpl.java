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
//     Gordon Yorke - Initial development
//

package org.eclipse.persistence.internal.jpa.querydef;

import java.util.Map;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Join interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This class represents a join of an attribute from a "From"element.
 * <p>
 *
 * @see javax.persistence.criteria MapJoin
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */

public class MapJoinImpl<Z, K, V>  extends JoinImpl<Z, V> implements MapJoin<Z, K, V> {

    public <T> MapJoinImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<V> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact){
        this(parentPath, managedType, metamodel, javaClass, expressionNode, modelArtifact,JoinType.INNER);
    }

    public <T> MapJoinImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<V> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact, JoinType joinType){
        super(parentPath, managedType, metamodel, javaClass, expressionNode, modelArtifact, joinType);
    }

    public <T> MapJoinImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<V> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact, JoinType joinType, FromImpl correlatedParent){
        super(parentPath, managedType, metamodel, javaClass, expressionNode, modelArtifact, joinType, correlatedParent);
    }

    /**
    * Return the metamodel representation for the collection.
    * @return metamodel type representing the Collection that is
    * the target of the join
    */
    public javax.persistence.metamodel.MapAttribute<? super Z, K, V> getModel(){
        return (javax.persistence.metamodel.MapAttribute<? super Z, K, V>)this.modelArtifact;
    }

    public Expression<Map.Entry<K, V>> entry() {
        return new ExpressionImpl(this.metamodel, ClassConstants.Map_Entry_Class, this.currentNode.mapEntry());
    }

    public Join<Map<K, V>, K> joinKey() {
        return this.joinKey(JoinType.INNER);
    }

    public Join<Map<K, V>, K> joinKey(JoinType jt) {
        if (this.getModel().getKeyType().getPersistenceType().equals(PersistenceType.BASIC)){
            throw new IllegalStateException(ExceptionLocalization.buildMessage("attemped_to_join_basic_key"));
        }
        return new JoinImpl(this, (ManagedType)this.getModel().getKeyType(), metamodel, this.getModel().getKeyJavaType(), this.currentNode.mapKey(), this.getModel());
    }

    public Path<K> key() {
        if (this.getModel().getKeyType().getPersistenceType().equals(PersistenceType.BASIC)){
            return new PathImpl(this, metamodel, ((MapAttribute)this.modelArtifact).getKeyJavaType(), this.currentNode.mapKey(), this.getModel());
        }
        return new JoinImpl(this, (ManagedType)this.getModel().getKeyType(), metamodel, this.getModel().getKeyJavaType(), this.currentNode.mapKey(), this.getModel());
    }

    public Path<V> value() {
        return this;
    }

    public MapJoinImpl<Z, K, V> on(Expression<Boolean> restriction) {
        return (MapJoinImpl<Z, K, V>)super.on(restriction);
    }

    public MapJoinImpl<Z, K, V> on(Predicate... restrictions) {
        return (MapJoinImpl<Z, K, V>)super.on(restrictions);
    }
}
