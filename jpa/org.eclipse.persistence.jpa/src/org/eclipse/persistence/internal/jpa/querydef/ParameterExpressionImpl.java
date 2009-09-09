package org.eclipse.persistence.internal.jpa.querydef;

import javax.persistence.criteria.ParameterExpression;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.expressions.ExpressionBuilder;

public class ParameterExpressionImpl<T> extends ExpressionImpl<T> implements ParameterExpression<T> {
    
    protected String name;
    protected Integer position;
    
    public ParameterExpressionImpl(Metamodel metamodel, Class<T> javaType, String name){
        super(metamodel, javaType, new ExpressionBuilder().getParameter(name));
        this.name = name;
    }

    public ParameterExpressionImpl(Metamodel metamodel, Class<T> javaType, Integer position){
        super(metamodel, javaType, new ExpressionBuilder().getParameter(position.toString()));
        this.position = position;
    }

    /**
     * Return the parameter name, or null if the parameter is not a named
     * parameter.
     * 
     * @return parameter name
     */
    public String getName(){
        return this.name;
    }

    /**
     * Return the parameter position, or null if the parameter is not a
     * positional parameter.
     * 
     * @return position of parameter
     */
    public Integer getPosition(){
        return this.position;
    }
    
    /**
     * Return the Java type of the parameter. Values bound to the
     * parameter must be assignable to this type.
     * This method is required to be supported for criteria queries
     * only. Applications that use this method for Java Persistence
     * query language queries and native queries will not be portable.
     * @return the Java type of the parameter
     * @throws IllegalStateException if invoked on a parameter
     * obtained from a Java persistence query language query or
     * native query when the implementation does not support this
     * use.
     */
     public Class<T> getParameterType(){
         return this.javaType;
     }


}
