package org.eclipse.persistence.internal.jpa.querydef;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.QueryBuilder;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.querydef.AbstractQueryImpl.ResultType;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

public class QueryBuilderImpl implements QueryBuilder {
    
    protected Metamodel metamodel;
    
    public QueryBuilderImpl(Metamodel metamodel){
        this.metamodel = metamodel;
    }

    /**
     *  Create a Criteria query object.
     *  @return query object
     */
    public CriteriaQuery<Object> createQuery(){
        return new CriteriaQueryImpl(this.metamodel, ResultType.OTHER, ClassConstants.OBJECT, this);
    }

    /**
     *  Create a Criteria query object.
     *  @return query object
     */
    public <T> CriteriaQuery<T> createQuery(Class<T> resultClass){
        if (resultClass.equals(Tuple.class)){
            return new CriteriaQueryImpl(this.metamodel, ResultType.TUPLE, resultClass, this);
        }else if(resultClass.equals(ClassConstants.AOBJECT)){
            return new CriteriaQueryImpl<T>(this.metamodel, ResultType.OBJECT_ARRAY, resultClass, this);
        }else if (resultClass.isArray()){
            return new CriteriaQueryImpl<T>(this.metamodel, ResultType.OBJECT_ARRAY, resultClass, this);
        }else{
            ManagedType type = this.metamodel.type(resultClass);
            if (type != null && type.getPersistenceType().equals(PersistenceType.ENTITY)){
                return new CriteriaQueryImpl(this.metamodel, ResultType.ENTITY, resultClass , this);
            } else {
                return new CriteriaQueryImpl(this.metamodel, ResultType.OTHER, resultClass, this);
            }
        }
    }

    /**
     *  Create a Criteria query object that returns a tuple of 
     *  objects as its result.
     *  @return query object
     */
    public CriteriaQuery<Tuple> createTupleQuery(){
        return new CriteriaQueryImpl(this.metamodel, ResultType.TUPLE, Tuple.class, this);
    }

    /**
     * Define a select list item corresponding to a constructor.
     * 
     * @param result
     *            class whose instance is to be constructed
     * @param selections
     *            arguments to the constructor
     * @return selection item
     */
    public <Y> Selection<Y> construct(Class<Y> result, Selection<?>... selections){
        return new CompoundSelectionImpl(result, selections);
    }
    

    public Selection<Tuple> tuple(Selection<?>... selections){
        return construct(Tuple.class, selections);
    }

