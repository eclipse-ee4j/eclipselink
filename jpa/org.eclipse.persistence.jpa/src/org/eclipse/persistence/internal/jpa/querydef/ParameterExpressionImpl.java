package org.eclipse.persistence.internal.jpa.querydef;

import javax.persistence.criteria.ParameterExpression;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.ClassConstants;

public class ParameterExpressionImpl<T> extends ExpressionImpl<T> implements ParameterExpression<T> {
    
    protected String name;
    protected Integer position;
    
    public ParameterExpressionImpl(Metamodel metamodel, Class<T> javaType, String name){
        super(metamodel, javaType, new ExpressionBuilder().getParameter(name, javaType));
        this.name = name;
    }

    public ParameterExpressionImpl(Metamodel metamodel, Class<T> javaType){
        super(metamodel, javaType, null);
        this.name = String.valueOf(System.identityHashCode(this));
        this.currentNode = new ExpressionBuilder().getParameter(this.name, javaType);
    }

    public ParameterExpressionImpl(Metamodel metamodel, Class<T> javaType, Integer position){
        super(metamodel, javaType, new ExpressionBuilder().getParameter(position.toString(), javaType));
        this.position = position;
        this.name = String.valueOf(position);
    }

    public void findRootAndParameters(AbstractQueryImpl query){
        query.addParameter(this);
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

    @Override
    public int hashCode() {
        if (this.name == null) this.name = "";
        return this.name.hashCode();
    }

    public boolean isParameter(){
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (ClassConstants.STRING == obj.getClass()) {
            return (this.name.equals(obj));
        }
        if (getClass() != obj.getClass())
            return false;
        ParameterExpressionImpl other = (ParameterExpressionImpl) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
