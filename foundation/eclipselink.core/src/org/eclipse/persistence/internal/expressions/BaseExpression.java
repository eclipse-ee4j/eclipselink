/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
    protected void postCopyIn(Dictionary alreadyDone) {
        super.postCopyIn(alreadyDone);
        if (this.baseExpression != null) {
            setBaseExpression(this.baseExpression.copiedVersionFrom(alreadyDone));
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