    /**
     * Create an ordering by the ascending value of the expression.
     * 
     * @param x
     *            expression used to define the ordering
     * @return ascending ordering corresponding to the expression
     */
    public Order asc(Expression<?> x){
        if (((ExpressionImpl)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new OrderImpl(x);
    }

    /**
     * Create an ordering by the descending value of the expression.
     * 
     * @param x
     *            expression used to define the ordering
     * @return descending ordering corresponding to the expression
     */
    public Order desc(Expression<?> x){
        if (((ExpressionImpl)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        OrderImpl order = new OrderImpl(x, false);
        return order;
    }

    // aggregate functions:
    /**
     * Create an expression applying the avg operation.
     * 
     * @param x
     *            expression representing input value to avg operation
     * @return avg expression
     */
    public <N extends Number> Expression<Double> avg(Expression<N> x){
        return new ExpressionImpl(this.metamodel, ClassConstants.DOUBLE,((ExpressionImpl)x).getCurrentNode().average());
    }

    /**
     * Create an expression applying the sum operation.
     * 
     * @param x
     *            expression representing input value to sum operation
     * @return sum expression
     */
    public <N extends Number> Expression<N> sum(Expression<N> x){
        return new ExpressionImpl(this.metamodel, ClassConstants.DOUBLE,((ExpressionImpl)x).getCurrentNode().sum());
    }

    /**
     * Create an expression applying the numerical max operation.
     * 
     * @param x
     *            expression representing input value to max operation
     * @return max expression
     */
    public <N extends Number> Expression<N> max(Expression<N> x){
        return new ExpressionImpl(this.metamodel, ClassConstants.DOUBLE,((ExpressionImpl)x).getCurrentNode().maximum());
    }

    /**
     * Create an expression applying the numerical min operation.
     * 
     * @param x
     *            expression representing input value to min operation
     * @return min expression
     */
    public <N extends Number> Expression<N> min(Expression<N> x){
        return new ExpressionImpl(this.metamodel, ClassConstants.DOUBLE,((ExpressionImpl)x).getCurrentNode().minimum());
    }

    /**
     * Create an aggregate expression for finding the greatest of the values
     * (strings, dates, etc).
     * 
     * @param x
     *            expression representing input value to greatest operation
     * @return greatest expression
     */
    public <X extends Comparable<X>> Expression<X> greatest(Expression<X> x){
        if (((ExpressionImpl)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new ExpressionImpl(this.metamodel, ClassConstants.DOUBLE,((ExpressionImpl)x).getCurrentNode().maximum());
    }

    /**
     * Create an aggregate expression for finding the least of the values
     * (strings, dates, etc).
     * 
     * @param x
     *            expression representing input value to least operation
     * @return least expression
     */
    public <X extends Comparable<X>> Expression<X> least(Expression<X> x){
        if (((ExpressionImpl)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new ExpressionImpl(this.metamodel, ClassConstants.DOUBLE,((ExpressionImpl)x).getCurrentNode().minimum());
    }

    /**
     * Create an expression applying the count operation.
     * 
     * @param x
     *            expression representing input value to count operation
     * @return count expression
     */
    public Expression<Long> count(Expression<?> x){
        if (((ExpressionImpl)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new ExpressionImpl(this.metamodel, ClassConstants.DOUBLE,((ExpressionImpl)x).getCurrentNode().count());
    }

    /**
     * Create an expression applying the count distinct operation.
     * 
     * @param x
     *            expression representing input value to count distinct
     *            operation
     * @return count distinct expression
     */
    public Expression<Long> countDistinct(Expression<?> x){
        if (((ExpressionImpl)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new ExpressionImpl(this.metamodel, ClassConstants.DOUBLE,((ExpressionImpl)x).getCurrentNode().distinct().count());
    }

    // subqueries:
    /**
     * Create a predicate testing the existence of a subquery result.
     * 
     * @param subquery
     *            subquery whose result is to be tested
     * @return exists predicate
     */
    public Predicate exists(Subquery<?> subquery){
        //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Create a predicate corresponding to an all expression over the subquery
     * results.
     * 
     * @param subquery
     * @return all expression
     */
    public <Y> Expression<Y> all(Subquery<Y> subquery){
        //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Create a predicate corresponding to a some expression over the subquery
     * results. This is equivalent to an any expression.
     * 
     * @param subquery
     * @return all expression
     */
    public <Y> Expression<Y> some(Subquery<Y> subquery){
        //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Create a predicate corresponding to an any expression over the subquery
     * results. This is equivalent to a some expression.
     * 
     * @param subquery
     * @return any expression
     */
    public <Y> Expression<Y> any(Subquery<Y> subquery){
        //TODO
        throw new UnsupportedOperationException();
    }

    // boolean functions:
    /**
     * Create a conjunction of the given boolean expressions.
     * 
     * @param x
     *            boolean expression
     * @param y
     *            boolean expression
     * @return and predicate
     */
    public Predicate and(Expression<Boolean> x, Expression<Boolean> y){
        CompoundExpressionImpl xp = null;
        CompoundExpressionImpl yp = null;
        
        
        //TODO determine if this is needed
        if (((ExpressionImpl)x).isExpression()){
            xp = (CompoundExpressionImpl)this.isTrue(x);
        }else{
            xp = (CompoundExpressionImpl)x;
        }
        if (((ExpressionImpl)y).isExpression()){
            yp = (CompoundExpressionImpl)this.isTrue(y);
        }else{
            yp = (CompoundExpressionImpl)y;
        }
        
        
        if (yp.isPredicate() && yp.expressions.isEmpty()){
            if (yp.isNegated()){
                return yp;
            }else{
                return xp;
            }
        }
        if (xp.isPredicate() && xp.expressions.isEmpty()){
            if (xp.isNegated()){
                return xp;
            }else{
                return yp;
            }
        }
        return new PredicateImpl(this.metamodel, xp.getCurrentNode().and(yp.getCurrentNode()), buildList(xp,yp), BooleanOperator.AND);
    }

    /**
     * Create a disjunction of the given boolean expressions.
     * 
     * @param x
     *            boolean expression
     * @param y
     *            boolean expression
     * @return or predicate
     */
    public Predicate or(Expression<Boolean> x, Expression<Boolean> y){
        CompoundExpressionImpl xp = null;
        CompoundExpressionImpl yp = null;
        
        if (((ExpressionImpl)x).isExpression()){
            xp = (CompoundExpressionImpl)this.isTrue(x);
        }else{
            xp = (CompoundExpressionImpl)x;
        }
        if (((ExpressionImpl)y).isExpression()){
            yp = (CompoundExpressionImpl)this.isTrue(y);
        }else{
            yp = (CompoundExpressionImpl)y;
        }
        if (yp.isPredicate() && yp.expressions.isEmpty()){
            if (yp.isNegated()){
                return xp;
            }
        }
        if (xp.isPredicate() && xp.expressions.isEmpty()){
            if (xp.isNegated()){
                return yp;
            }
        }
        return new PredicateImpl(this.metamodel, xp.getCurrentNode().or(yp.getCurrentNode()), buildList(xp,yp), BooleanOperator.OR);
    }

    /**
     * Create a conjunction of the given restriction predicates. A conjunction
     * of zero predicates is true.
     * 
     * @param restriction
     *            zero or more restriction predicates
     * @return and predicate
     */
    public Predicate and(Predicate... restrictions){
        int max = restrictions.length;
        if (max == 0){
            return this.conjunction();
        }
        Predicate a = restrictions[0];
        for (int i = 1; i < max; ++i){
            a = this.and(a, restrictions[i]);
        }
        return a;
    }

    /**
     * Create a disjunction of the given restriction predicates. A disjunction
     * of zero predicates is false.
     * 
     * @param restriction
     *            zero or more restriction predicates
     * @return and predicate
     */
    public Predicate or(Predicate... restrictions){
        int max = restrictions.length;
        if (max == 0){
            return this.disjunction();
        }
        Predicate a = restrictions[0];
        for (int i = 1; i < max; ++i){
            a = this.or(a, restrictions[i]);
        }
        return a;
    }

    /**
     * Create a negation of the given restriction.
     * 
     * @param restriction
     *            restriction expression
     * @return not predicate
     */
    public Predicate not(Expression<Boolean> restriction){
        if (((ExpressionImpl)restriction).isPredicate()){
            return ((PredicateImpl)restriction).negate();
        }
        return new PredicateImpl(this.metamodel, ((ExpressionImpl)restriction).currentNode.not(), buildList(restriction), BooleanOperator.NOT);
    }

    /**
     * Create a conjunction (with zero conjuncts). A conjunction with zero
     * conjuncts is true.
     * 
     * @return and predicate
     */
    public Predicate conjunction(){
        return new PredicateImpl(this.metamodel, null, null, BooleanOperator.AND);
    }

    /**
     * Create a disjunction (with zero disjuncts). A disjunction with zero
     * disjuncts is false.
     * 
     * @return or predicate
     */
    public Predicate disjunction(){
        return new PredicateImpl(this.metamodel, null, null, BooleanOperator.OR);
    }

    // turn Expression<Boolean> into a Predicate
    // useful for use with varargs methods
    /**
     * Create a predicate testing for a true value.
     * 
     * @param x
     *            expression to be tested if true
     * @return predicate
     */
    public Predicate isTrue(Expression<Boolean> x){
        if (((ExpressionImpl)x).isPredicate()){
            if (((ExpressionImpl)x).getCurrentNode() == null){
                return (Predicate)x;
            }else{
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("PREDICATE_PASSED_TO_EVALUATION"));
            }
        }
        List list = new ArrayList();
        list.add(x);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().equal(true), list, "equals");
    }

    /**
     * Create a predicate testing for a false value.
     * 
     * @param x
     *            expression to be tested if false
     * @return predicate
     */
    public Predicate isFalse(Expression<Boolean> x){
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().equal(false), buildList(x), "equals");
    }
    
    //null tests:
    /**
     * Create a predicate to test whether the expression is null.
     * @param x expression
     * @return predicate
     */
    public Predicate isNull(Expression<?> x){
        return new PredicateImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().isNull(), new ArrayList(), BooleanOperator.AND);
    }
    
    /**
     * Create a predicate to test whether the expression is not null.
     * @param x expression
     * @return predicate
     */
    public Predicate isNotNull(Expression<?> x){
        return new PredicateImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().notNull(),new ArrayList(), BooleanOperator.AND);
    }

    // equality:
    /**
     * Create a predicate for testing the arguments for equality.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return equality predicate
     */
    public Predicate equal(Expression<?> x, Expression<?> y){
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().equal(((ExpressionImpl)y).getCurrentNode()), list, "equals");
    }

    /**
     * Create a predicate for testing the arguments for inequality.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return inequality predicate
     */
    public Predicate notEqual(Expression<?> x, Expression<?> y){
        if (((ExpressionImpl)x).getCurrentNode() == null || ((ExpressionImpl)y).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().notEqual(((ExpressionImpl)y).getCurrentNode()), list, "not equal");
    }

    /**
     * Create a predicate for testing the arguments for equality.
     * 
     * @param x
     *            expression
     * @param y
     *            object
     * @return equality predicate
     */
    public Predicate equal(Expression<?> x, Object y){
        if (((ExpressionImpl)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().equal(y), list, "equal");
    }

    /**
     * Create a predicate for testing the arguments for inequality.
     * 
     * @param x
     *            expression
     * @param y
     *            object
     * @return inequality predicate
     */
    public Predicate notEqual(Expression<?> x, Object y){
        if (((ExpressionImpl)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().notEqual(y), list, "not equal");
    }

    // comparisons for generic (non-numeric) operands:
    /**
     * Create a predicate for testing whether the first argument is greater than
     * the second.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return greater-than predicate
     */
    public <Y extends Comparable<Y>> Predicate greaterThan(Expression<? extends Y> x, Expression<? extends Y> y){
        if (((ExpressionImpl)x).getCurrentNode() == null || ((ExpressionImpl)y).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().greaterThan(((ExpressionImpl)y).getCurrentNode()), list, "greaterThan");
    }

    /**
     * Create a predicate for testing whether the first argument is less than
     * the second.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return less-than predicate
     */
    public <Y extends Comparable<Y>> Predicate lessThan(Expression<? extends Y> x, Expression<? extends Y> y){
        if (((ExpressionImpl)x).getCurrentNode() == null || ((ExpressionImpl)y).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().lessThan(((ExpressionImpl)y).getCurrentNode()), list, "lessThan");
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * or equal to the second.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return greater-than-or-equal predicate
     */
    public <Y extends Comparable<Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is less than or
     * equal to the second.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return less-than-or-equal predicate
     */
    public <Y extends Comparable<Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is between the
     * second and third arguments in value.
     * 
     * @param v
     *            expression
     * @param x
     *            expression
     * @param y
     *            expression
     * @return between predicate
     */
    public <Y extends Comparable<Y>> Predicate between(Expression<? extends Y> v, Expression<? extends Y> x, Expression<? extends Y> y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * the second.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return greater-than predicate
     */
    public <Y extends Comparable<Y>> Predicate greaterThan(Expression<? extends Y> x, Y y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is less than
     * the second.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return less-than predicate
     */
    public <Y extends Comparable<Y>> Predicate lessThan(Expression<? extends Y> x, Y y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * or equal to the second.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return greater-than-or-equal predicate
     */
    public <Y extends Comparable<Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> x, Y y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is less than or
     * equal to the second.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return less-than-or-equal predicate
     */
    public <Y extends Comparable<Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> x, Y y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is between the
     * second and third arguments in value.
     * 
     * @param v
     *            expression
     * @param x
     *            value
     * @param y
     *            value
     * @return between predicate
     */
    public <Y extends Comparable<Y>> Predicate between(Expression<? extends Y> v, Y x, Y y){
        //TODO
        return null;
    }
    
    protected List<Expression<?>> buildList(Expression<?>... expressions){
        ArrayList list = new ArrayList();
        for(Expression<?> exp : expressions){
            list.add(exp);
        }
        return list;
    }

    // comparisons for numeric operands:
    /**
     * Create a predicate for testing whether the first argument is greater than
     * the second.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return greater-than predicate
     */
    public Predicate gt(Expression<? extends Number> x, Expression<? extends Number> y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is less than
     * the second.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return less-than predicate
     */
    public Predicate lt(Expression<? extends Number> x, Expression<? extends Number> y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * or equal to the second.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return greater-than-or-equal predicate
     */
    public Predicate ge(Expression<? extends Number> x, Expression<? extends Number> y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is less than or
     * equal to the second.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return less-than-or-equal predicate
     */
    public Predicate le(Expression<? extends Number> x, Expression<? extends Number> y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * the second.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return greater-than predicate
     */
    public Predicate gt(Expression<? extends Number> x, Number y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is less than
     * the second.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return less-than predicate
     */
    public Predicate lt(Expression<? extends Number> x, Number y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * or equal to the second.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return greater-than-or-equal predicate
     */
    public Predicate ge(Expression<? extends Number> x, Number y){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the first argument is less than or
     * equal to the second.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return less-than-or-equal predicate
     */
    public Predicate le(Expression<? extends Number> x, Number y){
        //TODO
        return null;
    }

    // numerical operations:
    /**
     * Create an expression that returns the arithmetic negation of its
     * argument.
     * 
     * @param x
     *            expression
     * @return negated expression
     */
    public <N extends Number> Expression<N> neg(Expression<N> x){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the absolute value of its argument.
     * 
     * @param x
     *            expression
     * @return absolute value
     */
    public <N extends Number> Expression<N> abs(Expression<N> x){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the sum of its arguments.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return sum
     */
    public <N extends Number> Expression<N> sum(Expression<? extends N> x, Expression<? extends N> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the product of its arguments.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return product
     */
    public <N extends Number> Expression<N> prod(Expression<? extends N> x, Expression<? extends N> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the difference between its arguments.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return difference
     */
    public <N extends Number> Expression<N> diff(Expression<? extends N> x, Expression<? extends N> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the sum of its arguments.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return sum
     */
    public <N extends Number> Expression<N> sum(Expression<? extends N> x, N y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the product of its arguments.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return product
     */
    public <N extends Number> Expression<N> prod(Expression<? extends N> x, N y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the difference between its arguments.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return difference
     */
    public <N extends Number> Expression<N> diff(Expression<? extends N> x, N y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the sum of its arguments.
     * 
     * @param x
     *            value
     * @param y
     *            expression
     * @return sum
     */
    public <N extends Number> Expression<N> sum(N x, Expression<? extends N> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the product of its arguments.
     * 
     * @param x
     *            value
     * @param y
     *            expression
     * @return product
     */
    public <N extends Number> Expression<N> prod(N x, Expression<? extends N> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the difference between its arguments.
     * 
     * @param x
     *            value
     * @param y
     *            expression
     * @return difference
     */
    public <N extends Number> Expression<N> diff(N x, Expression<? extends N> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the quotient of its arguments.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return quotient
     */
    public Expression<Number> quot(Expression<? extends Number> x, Expression<? extends Number> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the quotient of its arguments.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return quotient
     */
    public Expression<Number> quot(Expression<? extends Number> x, Number y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the quotient of its arguments.
     * 
     * @param x
     *            value
     * @param y
     *            expression
     * @return quotient
     */
    public Expression<Number> quot(Number x, Expression<? extends Number> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the modulus of its arguments.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return modulus
     */
    public Expression<Integer> mod(Expression<Integer> x, Expression<Integer> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the modulus of its arguments.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return modulus
     */
    public Expression<Integer> mod(Expression<Integer> x, Integer y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the modulus of its arguments.
     * 
     * @param x
     *            value
     * @param y
     *            expression
     * @return modulus
     */
    public Expression<Integer> mod(Integer x, Expression<Integer> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the square root of its argument.
     * 
     * @param x
     *            expression
     * @return modulus
     */
    public Expression<Double> sqrt(Expression<? extends Number> x){
        //TODO
        return null;
    }

    // typecasts:
    /**
     * Typecast.
     * 
     * @param number
     *            numeric expression
     * @return Expression<Long>
     */
    public Expression<Long> toLong(Expression<? extends Number> number){
        //TODO
        return null;
    }

    /**
     * Typecast.
     * 
     * @param number
     *            numeric expression
     * @return Expression<Integer>
     */
    public Expression<Integer> toInteger(Expression<? extends Number> number){
        //TODO
        return null;
    }

    /**
     * Typecast.
     * 
     * @param number
     *            numeric expression
     * @return Expression<Float>
     */
    public Expression<Float> toFloat(Expression<? extends Number> number){
        //TODO
        return null;
    }

    /**
     * Typecast.
     * 
     * @param number
     *            numeric expression
     * @return Expression<Double>
     */
    public Expression<Double> toDouble(Expression<? extends Number> number){
        //TODO
        return null;
    }

    /**
     * Typecast.
     * 
     * @param number
     *            numeric expression
     * @return Expression<BigDecimal>
     */
    public Expression<BigDecimal> toBigDecimal(Expression<? extends Number> number){
        //TODO
        return null;
    }

    /**
     * Typecast.
     * 
     * @param number
     *            numeric expression
     * @return Expression<BigInteger>
     */
    public Expression<BigInteger> toBigInteger(Expression<? extends Number> number){
        //TODO
        return null;
    }

    /**
     * Typecast.
     * 
     * @param character
     *            expression
     * @return Expression<String>
     */
    public Expression<String> toString(Expression<Character> character){
        //TODO
        return null;
    }

    // literals:
    /**
     * Create an expression literal.
     * 
     * @param value
     * @return expression literal
     */
    public <T> Expression<T> literal(T value){
        return new ExpressionImpl<T>(metamodel, (Class<T>) value.getClass(), new ConstantExpression(value, new ExpressionBuilder()), value);
    }

    // parameters:
    /**
     * Create a parameter.
     * 
     * Create a parameter expression.
     * @param paramClass parameter class
     * @return parameter expression
     */
    public <T> ParameterExpression<T> parameter(Class<T> paramClass){
        //TODO
        return null;
    }

    /**
     * Create a parameter expression with the given name.
     * 
     * @param paramClass
     *            parameter class
     * @param name
     * @return parameter
     */
    public <T> ParameterExpression<T> parameter(Class<T> paramClass, String name){
        return new ParameterExpressionImpl<T>(metamodel, paramClass, name);
    }

    // collection operations:
    /**
     * Create a predicate that tests whether a collection is empty.
     * 
     * @param collection
     *            expression
     * @return predicate
     */
    public <C extends Collection<?>> Predicate isEmpty(Expression<C> collection){
        //TODO
        return null;
    }

    /**
     * Create a predicate that tests whether a collection is not empty.
     * 
     * @param collection
     *            expression
     * @return predicate
     */
    public <C extends Collection<?>> Predicate isNotEmpty(Expression<C> collection){
        //TODO
        return null;
    }

    /**
     * Create an expression that tests the size of a collection.
     * 
     * @param collection
     * @return size expression
     */
    public <C extends Collection<?>> Expression<Integer> size(C collection){
        //TODO
        return null;
    }

    /**
     * Create an expression that tests the size of a collection.
     * 
     * @param collection
     *            expression
     * @return size expression
     */
    public <C extends java.util.Collection<?>> Expression<Integer> size(Expression<C> collection){
        //TODO
        return null;
    }

    /**
     * Create a predicate that tests whether an element is a member of a
     * collection.
     * 
     * @param elem
     *            element
     * @param collection
     *            expression
     * @return predicate
     */
    public <E, C extends Collection<E>> Predicate isMember(E elem, Expression<C> collection){
        //TODO
        return null;
    }

    /**
     * Create a predicate that tests whether an element is not a member of a
     * collection.
     * 
     * @param elem
     *            element
     * @param collection
     *            expression
     * @return predicate
     */
    public <E, C extends Collection<E>> Predicate isNotMember(E elem, Expression<C> collection){
        //TODO
        return null;
    }

    /**
     * Create a predicate that tests whether an element is a member of a
     * collection.
     * 
     * @param elem
     *            element expression
     * @param collection
     *            expression
     * @return predicate
     */
    public <E, C extends Collection<E>> Predicate isMember(Expression<E> elem, Expression<C> collection){
        //TODO
        return null;
    }

    /**
     * Create a predicate that tests whether an element is not a member of a
     * collection.
     * 
     * @param elem
     *            element expression
     * @param collection
     *            expression
     * @return predicate
     */
    public <E, C extends Collection<E>> Predicate isNotMember(Expression<E> elem, Expression<C> collection){
        //TODO
        return null;
    }

    // get the values and keys collections of the Map, which may then
    // be passed to size(), isMember(), isEmpty(), etc
    /**
     * Create an expression that returns the values of a map.
     * 
     * @param map
     * @return collection expression
     */
    public <V, M extends Map<?, V>> Expression<Collection<V>> values(M map){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns the keys of a map.
     * 
     * @param map
     * @return set expression
     */
    public <K, M extends Map<K, ?>> Expression<Set<K>> keys(M map){
        //TODO
        return null;
    }

    // string functions:
    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @return like predicate
     */
    public Predicate like(Expression<String> x, Expression<String> pattern){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @param escapeChar
     *            escape character expression
     * @return like predicate
     */
    public Predicate like(Expression<String> x, Expression<String> pattern, Expression<Character> escapeChar){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @param escapeChar
     *            escape character
     * @return like predicate
     */
    public Predicate like(Expression<String> x, Expression<String> pattern, char escapeChar){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @return like predicate
     */
    public Predicate like(Expression<String> x, String pattern){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @param escapeChar
     *            escape character expression
     * @return like predicate
     */
    public Predicate like(Expression<String> x, String pattern, Expression<Character> escapeChar){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @param escapeChar
     *            escape character
     * @return like predicate
     */
    public Predicate like(Expression<String> x, String pattern, char escapeChar){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @return like predicate
     */
    public Predicate notLike(Expression<String> x, Expression<String> pattern){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @param escapeChar
     *            escape character expression
     * @return like predicate
     */
    public Predicate notLike(Expression<String> x, Expression<String> pattern, Expression<Character> escapeChar){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @param escapeChar
     *            escape character
     * @return like predicate
     */
    public Predicate notLike(Expression<String> x, Expression<String> pattern, char escapeChar){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @return like predicate
     */
    public Predicate notLike(Expression<String> x, String pattern){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @param escapeChar
     *            escape character expression
     * @return like predicate
     */
    public Predicate notLike(Expression<String> x, String pattern, Expression<Character> escapeChar){
        //TODO
        return null;
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     * 
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @param escapeChar
     *            escape character
     * @return like predicate
     */
    public Predicate notLike(Expression<String> x, String pattern, char escapeChar){
        //TODO
        return null;
    }

    /**
     * String concatenation operation.
     * 
     * @param x
     *            string expression
     * @param y
     *            string expression
     * @return expression corresponding to concatenation
     */
    public Expression<String> concat(Expression<String> x, Expression<String> y){
        //TODO
        return null;
    }

    /**
     * String concatenation operation.
     * 
     * @param x
     *            string expression
     * @param y
     *            string
     * @return expression corresponding to concatenation
     */
    public Expression<String> concat(Expression<String> x, String y){
        //TODO
        return null;
    }

    /**
     * String concatenation operation.
     * 
     * @param x
     *            string
     * @param y
     *            string expression
     * @return expression corresponding to concatenation
     */
    public Expression<String> concat(String x, Expression<String> y){
        //TODO
        return null;
    }

    /**
     * Substring extraction operation. Extracts a substring starting at
     * specified position through to end of the string. First position is 1.
     * 
     * @param x
     *            string expression
     * @param from
     *            start position expression
     * @return expression corresponding to substring extraction
     */
    public Expression<String> substring(Expression<String> x, Expression<Integer> from){
        //TODO
        return null;
    }

    /**
     * Substring extraction operation. Extracts a substring starting at
     * specified position through to end of the string. First position is 1.
     * 
     * @param x
     *            string expression
     * @param from
     *            start position
     * @return expression corresponding to substring extraction
     */
    public Expression<String> substring(Expression<String> x, int from){
        //TODO
        return null;
    }

    /**
     * Substring extraction operation. Extracts a substring of given length
     * starting at specified position. First position is 1.
     * 
     * @param x
     *            string expression
     * @param from
     *            start position expression
     * @param len
     *            length expression
     * @return expression corresponding to substring extraction
     */
    public Expression<String> substring(Expression<String> x, Expression<Integer> from, Expression<Integer> len){
        //TODO
        return null;
    }

    /**
     * Substring extraction operation. Extracts a substring of given length
     * starting at specified position. First position is 1.
     * 
     * @param x
     *            string expression
     * @param from
     *            start position
     * @param len
     *            length
     * @return expression corresponding to substring extraction
     */
    public Expression<String> substring(Expression<String> x, int from, int len){
        //TODO
        return null;
    }

    /**
     * Create expression to trim blanks from both ends of a string.
     * 
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    public Expression<String> trim(Expression<String> x){
        //TODO
        return null;
    }

    /**
     * Create expression to trim blanks from a string.
     * 
     * @param ts
     *            trim specification
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    public Expression<String> trim(Trimspec ts, Expression<String> x){
        //TODO
        return null;
    }

    /**
     * Create expression to trim character from both ends of a string.
     * 
     * @param t
     *            expression for character to be trimmed
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    public Expression<String> trim(Expression<Character> t, Expression<String> x){
        //TODO
        return null;
    }

    /**
     * Create expression to trim character from a string.
     * 
     * @param ts
     *            trim specification
     * @param t
     *            expression for character to be trimmed
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    public Expression<String> trim(Trimspec ts, Expression<Character> t, Expression<String> x){
        //TODO
        return null;
    }

    /**
     * Create expression to trim character from both ends of a string.
     * 
     * @param t
     *            character to be trimmed
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    public Expression<String> trim(char t, Expression<String> x){
        //TODO
        return null;
    }

    /**
     * Create expression to trim character from a string.
     * 
     * @param ts
     *            trim specification
     * @param t
     *            character to be trimmed
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    public Expression<String> trim(Trimspec ts, char t, Expression<String> x){
        //TODO
        return null;
    }

    /**
     * Create expression for converting a string to lowercase.
     * 
     * @param x
     *            string expression
     * @return expression to convert to lowercase
     */
    public Expression<String> lower(Expression<String> x){
        //TODO
        return null;
    }

    /**
     * Create expression for converting a string to uppercase.
     * 
     * @param x
     *            string expression
     * @return expression to convert to uppercase
     */
    public Expression<String> upper(Expression<String> x){
        //TODO
        return null;
    }

    /**
     * Create expression to return length of a string.
     * 
     * @param x
     *            string expression
     * @return length expression
     */
    public Expression<Integer> length(Expression<String> x){
        //TODO
        return null;
    }

    /**
     * Create expression to locate the position of one string within another,
     * returning position of first character if found. The first position in a
     * string is denoted by 1. If the string to be located is not found, 0 is
     * returned.
     * 
     * @param x
     *            expression for string to be searched
     * @param pattern
     *            expression for string to be located
     * @return expression corresponding to position
     */
    public Expression<Integer> locate(Expression<String> x, Expression<String> pattern){
        //TODO
        return null;
    }

    /**
     * Create expression to locate the position of one string within another,
     * returning position of first character if found. The first position in a
     * string is denoted by 1. If the string to be located is not found, 0 is
     * returned.
     * 
     * @param x
     *            expression for string to be searched
     * @param pattern
     *            expression for string to be located
     * @param from
     *            expression for position at which to start search
     * @return expression corresponding to position
     */
    public Expression<Integer> locate(Expression<String> x, Expression<String> pattern, Expression<Integer> from){
        //TODO
        return null;
    }

    /**
     * Create expression to locate the position of one string within another,
     * returning position of first character if found. The first position in a
     * string is denoted by 1. If the string to be located is not found, 0 is
     * returned.
     * 
     * @param x
     *            expression for string to be searched
     * @param pattern
     *            string to be located
     * @return expression corresponding to position
     */
    public Expression<Integer> locate(Expression<String> x, String pattern){
        //TODO
        return null;
    }

    /**
     * Create expression to locate the position of one string within another,
     * returning position of first character if found. The first position in a
     * string is denoted by 1. If the string to be located is not found, 0 is
     * returned.
     * 
     * @param x
     *            expression for string to be searched
     * @param pattern
     *            string to be located
     * @param from
     *            position at which to start search
     * @return expression corresponding to position
     */
    public Expression<Integer> locate(Expression<String> x, String pattern, int from){
        //TODO
        return null;
    }

    // Date/time/timestamp functions:
    /**
     * Create expression to return current date.
     * 
     * @return expression for current date
     */
    public Expression<java.sql.Date> currentDate(){
        //TODO
        return null;
    }

    /**
     * Create expression to return current timestamp.
     * 
     * @return expression for current timestamp
     */
    public Expression<java.sql.Timestamp> currentTimestamp(){
        //TODO
        return null;
    }

    /**
     * Create expression to return current time.
     * 
     * @return expression for current time
     */
    public Expression<java.sql.Time> currentTime(){
        //TODO
        return null;
    }

    /**
     * Create predicate to test whether given expression is contained in a list
     * of values.
     * 
     * @param expression
     *            to be tested against list of values
     * @return in predicate
     */
    public <T> In<T> in(Expression<? extends T> expression){
        //TODO
        return null;
    }

    // coalesce, nullif:
    /**
     * Create an expression that returns null if all its arguments evaluate to
     * null, and the value of the first non-null argument otherwise.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return expression corresponding to the given coalesce expression
     */
    public <Y> Expression<Y> coalesce(Expression<? extends Y> x, Expression<? extends Y> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that returns null if all its arguments evaluate to
     * null, and the value of the first non-null argument otherwise.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return coalesce expression
     */
    public <Y> Expression<Y> coalesce(Expression<? extends Y> x, Y y){
        //TODO
        return null;
    }

    /**
     * Create an expression that tests whether its argument are equal, returning
     * null if they are and the value of the first expression if they are not.
     * 
     * @param x
     *            expression
     * @param y
     *            expression
     * @return expression corresponding to the given nullif expression
     */
    public <Y> Expression<Y> nullif(Expression<Y> x, Expression<?> y){
        //TODO
        return null;
    }

    /**
     * Create an expression that tests whether its argument are equal, returning
     * null if they are and the value of the first expression if they are not.
     * 
     * @param x
     *            expression
     * @param y
     *            value
     * @return expression corresponding to the given nullif expression
     */
    public <Y> Expression<Y> nullif(Expression<Y> x, Y y){
        //TODO
        return null;
    }

    /**
     * Create a coalesce expression.
     * 
     * @return coalesce expression
     */
    public <T> Coalesce<T> coalesce(){
        //TODO
        return null;
    }

    /**
     * Create simple case expression.
     * 
     * @param expression
     *            to be tested against the case conditions
     * @return simple case expression
     */
    public <C, R> SimpleCase<C, R> selectCase(Expression<? extends C> expression){
        //TODO
        return null;
    }

    /**
     * Create a general case expression.
     * 
     * @return general case expression
     */
    public <R> Case<R> selectCase(){
        //TODO
        return null;
    }

    /**
     * Create an expression for execution of a database function.
     * 
     * @param name
     *            function name
     * @param type
     *            expected result type
     * @param args
     *            function arguments
     * @return expression
     */
    public <T> Expression<T> function(String name, Class<T> type, Expression<?>... args){
        //TODO
        return null;
    }
}
