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

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Path interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This class represents an abstract path which is a model of the expression through joins.
 * <p>
 *
 * @see javax.persistence.criteria Path
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */

public class PathImpl<X> extends ExpressionImpl<X> implements Path<X>, Cloneable{
    protected Path<?> pathParent;

    // Although this is an Object type only Attributes that implement Bindable are passed to this class
    // sublcasses like JoinImpl will cast this artifact to Attribute
    protected Object modelArtifact;

    public PathImpl(Path<?> parent, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable modelArtifact) {
        super(metamodel, javaClass, expressionNode);
        this.pathParent = parent;
        this.modelArtifact = modelArtifact;
    }

    /**
     * Return the bindable object that corresponds to the path expression.
     *
     * @return bindable object corresponding to the path
     */
    @Override
    public  Bindable<X> getModel(){
        return (Bindable<X>) this.modelArtifact;

    }

    /**
     * Return the parent "node" in the path.
     *
     * @return parent
     */
    @Override
    public Path<?> getParentPath(){
        return this.pathParent;
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
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
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
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
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
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    /**
     * Return an expression corresponding to the type of the path.
     *
     * @return expression corresponding to the type of the path
     */
    @Override
    public Expression<Class<? extends X>> type(){
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_type_does_not_apply_to_primitive_node"));
    }

    @Override
    public <Y> Path<Y> get(String attName) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public void findRootAndParameters(CommonAbstractCriteriaImpl query){
        ((PathImpl)this.pathParent).findRootAndParameters(query);
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException excecption) {
            return null;
        }
    }
}
