/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import java.util.HashSet;
import java.util.Set;

import javax.naming.OperationNotSupportedException;
import javax.persistence.FetchType;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable.BindableType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.Type.PersistenceType;

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
 * @since EclipseLink 2.0
 */

public class FromImpl<Z, X>  extends PathImpl<X> implements javax.persistence.criteria.From<Z, X> {

    protected ManagedType managedType;
    protected Set<Join<X, ?>> joins;
    protected Set<Fetch<X, ?>> fetches;

    public <T> FromImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact) {
        super(parentPath, metamodel, javaClass, expressionNode, modelArtifact);
        this.managedType = managedType;
        this.joins = new HashSet<Join<X, ?>>();
        this.fetches = new HashSet<Fetch<X, ?>>();
    }

    /**
     * Return the fetch joins that have been made from this type.
     * 
     * @return fetch joins made from this type
     */
    public java.util.Set<Fetch<X, ?>> getFetches(){
        return this.fetches;
    }

    /**
     * Fetch join to the specified attribute using an inner join.
     * 
     * @param assoc
     *            target of the join
     * @return the resulting fetch join
     */
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
    public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> assoc, JoinType jt){
        if (((SingularAttribute)assoc).getType().getPersistenceType().equals(PersistenceType.BASIC)){
            throw new IllegalStateException(ExceptionLocalization.buildMessage("CAN_NOT_JOIN_TO_BASIC"));
        }
        Class clazz = assoc.getBindableJavaType();
        Fetch<X, Y> join = null;
        if (jt.equals(JoinType.LEFT)){
            join = new JoinImpl<X, Y>(this, this.metamodel.type(clazz), this.metamodel, clazz, this.currentNode.getAllowingNull(assoc.getName()), assoc, jt);
        }else if(jt.equals(JoinType.RIGHT)){
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        }else{
            join = new JoinImpl<X, Y>(this, this.metamodel.type(clazz), this.metamodel, clazz, this.currentNode.get(assoc.getName()), assoc, jt);
        }
        this.fetches.add(join);
        return join;
    }
    /**
     * Fetch join to the specified collection using an inner join.
     * 
     * @param assoc
     *            target of the join
     * @return the resulting join
     */
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
                fetch = new BasicCollectionJoinImpl<X, Y>(this, this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc);
            } else if (assoc.getCollectionType().equals(CollectionType.LIST)) {
                fetch = new BasicListJoinImpl<X, Y>(this, this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc);
            } else if (assoc.getCollectionType().equals(CollectionType.SET)) {
                fetch = new BasicSetJoinImpl<X, Y>(this, this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc);
            } else {
                fetch = new BasicMapJoinImpl(this, this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc);
            }
        } else {
            if (assoc.getCollectionType().equals(CollectionType.COLLECTION)) {
                fetch = new CollectionJoinImpl<X, Y>(this, metamodel.type(((PluralAttribute) assoc).getBindableJavaType()), this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc);
            } else if (assoc.getCollectionType().equals(CollectionType.LIST)) {
                fetch = new ListJoinImpl<X, Y>(this, metamodel.type(((PluralAttribute) assoc).getBindableJavaType()), this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc);
            } else if (assoc.getCollectionType().equals(CollectionType.SET)) {
                fetch = new SetJoinImpl<X, Y>(this, metamodel.type(((PluralAttribute) assoc).getBindableJavaType()), this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc);
            } else {
                fetch = new MapJoinImpl(this, metamodel.type(((PluralAttribute) assoc).getBindableJavaType()), this.metamodel, ((PluralAttribute) assoc).getBindableJavaType(), node, (Bindable) assoc);
            }
        }
        this.fetches.add(fetch);
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
    public <Y> Fetch<X, Y> fetch(String assocName){
        return fetch(assocName, JoinType.INNER);
    }

    /**
     * Fetch join to the specified attribute or association using the given join
     * type.
     * 
     * @param name
     *            assocName of the attribute or association for the target of
     *            the join
     * @param jt
     *            join type
     * @return the resulting fetch join
     */
    public <Y> Fetch<X, Y> fetch(String assocName, JoinType jt){
        Attribute attribute = this.managedType.getAttribute(assocName);
        if (attribute.isCollection()) {
            return fetch(((PluralAttribute)attribute), jt);
        }else{
            return fetch(((SingularAttribute)attribute), jt);
        }
    }
    
    
    public Set<Join<X, ?>> getJoins() {
        return joins;
    }

    /**
     * Return the path corresponding to the referenced non-collection valued
     * attribute.
     * 
     * @param model
     *            attribute
     * @return path corresponding to the referenced attribute
     */
    public <Y> Path<Y> get(SingularAttribute<? super X, Y> att){
        if (att.getPersistentAttributeType().equals(PersistentAttributeType.BASIC)){
            return new PathImpl<Y>(this, this.metamodel, att.getBindableJavaType(),this.currentNode.get(att.getName()), att);
        }else{
            Class<Y> clazz = att.getBindableJavaType();
            Join join = new JoinImpl<X, Y>(this, this.metamodel.type(clazz), this.metamodel, clazz,this.currentNode.get(att.getName()), att);
            this.joins.add(join);
            return join;
        }
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
    public <E, C extends java.util.Collection<E>> Expression<C> get(PluralAttribute<X, C, E> collection){
        
        // This is a special Expression that represents just the collection for member of etc...
        
        return new ExpressionImpl<C>(this.metamodel, ClassConstants.Collection_Class ,this.currentNode.anyOf(collection.getName()));
    }

    /**
     * Return the path corresponding to the referenced map-valued attribute.
     * 
     * @param model
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
                    join = new CollectionJoinImpl<X, Y>(this, metamodel.type(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.LIST)) {
                    join = new ListJoinImpl<X, Y>(this, metamodel.type(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.SET)) {
                    join = new SetJoinImpl<X, Y>(this, metamodel.type(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
                } else {
                    join = new MapJoinImpl(this, metamodel.type(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), this.currentNode.anyOf(attribute.getName()), (Bindable) attribute);
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
                join = new JoinImpl(this, this.metamodel.type(clazz), this.metamodel, clazz, this.currentNode.get(attribute.getName()), (Bindable)attribute);
            }
        }
        this.joins.add(join);
        return join;
    }

    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute) {
        return this.join(attribute, JoinType.INNER);
    }

    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute, JoinType jt) {
        if (((SingularAttribute)attribute).getType().getPersistenceType().equals(PersistenceType.BASIC)){
            throw new IllegalStateException(ExceptionLocalization.buildMessage("CAN_NOT_JOIN_TO_BASIC"));
        }
        Class clazz = attribute.getBindableJavaType();
        Join<X, Y> join = null;
        if (jt.equals(JoinType.LEFT)){
            join = new JoinImpl<X, Y>(this, this.metamodel.type(clazz), this.metamodel, clazz, this.currentNode.getAllowingNull(attribute.getName()), attribute, jt);
        }else if(jt.equals(JoinType.RIGHT)){
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        }else{
            join = new JoinImpl<X, Y>(this, this.metamodel.type(clazz), this.metamodel, clazz, this.currentNode.get(attribute.getName()), attribute, jt);
        }
        this.joins.add(join);
        return join;
    }

    public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection) {
        return this.join(collection, JoinType.INNER);
    }

    public <Y> SetJoin<X, Y> join(javax.persistence.metamodel.SetAttribute<? super X, Y> set) {
        return this.join(set, JoinType.INNER);
    }

    public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list) {
        return this.join(list, JoinType.INNER);
    }

    public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map) {
        return this.join(map, JoinType.INNER);
    }

    public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection, JoinType jt) {
        org.eclipse.persistence.expressions.Expression node;
        Class clazz = collection.getBindableJavaType();
        CollectionJoin<X, Y> join = null;
        if (jt.equals(JoinType.LEFT)) {
            node = this.currentNode.anyOf(collection.getName());
        } else if (jt.equals(JoinType.RIGHT)) {
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        } else {
            node = this.currentNode.anyOfAllowingNone(collection.getName());
        }
        if (collection.getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
            join = new BasicCollectionJoinImpl<X, Y>(this, this.metamodel, clazz, node, (Bindable) collection);
        } else {
            join = new CollectionJoinImpl<X, Y>(this, metamodel.type(clazz), this.metamodel, clazz, node, (Bindable) collection);
        }
        this.joins.add(join);
        return join;
    }

    public <Y> SetJoin<X, Y> join(javax.persistence.metamodel.SetAttribute<? super X, Y> set, JoinType jt) {
        org.eclipse.persistence.expressions.Expression node;
        Class clazz = set.getBindableJavaType();
        SetJoin<X, Y> join = null;
        if (jt.equals(JoinType.LEFT)) {
            node = this.currentNode.anyOf(set.getName());
        } else if (jt.equals(JoinType.RIGHT)) {
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        } else {
            node = this.currentNode.anyOfAllowingNone(set.getName());
        }
        if (set.getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
            join = new BasicSetJoinImpl<X, Y>(this, this.metamodel, clazz, node, (Bindable) set);
        } else {
            join = new SetJoinImpl<X, Y>(this, metamodel.type(clazz), this.metamodel, clazz, node, (Bindable) set);
        }
        this.joins.add(join);
        return join;
    }

    public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list, JoinType jt) {
        org.eclipse.persistence.expressions.Expression node;
        Class clazz = list.getBindableJavaType();
        ListJoin<X, Y> join = null;
        if (jt.equals(JoinType.LEFT)) {
            node = this.currentNode.anyOf(list.getName());
        } else if (jt.equals(JoinType.RIGHT)) {
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        } else {
            node = this.currentNode.anyOfAllowingNone(list.getName());
        }
        if (list.getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
            join = new BasicListJoinImpl<X, Y>(this, this.metamodel, clazz, node, (Bindable) list);
        } else {
            join = new ListJoinImpl<X, Y>(this, metamodel.type(clazz), this.metamodel, clazz, node, (Bindable) list);
        }
        this.joins.add(join);
        return join;
    }

    public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map, JoinType jt) {
        org.eclipse.persistence.expressions.Expression node;
        Class clazz = map.getBindableJavaType();
        MapJoin<X, K, V> join = null;
        if (jt.equals(JoinType.LEFT)) {
            node = this.currentNode.anyOf(map.getName());
        } else if (jt.equals(JoinType.RIGHT)) {
            throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
        } else {
            node = this.currentNode.anyOfAllowingNone(map.getName());
        }
        if (map.getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
            join = new BasicMapJoinImpl(this, this.metamodel, clazz, node, (Bindable) map);
        } else {
            join = new MapJoinImpl(this, metamodel.type(clazz), this.metamodel, clazz, node, (Bindable) map);
        }
        this.joins.add(join);
        return join;
    }

    public <X, Y> Join<X, Y> join(String attributeName) {
        return join(attributeName, JoinType.INNER);
    }

    public <X, Y> Join<X, Y> join(String attributeName, JoinType jt) {
        Attribute attribute = this.managedType.getAttribute(attributeName);
        if (attribute.isCollection()) {
            org.eclipse.persistence.expressions.Expression node;
            if (jt.equals(JoinType.LEFT)) {
                node = this.currentNode.anyOf(((PluralAttribute) attribute).getName());
            } else if (jt.equals(JoinType.RIGHT)) {
                throw new UnsupportedOperationException(ExceptionLocalization.buildMessage("RIGHT_JOIN_NOT_SUPPORTED"));
            } else {
                node = this.currentNode.anyOfAllowingNone(((PluralAttribute) attribute).getName());
            }
            Join join;
            if (((PluralAttribute) attribute).getElementType().getPersistenceType().equals(PersistenceType.BASIC)) {
                if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.COLLECTION)) {
                    join = new BasicCollectionJoinImpl(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.LIST)) {
                    join = new BasicListJoinImpl(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.SET)) {
                    join = new BasicSetJoinImpl(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute);
                } else{
                    join = new BasicMapJoinImpl(this, this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute);
                }
            } else {
                if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.COLLECTION)) {
                    join = new CollectionJoinImpl(this, metamodel.type(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.LIST)) {
                    join = new ListJoinImpl(this, metamodel.type(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute);
                } else if (((PluralAttribute) attribute).getCollectionType().equals(CollectionType.SET)) {
                    join = new SetJoinImpl(this, metamodel.type(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute);
                } else {
                    join = new MapJoinImpl(this, metamodel.type(((PluralAttribute) attribute).getBindableJavaType()), this.metamodel, ((PluralAttribute) attribute).getBindableJavaType(), node, (Bindable) attribute);
                }
            }
            this.joins.add(join);
            return join;
        }else{
            return join(((SingularAttribute)attribute), jt);
        }
    }

    public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName) {
        return joinCollection(attributeName, JoinType.INNER);
    }

    public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName, JoinType jt) {
        try {
            return (CollectionJoin<X, Y>) join(attributeName, jt);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_attribute_not_collection", new Object[] { attributeName, this.managedType.getJavaType().getName() }), ex);
        }
    }

    public <X, Y> ListJoin<X, Y> joinList(String attributeName) {
        return joinList(attributeName, JoinType.INNER);
    }

    public <X, Y> ListJoin<X, Y> joinList(String attributeName, JoinType jt) {
        try {
            return (ListJoin<X, Y>) join(attributeName, jt);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_attribute_not_list", new Object[] { attributeName, this.managedType.getJavaType().getName() }), ex);
        }
    }

    public <X, K, Y> MapJoin<X, K, Y> joinMap(String attributeName) {
        return joinMap(attributeName, JoinType.INNER);
    }

    public <X, K, Y> MapJoin<X, K, Y> joinMap(String attributeName, JoinType jt) {
        try {
            return (MapJoin<X, K, Y>) join(attributeName, jt);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_attribute_not_map", new Object[] { attributeName, this.managedType.getJavaType().getName() }), ex);
        }
    }

    public <X, Y> SetJoin<X, Y> joinSet(String attributeName) {
        return joinSet(attributeName, JoinType.INNER);
    }

    public <X, Y> SetJoin<X, Y> joinSet(String attributeName, JoinType jt) {
        try {
            return (SetJoin<X, Y>) join(attributeName, jt);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("metamodel_attribute_not_set", new Object[] { attributeName, this.managedType.getJavaType().getName() }), ex);
        }
    }
}
