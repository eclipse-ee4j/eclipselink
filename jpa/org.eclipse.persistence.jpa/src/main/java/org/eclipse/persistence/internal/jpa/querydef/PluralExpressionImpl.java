package org.eclipse.persistence.internal.jpa.querydef;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.NumericExpression;
import jakarta.persistence.criteria.PluralExpression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.Metamodel;

import java.io.Serial;

public class PluralExpressionImpl<C, E> extends ExpressionImpl<C> implements PluralExpression<C, E> {

    @Serial
    private static final long serialVersionUID = 1L;

    public PluralExpressionImpl(Metamodel metamodel, Class<? extends C> javaType, org.eclipse.persistence.expressions.Expression expressionNode) {
        super(metamodel, javaType, expressionNode);
    }

    @Override
    public Predicate isEmpty() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate isNotEmpty() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public NumericExpression<Integer> size() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate contains(Expression<? extends E> elem) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate contains(E elem) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate notContains(Expression<? extends E> elem) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate notContains(E elem) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}