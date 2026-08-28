/*
 * Copyright (c) 2011, 2023 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.BooleanExpression;
import jakarta.persistence.criteria.ComparableExpression;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.NumericExpression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.PluralExpression;
import jakarta.persistence.criteria.TemporalExpression;
import jakarta.persistence.criteria.TextExpression;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.BooleanAttribute;
import jakarta.persistence.metamodel.ComparableAttribute;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.NumericAttribute;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.TemporalAttribute;
import jakarta.persistence.metamodel.TextAttribute;

import java.io.Serial;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Map;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Path interface of the JPA criteria API.
 * <p>
 * <b>Description</b>: This class represents an abstract path which is a model of the expression through joins.
 *
 * @see jakarta.persistence.criteria Path
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */
public class PathImpl<X> extends ExpressionImpl<X> implements Path<X>, Cloneable {

    @Serial
    private static final long serialVersionUID = 1L;

    protected Path<?> pathParent;

    // Although this is an Object type only Attributes that implement Bindable are passed to this class
    // subclasses like JoinImpl will cast this artifact to Attribute
    protected Object modelArtifact;

    public PathImpl(Path<?> parent, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<?> modelArtifact) {
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
    public Bindable<X> getModel() {
        return (Bindable<X>) modelArtifact;

    }

    /**
     * Return the parent "node" in the path.
     *
     * @return parent
     */
    @Override
    public Path<?> getParentPath() {
        return pathParent;
    }

    /**
     * Return the path corresponding to the referenced non-collection valued attribute.
     *
     * @param att attribute
     * @return path corresponding to the referenced attribute
     */
    @Override
    public <Y> Path<Y> get(SingularAttribute<? super X, Y> att) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public <E, C extends Collection<E>> PluralExpression<C,E> get(@Nonnull PluralAttribute<? super X, C, E> collection) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public <K, V, M extends Map<K, V>> PluralExpression<M,V> get(@Nonnull MapAttribute<? super X, K, V> map) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    /**
     * Return an expression corresponding to the type of the path.
     *
     * @return expression corresponding to the type of the path
     */
    @Override
    public Expression<Class<? extends X>> type() {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_type_does_not_apply_to_primitive_node"));
    }

    @Override
    public <Y> Path<Y> get(String attName) {
        throw new IllegalStateException(ExceptionLocalization.buildMessage("pathnode_is_primitive_node"));
    }

    @Override
    public void findRootAndParameters(CommonAbstractCriteriaImpl<?> query) {
        ((PathImpl) pathParent).findRootAndParameters(query);
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException excecption) {
            return null;
        }
    }

    @Override
    public BooleanExpression get(BooleanAttribute<? super X> attribute) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public <C extends Comparable<? super C>> ComparableExpression<C> get(ComparableAttribute<? super X, C> attribute) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public <T extends Temporal & Comparable<? super T>> TemporalExpression<T> get(TemporalAttribute<? super X, T> attribute) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public <N extends Number & Comparable<N>> NumericExpression<N> get(NumericAttribute<? super X, N> attribute) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public TextExpression get(TextAttribute<? super X> attribute) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public <T extends X> Path<T> treat(Class<T> type) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
