/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import java.util.*;
import org.eclipse.persistence.expressions.*;

/**
 * Generic class for an expression with a base.
 * The base is the expression that this one is derived from.
 */
public abstract class BaseExpression extends Expression {

    /** The base expression is what this was derived from. */
    protected Expression baseExpression;
    /** PERF: Used to cache the builder. */
    protected ExpressionBuilder builder;

    public BaseExpression() {
        super();
    }

    public BaseExpression(Expression baseExpression) {
        super();
        this.baseExpression = baseExpression;
    }

    /**
     * The base expression is what the parameter was derived from.
     */
    public Expression getBaseExpression() {
        return baseExpression;
    }

    /**
     * Return the expression builder which is the ultimate base of this expression, or
     * null if there isn't one (shouldn't happen if we start from a root).
     */
    public ExpressionBuilder getBuilder() {
        if ((this.builder == null) && (baseExpression != null)) {
            this.builder = baseExpression.getBuilder();
        }
        return this.builder;
    }

    /**
     * INTERNAL:
     * Used for cloning.
     */
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        if (this.baseExpression != null) {
            setBaseExpression(this.baseExpression.copiedVersionFrom(alreadyDone));
        }
    }

    /**
     * INTERNAL:
     * Search the tree for any expressions (like SubSelectExpressions) that have been
     * built using a builder that is not attached to the query.  This happens in case of an Exists
     * call using a new ExpressionBuilder().  This builder needs to be replaced with one from the query.
     */
    public void resetPlaceHolderBuilder(ExpressionBuilder queryBuilder){
        if (this.baseExpression != null){
            this.baseExpression.resetPlaceHolderBuilder(queryBuilder);
        }
        
    }

    /**
     * The base expression is what the parameter was derived from.
     * This is used for nested parameters.
     */
    public void setBaseExpression(Expression baseExpression) {
        this.baseExpression = baseExpression;
        this.builder = null;
    }
    
    /**
     * INTERNAL:
     * Clear the builder when cloning.
     */
    public Expression shallowClone() {
        BaseExpression clone = (BaseExpression)super.shallowClone();
        clone.builder = null;
        return clone;
    }
}
