/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     			 Specification and licensing terms available from
 *     		   	 http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
package javax.persistence.criteria;

import java.util.List;

/**
 * The type of a simple or compound predicate: a conjunction or disjunction of
 * restrictions. A simple predicate is considered to be a conjunction with a
 * single conjunct.
 */
public interface Predicate extends Expression<Boolean> {
    public static enum BooleanOperator {
        AND, OR
    }

    /**
     * Return the boolean operator for the predicate. If the predicate is
     * simple, this is AND.
     * 
     * @return boolean operator for the predicate
     */
    BooleanOperator getOperator();

    /**
     * Has negation been applied to the predicate.
     * 
     * @return boolean indicating if the predicate has been negated
     */
    boolean isNegated();

    /**
     * Return the top-level conjuncts or disjuncts of the predicate.
     * 
     * @return list boolean expressions forming the predicate
     */
    List<Expression<Boolean>> getExpressions();

    /**
     * Add another operand to the predicate. Whether the operand is added as a
     * conjunct or disjunct is determined by the predicate operator.
     * 
     * @return the resulting compound predicate
     */
    Predicate add(Expression<Boolean> s);

    /**
     * Apply negation to the predicate.
     * 
     * @return the negated predicate
     */
    Predicate negate();
}