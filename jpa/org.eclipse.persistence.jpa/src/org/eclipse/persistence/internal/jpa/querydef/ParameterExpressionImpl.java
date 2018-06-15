/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

import javax.persistence.criteria.ParameterExpression;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.ClassConstants;

public class ParameterExpressionImpl<T> extends ExpressionImpl<T> implements ParameterExpression<T> {

    protected String name;
    protected String internalName;
    protected Integer position;

    public ParameterExpressionImpl(Metamodel metamodel, Class<T> javaType, String name){
        super(metamodel, javaType, new ExpressionBuilder().getParameter(name, javaType));
        this.name = name;
        this.internalName = name;
    }

    public ParameterExpressionImpl(Metamodel metamodel, Class<T> javaType){
        super(metamodel, javaType, null);
        this.internalName = String.valueOf(System.identityHashCode(this));
        this.currentNode = new ExpressionBuilder().getParameter(this.internalName, javaType);
    }

    public ParameterExpressionImpl(Metamodel metamodel, Class<T> javaType, Integer position){
        super(metamodel, javaType, new ExpressionBuilder().getParameter(position.toString(), javaType));
        this.position = position;
    }

    @Override
    public void findRootAndParameters(CommonAbstractCriteriaImpl query){
        query.addParameter(this);
    }

    /**
     * Return the parameter name, or null if the parameter is not a named
     * parameter.
     *
     * @return parameter name
     */
    @Override
    public String getName(){
        return this.name;
    }

    /**
     * Returns the name used by EclipseLink when a name has not been assigned by the user.
     * @return
     */
    public String getInternalName(){
        return this.internalName;
    }

    /**
     * Return the parameter position, or null if the parameter is not a
     * positional parameter.
     *
     * @return position of parameter
     */
    @Override
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
     @Override
    public Class<T> getParameterType(){
         return this.javaType;
     }

    @Override
    public int hashCode() {
        if (position != null){
            return position.hashCode();
        }
        if (this.internalName == null) this.internalName = "";
        return this.internalName.hashCode();
    }

    @Override
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
            return (this.internalName.equals(obj));
        }
        if (getClass() != obj.getClass())
            return false;
        ParameterExpressionImpl other = (ParameterExpressionImpl) obj;
        if (internalName == null) {
            if (other.internalName != null){
                return false;
            } else if (position == null){
                if (other.position != null){
                    return false;
                }
            } else if (!position.equals(other.position)){
                return false;
            }
        } else if (!internalName.equals(other.internalName)){
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (position == null){
            return "Parameter[name=" + name + "]";
        }else{
            return "Parameter[position=" + position + "]";
        }
    }


}
