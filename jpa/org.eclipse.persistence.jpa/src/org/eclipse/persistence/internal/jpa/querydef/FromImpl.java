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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.internal.expressions.ObjectExpression;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the From interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This class represents a from clause element which could
 * be the root of the query of the end node of a join statement.
 * <p>
 *
 * @see javax.persistence.criteria From
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */

public class FromImpl<Z, X>  extends PathImpl<X> implements javax.persistence.criteria.From<Z, X> {

    protected ManagedType managedType;
    protected Set<Join<X, ?>> joins;
    protected Set<Fetch<X, ?>> fetches;
    protected boolean isJoin = false;
    protected boolean isFetch = false;
    protected FromImpl correlatedParent;

    public <T> FromImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact) {
        super(parentPath, metamodel, javaClass, expressionNode, modelArtifact);
        this.managedType = managedType;
        this.joins = new HashSet<Join<X, ?>>();
        this.fetches = new HashSet<Fetch<X, ?>>();
    }

    public <T> FromImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact, FromImpl correlatedParent) {
        this(parentPath, managedType, metamodel, javaClass, expressionNode, modelArtifact);
        this.correlatedParent = correlatedParent;

    }
    /**
     * Return the fetch joins that have been made from this type.
     *
     * @return fetch joins made from this type
     */
    @Override
    public java.util.Set<Fetch<X, ?>> getFetches(){
        return this.fetches;
    }
    /**
     *  Whether the <code>From</code> object has been obtained as a result of
     *  correlation (use of a <code>Subquery</code> <code>correlate</code>
     *  method).
     *  @return boolean indicating whether the object has been
     *          obtained through correlation
     */
    @Override
    public boolean isCorrelated(){
        return this.correlatedParent != null;
    }

    /**
     *  Returns the parent <code>From</code> object from which the correlated
     *  <code>From</code> object has been obtained through correlation (use
     *  of a <code>Subquery</code> <code>correlate</code> method).
     *  @return  the parent of the correlated From object
     *  @throws IllegalStateException if the From object has
     *          not been obtained through correlation
     */
    @Override
    public From<Z, X> getCorrelationParent() {
        if (this.correlatedParent == null){
            throw new IllegalStateException(ExceptionLocalization.buildMessage("cannot_get_from_non_correlated_query"));
        }
        return this.correlatedParent;
    }


    /**
     * Fetch join to the specified attribute using an inner join.
     *
     * @param assoc
     *            target of the join
     * @return the resulting fetch join
     */
    @Override
    public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> assoc){
        return this.fetch(assoc, JoinType.INNER);
    }

    /**
     * Fetch join to the specified attribute using the given join type.
     *
     * @param assoc
     *            target of the join
     * @param jt
     *            join type
     * @return the resulting fetch join
     */
    @Override
    public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> assoc, JoinType jt){
        if (((SingularAttribute)assoc).getType().getPersistenceType().equals(PersistenceType.BASIC)){
            throw new IllegalStateException(ExceptionLocalization.buildMessage("CAN_NOT_JOIN_TO_BASIC"));
        }
        Class clazz = assoc.getBindableJavaType();
        Fetch<X, Y> join = null;
        ObjectExpression exp = ((ObjectExpression)this.currentNode).newDerivedExpressionNamed(assoc.getName());
        if (jt.equals(JoinType.LEFT)){
            exp.doUseOuterJoin();
        }else if(jt.equals(JoinType.RIGHT)){
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        }else{
            exp.doNotUseOuterJoin();
        }
        join = new JoinImpl<X, Y>(this, this.metamodel.managedType(clazz), this.metamodel, clazz, exp, assoc, jt);
        this.fetches.add(join);
        ((FromImpl)join).isFetch = true;
        return join;
    }
    /**
     * Fetch join to the specified collection using an inner join.
     *
     * @param assoc
     *            target of the join
     * @return the resulting join
     */
    @Override
    public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> assoc){
        return fetch(assoc, JoinType.INNER);
    }

    /**
     * Fetch join to the specified collection using the given join type.
     *
     * @param assoc
     *            target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    @Override
    public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> assoc, JoinType jt) {
        org.eclipse.persistence.expressions.Expression node;
        Fetch fetch;
        if (jt.equals(JoinType.LEFT)) {
            node = this.currentNode.anyOfAllowingNone(assoc.getName());
        } else if (jt.equals(JoinType.RIGHT)) {
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        } else {
            node = this.currentNode.anyOf(assoc.getName());
        }
        if (assoc.getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
            if (assoc.getCollectionType().equals(CollectionType.COLLECTION)) {
                fetch = new BasicCollectionJoinImpl<X, Y>(this, this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc, jt);
            } else if (assoc.getCollectionType().equals(CollectionType.LIST)) {
                fetch = new BasicListJoinImpl<X, Y>(this, this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc, jt);
            } else if (assoc.getCollectionType().equals(CollectionType.SET)) {
                fetch = new BasicSetJoinImpl<X, Y>(this, this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc, jt);
            } else {
                fetch = new BasicMapJoinImpl(this, this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, assoc, jt);
            }
        } else {
            if (assoc.getCollectionType().equals(CollectionType.COLLECTION)) {
                fetch = new CollectionJoinImpl<X, Y>(this, metamodel.managedType(((PluralAttribute) assoc).getBindableJavaType()), this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc, jt);
            } else if (assoc.getCollectionType().equals(CollectionType.LIST)) {
                fetch = new ListJoinImpl<X, Y>(this, metamodel.managedType(((PluralAttribute) assoc).getBindableJavaType()), this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc, jt);
            } else if (assoc.getCollectionType().equals(CollectionType.SET)) {
                fetch = new SetJoinImpl<X, Y>(this, metamodel.managedType(((PluralAttribute) assoc).getBindableJavaType()), this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc, jt);
            } else {
                fetch = new MapJoinImpl(this, metamodel.managedType(((PluralAttribute) assoc).getBindableJavaType()), this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, assoc, jt);
            }
        }
        this.fetches.add(fetch);
        ((FromImpl)fetch).isFetch = true;
        return fetch;
    }

    // String-based:
    /**
     * Fetch join to the specified attribute or association using an inner join.
     *
     * @param assocName
     *            name of the attribute or association for the target of the
     *            join
     * @return the resulting fetch join
     */
    @Override
    public <T, Y> Fetch<T, Y> fetch(String assocName){
        return fetch(assocName, JoinType.INNER);
    }

    /**
     * Fetch join to the specified attribute or association using the given join
     * type.
     *
     * @param assocName
     *            assocName of the attribute or association for the target of
     *            the join
     * @param jt
     *            join type
     * @return the resulting fetch join
     */
    @Override
    public <T, Y> Fetch<T, Y> fetch(String assocName, JoinType jt){
        Attribute attribute = this.managedType.getAttribute(assocName);
        if (attribute.isCollection()) {
            return fetch(((PluralAttribute)attribute), jt);
        }else{
            return fetch(((SingularAttribute)attribute), jt);
        }
    }


    @Override
    public Set<Join<X, ?>> getJoins() {
        return joins;
    }

    /**
     * Return the path corresponding to the referenced non-collection valued
     * attribute.
     *
     * @param att
     *            attribute
     * @return path corresponding to the referenced attribute
     */
    @Override
    public <Y> Path<Y> get(SingularAttribute<? super X, Y> att){
        if (att.getPersistentAttributeType().equals(PersistentAttributeType.BASIC)){
            return new PathImpl<Y>(this, this.metamodel, att.getBindableJavaType(),this.currentNode.get(att.getName()), att);
        }else{
            Class<Y> clazz = att.getBindableJavaType();
            Join join = new JoinImpl<X, Y>(this, this.metamodel.managedType(clazz), this.metamodel, clazz,this.currentNode.get(att.getName()), att);
            this.joins.add(join);
            return join;
        }
    }

    /**
     * Return the path corresponding to the referenced collection-valued
     * attribute.
     *
     * @param collection
     *            collection-valued attribute
     * @return expression corresponding to the referenced attribute
     */
    @Override
    public <E, C extends java.util.Collection<E>> Expression<C> get(PluralAttribute<X, C, E> collection){

        // This is a special Expression that represents just the collection for member of etc...
        return new ExpressionImpl<C>(this.metamodel, ClassConstants.Collection_Class ,this.currentNode.anyOf(collection.getName()));
    }

    /**
     * Return the path corresponding to the referenced map-valued attribute.
     *
     * @param map
     *            map-valued attribute
     * @return expression corresponding to the referenced attribute
     */
    @Override
    public <K, V, M extends java.util.Map<K, V>> Expression<M> get(MapAttribute<X, K, V> map){
        return new ExpressionImpl<M>(this.metamodel, ClassConstants.Map_Class ,this.currentNode.anyOf(map.getName()));
    }

    /**
     * Return an expression corresponding to the type of the path.
     *
     * @return expression corresponding to the type of the path
     */
    @Override
    public Expression<Class<? extends X>> type(){
        return new ExpressionImpl(this.metamodel, ClassConstants.CLASS,this.currentNode.type());
    }

    @Override
    public <Y> Path<Y> get(String attName) {
        Attribute attribute = this.managedType.getAttribute(attName);
        Join join;
        if (attribute.isCollection()) {
            if (!((PluralAttribute) attribute).getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
                if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.COLLECTION)) {
                    join = new CollectionJoinImpl<X, Y>(this, metamodel.managedType(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.LIST)) {
                    join = new ListJoinImpl<X, Y>(this, metamodel.managedType(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.SET)) {
                    join = new SetJoinImpl<X, Y>(this, metamodel.managedType(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
                } else {
                    join = new MapJoinImpl(this, metamodel.managedType(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
                }
            } else {
                if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.COLLECTION)) {
                    join = new BasicCollectionJoinImpl<X, Y>(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.LIST)) {
                    join = new BasicListJoinImpl<X, Y>(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.SET)) {
                    join = new BasicSetJoinImpl<X, Y>(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
                } else{
                    join = new BasicMapJoinImpl(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
                }
            }
        }else{
            Class clazz = ((SingularAttribute)attribute).getBindableJavaType();
            if (((SingularAttribute)attribute).getType().getPersistenceType().equals(PersistenceType.BASIC)){
                return new PathImpl<Y>(this, this.metamodel, clazz, this.currentNode.get(attribute.getName()), (Bindable)attribute);
            }else{
                join = new JoinImpl(this, this.metamodel.managedType(clazz), this.metamodel, clazz, this.currentNode.get(attribute.getName()), (Bindable)attribute);
            }
        }
        this.joins.add(join);
        return join;
    }

    @Override
    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute) {
        return this.join(attribute, JoinType.INNER);
    }

    @Override
    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute, JoinType jt) {
        if (((SingularAttribute)attribute).getType().getPersistenceType().equals(PersistenceType.BASIC)){
            throw new IllegalStateException(ExceptionLocalization.buildMessage("CAN_NOT_JOIN_TO_BASIC"));
        }
        Class clazz = attribute.getBindableJavaType();
        Join<X, Y> join = null;
        ObjectExpression exp = ((ObjectExpression)this.currentNode).newDerivedExpressionNamed(attribute.getName());
        if (jt.equals(JoinType.LEFT)){
            exp.doUseOuterJoin();
        }else if(jt.equals(JoinType.RIGHT)){
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        }else{
            exp.doNotUseOuterJoin();
        }
        join = new JoinImpl<X, Y>(this, this.metamodel.managedType(clazz), this.metamodel, clazz, exp, attribute, jt);
        this.joins.add(join);
        ((FromImpl)join).isJoin = true;
        return join;
    }

    @Override
    public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection) {
        return this.join(collection, JoinType.INNER);
    }

    @Override
    public <Y> SetJoin<X, Y> join(javax.persistence.metamodel.SetAttribute<? super X, Y> set) {
        return this.join(set, JoinType.INNER);
    }

    @Override
    public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list) {
        return this.join(list, JoinType.INNER);
    }

    @Override
    public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map) {
        return this.join(map, JoinType.INNER);
    }

    @Override
    public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection, JoinType jt) {
        org.eclipse.persistence.expressions.Expression node;
        Class clazz = collection.getBindableJavaType();
        CollectionJoin<X, Y> join = null;
        if (jt.equals(JoinType.INNER)) {
            node = this.currentNode.anyOf(collection.getName());
        } else if (jt.equals(JoinType.RIGHT)) {
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        } else {
            node = this.currentNode.anyOfAllowingNone(collection.getName());
        }
        if (collection.getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
            join = new BasicCollectionJoinImpl<X, Y>(this, this.metamodel, clazz, node, (Bindable) collection, jt);
        } else {
            join = new CollectionJoinImpl<X, Y>(this, metamodel.managedType(clazz), this.metamodel, clazz, node, (Bindable) collection, jt);
        }
        this.joins.add(join);
        ((FromImpl)join).isJoin = true;
        return join;
    }

    @Override
    public <Y> SetJoin<X, Y> join(javax.persistence.metamodel.SetAttribute<? super X, Y> set, JoinType jt) {
        org.eclipse.persistence.expressions.Expression node;
        Class clazz = set.getBindableJavaType();
        SetJoin<X, Y> join = null;
        if (jt.equals(JoinType.INNER)) {
            node = this.currentNode.anyOf(set.getName());
        } else if (jt.equals(JoinType.RIGHT)) {
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        } else {
            node = this.currentNode.anyOfAllowingNone(set.getName());
        }
        if (set.getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
            join = new BasicSetJoinImpl<X, Y>(this, this.metamodel, clazz, node, (Bindable) set, jt);
        } else {
            join = new SetJoinImpl<X, Y>(this, metamodel.managedType(clazz), this.metamodel, clazz, node, (Bindable) set, jt);
        }
        this.joins.add(join);
        ((FromImpl)join).isJoin = true;
        return join;
    }

    @Override
    public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list, JoinType jt) {
        org.eclipse.persistence.expressions.Expression node;
        Class clazz = list.getBindableJavaType();
        ListJoin<X, Y> join = null;
        if (jt.equals(JoinType.INNER)) {
            node = this.currentNode.anyOf(list.getName());
        } else if (jt.equals(JoinType.RIGHT)) {
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        } else {
            node = this.currentNode.anyOfAllowingNone(list.getName());
        }
        if (list.getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
            join = new BasicListJoinImpl<X, Y>(this, this.metamodel, clazz, node, (Bindable) list, jt);
        } else {
            join = new ListJoinImpl<X, Y>(this, metamodel.managedType(clazz), this.metamodel, clazz, node, (Bindable) list, jt);
        }
        this.joins.add(join);
        ((FromImpl)join).isJoin = true;
        return join;
    }

    @Override
    public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map, JoinType jt) {
        org.eclipse.persistence.expressions.Expression node;
        Class clazz = map.getBindableJavaType();
        MapJoin<X, K, V> join = null;
        if (jt.equals(JoinType.INNER)) {
            node = this.currentNode.anyOf(map.getName());
        } else if (jt.equals(JoinType.RIGHT)) {
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        } else {
            node = this.currentNode.anyOfAllowingNone(map.getName());
        }
        if (map.getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
            join = new BasicMapJoinImpl(this, this.metamodel, clazz, node, map, jt);
        } else {
            join = new MapJoinImpl(this, metamodel.managedType(clazz), this.metamodel, clazz, node, map, jt);
        }
        this.joins.add(join);
        ((FromImpl)join).isJoin = true;
        return join;
    }

    @Override
    public <T, Y> Join<T, Y> join(String attributeName) {
        return join(attributeName, JoinType.INNER);
    }

    @Override
    public <T, Y> Join<T, Y> join(String attributeName, JoinType jt) {
        Attribute attribute = this.managedType.getAttribute(attributeName);
        if (attribute.isCollection()) {
            org.eclipse.persistence.expressions.Expression node;
            if (jt.equals(JoinType.INNER)) {
                node = this.currentNode.anyOf(((PluralAttribute) attribute).getName());
            } else if (jt.equals(JoinType.RIGHT)) {
                throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
            } else {
                node = this.currentNode.anyOfAllowingNone(((PluralAttribute) attribute).getName());
            }
            Join join;
            if (((PluralAttribute) attribute).getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
                if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.COLLECTION)) {
                    join = new BasicCollectionJoinImpl(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute, jt);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.LIST)) {
                    join = new BasicListJoinImpl(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute, jt);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.SET)) {
                    join = new BasicSetJoinImpl(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute, jt);
                } else{
                    join = new BasicMapJoinImpl(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute, jt);
                }
            } else {
                if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.COLLECTION)) {
                    join = new CollectionJoinImpl(this, metamodel.managedType(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute, jt);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.LIST)) {
                    join = new ListJoinImpl(this, metamodel.managedType(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute, jt);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.SET)) {
                    join = new SetJoinImpl(this, metamodel.managedType(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute, jt);
                } else {
                    join = new MapJoinImpl(this, metamodel.managedType(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute, jt);
                }
            }
            this.joins.add(join);
            ((FromImpl)join).isJoin = true;
            return join;
        }else{
            return join(((SingularAttribute)attribute), jt);
        }
    }

    @Override
    public <T, Y> CollectionJoin<T, Y> joinCollection(String attributeName) {
        return joinCollection(attributeName, JoinType.INNER);
    }

    @Override
    public <T, Y> CollectionJoin<T, Y> joinCollection(String attributeName, JoinType jt) {
        try {
            return (CollectionJoin<T, Y>) join(attributeName, jt);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_attribute_not_collection", new Object[] { attributeName, this.managedType.getJavaType().getName() }), ex);
        }
    }

    @Override
    public <T, Y> ListJoin<T, Y> joinList(String attributeName) {
        return joinList(attributeName, JoinType.INNER);
    }

    @Override
    public <T, Y> ListJoin<T, Y> joinList(String attributeName, JoinType jt) {
        try {
            return (ListJoin<T, Y>) join(attributeName, jt);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_attribute_not_list", new Object[] { attributeName, this.managedType.getJavaType().getName() }), ex);
        }
    }

    @Override
    public <T, K, Y> MapJoin<T, K, Y> joinMap(String attributeName) {
        return joinMap(attributeName, JoinType.INNER);
    }

    @Override
    public <T, K, Y> MapJoin<T, K, Y> joinMap(String attributeName, JoinType jt) {
        try {
            return (MapJoin<T, K, Y>) join(attributeName, jt);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_attribute_not_map", new Object[] { attributeName, this.managedType.getJavaType().getName() }), ex);
        }
    }

    @Override
    public <T, Y> SetJoin<T, Y> joinSet(String attributeName) {
        return joinSet(attributeName, JoinType.INNER);
    }

    @Override
    public <T, Y> SetJoin<T, Y> joinSet(String attributeName, JoinType jt) {
        try {
            return (SetJoin<T, Y>) join(attributeName, jt);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_attribute_not_set", new Object[] { attributeName, this.managedType.getJavaType().getName() }), ex);
        }
    }

    public void findJoins(AbstractQueryImpl query){
        Stack stack = new Stack();
        stack.push(this);
        while(!stack.isEmpty()){
            FromImpl currentJoin = (FromImpl) stack.pop();
            stack.addAll(currentJoin.getJoins());
            if (currentJoin.isJoin) {
                query.addJoin(currentJoin);
            }
        }
    }

    public List<org.eclipse.persistence.expressions.Expression> findJoinFetches(){
        List<org.eclipse.persistence.expressions.Expression> fetches = new ArrayList<org.eclipse.persistence.expressions.Expression>();
        Stack stack = new Stack();
        stack.push(this);
        while(!stack.isEmpty()){
            FromImpl currentFetch = (FromImpl) stack.pop();
            stack.addAll(currentFetch.getFetches());
            if (currentFetch.isFetch) {
                fetches.add(currentFetch.getCurrentNode());
            }
        }
        return fetches;
    }

    @Override
    public boolean isFrom(){
        return true;
    }
}
