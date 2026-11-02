/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serial;
import java.time.temporal.Temporal;
import java.util.List;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.NumericExpression;
import jakarta.persistence.criteria.TemporalExpression;
import jakarta.persistence.criteria.TemporalField;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform.ExpressionTypeContext;

/** Temporal operations retaining the original expression's root and parameters. */
public class TemporalExpressionImpl<T extends Temporal & Comparable<? super T>>
        extends ComparableExpressionImpl<T> implements TemporalExpression<T> {

    @Serial
    private static final long serialVersionUID = 1L;

    TemporalExpressionImpl(ExpressionImpl<T> delegate) {
        super(delegate);
        if (delegate.isLiteral() && delegate.literal != null) {
            currentNode = CriteriaBuilderImpl.temporalValueNode(currentNode, getJavaType());
        }
    }

    private TemporalExpression<T> temporal(Expression<T> expression) {
        FunctionExpressionImpl<T> typed = new FunctionExpressionImpl<>(metamodel, expression.getJavaType(),
                CriteriaBuilderImpl.typedValueNode(((InternalSelection) expression).getCurrentNode(), expression.getJavaType(),
                        ExpressionTypeContext.TEMPORAL_RESULT),
                List.of(expression), "temporal");
        return new TemporalExpressionImpl<>(typed);
    }

    @Override
    public <N extends Number & Comparable<N>> NumericExpression<N> extract(TemporalField<N, T> field) {
        return new NumericExpressionImpl<>((ExpressionImpl<N>) builder().extract(field, this));
    }

    @Override
    public TemporalExpression<T> coalesce(Expression<? extends T> y) {
        return temporal(builder().coalesce(this, y));
    }

    @Override
    public TemporalExpression<T> coalesce(T y) {
        return temporal(builder().coalesce(this, value(y)));
    }

    @Override
    public TemporalExpression<T> nullif(Expression<? extends T> y) {
        return temporal(builder().nullif(this, y));
    }

    @Override
    public TemporalExpression<T> nullif(T y) {
        return temporal(builder().nullif(this, value(y)));
    }
}
