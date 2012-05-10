/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * <p>
 * <b>Purpose</b>: Represents a Join to a ElementCollection of basics.
 * <p>
 * <b>Description</b>: Represents a Join to a ElementCollection of basics.
 * Special type of Join that does not allow further joins.
 * <p>
 * 
 * @see javax.persistence.criteria MapJoin
 * 
 * @author gyorke
 * @since EclipseLink 1.2
 */
@SuppressWarnings("hiding")

public class BasicMapJoinImpl<Z, K, E> extends MapJoinImpl<Z, K, E> {
    public <T> BasicMapJoinImpl(Path<Z> parentPath, Metamodel metamodel, Class<E> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact){
        this(parentPath, metamodel, javaClass, expressionNode, modelArtifact,JoinType.INNER);
    }

    public <T> BasicMapJoinImpl(Path<Z> parentPath, Metamodel metamodel, Class<E> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact, JoinType joinType){
        super(parentPath, null, metamodel, javaClass, expressionNode, modelArtifact, joinType);
    }

    /**
     * Return the path corresponding to the referenced non-collection valued
     * attribute.
     * 
     * @param model
     *            attribute
     * @return path corresponding to the referenced attribute
     */
    public <Y> Path<Y> get(SingularAttribute<? super E, Y> att){
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    /**
     * Return the path corresponding to the referenced collection-valued
     * attribute.
     * 
     * @param model
     *            collection-valued attribute
     * @return expression corresponding to the referenced attribute
     */
    @Override
    public <Y, C extends java.util.Collection<Y>> Expression<C> get(PluralAttribute<E, C, Y> collection){
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    /**
     * Return the path corresponding to the referenced map-valued attribute.
     * 
     * @param model
     *            map-valued attribute
     * @return expression corresponding to the referenced attribute
     */
    @Override
    public <L, W, M extends java.util.Map<L, W>> Expression<M> get(MapAttribute<E, L, W> map){
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }
    
    /**
     * Return an expression corresponding to the type of the path.
     * 
     * @return expression corresponding to the type of the path
     */
    @Override
    public Expression<Class<? extends E>> type(){
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_type_does_not_apply"));
    }
    
    @Override
    public <Y> Path<Y> get(String attName) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public <Y> Join<E, Y> join(SingularAttribute<? super E, Y> attribute, JoinType jt) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public <Y> CollectionJoin<E, Y> join(CollectionAttribute<? super E, Y> collection, JoinType jt) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public <Y> SetJoin<E, Y> join(javax.persistence.metamodel.SetAttribute<? super E, Y> set, JoinType jt) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public <Y> ListJoin<E, Y> join(ListAttribute<? super E, Y> list, JoinType jt) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public <L, W> MapJoin<E, L, W> join(MapAttribute<? super E, L, W> map, JoinType jt) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public <E, Y> Join<E, Y> join(String attributeName, JoinType jt) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public <E, Y> CollectionJoin<E, Y> joinCollection(String attributeName, JoinType jt) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }
    
    @Override
    public <E, Y> ListJoin<E, Y> joinList(String attributeName, JoinType jt) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public <E, L, W> MapJoin<E, L, W> joinMap(String attributeName, JoinType jt) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public <E, Y> SetJoin<E, Y> joinSet(String attributeName, JoinType jt) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

}
