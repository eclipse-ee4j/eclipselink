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
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     		     Specification available from http://jcp.org/en/jsr/detail?id=317
 *     gyorke - Java Persistence 2.0 - Post Proposed Final Draft (March 13, 2009) Updates
 *               Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 * Java(TM) Persistence API, Version 2.0 - EARLY ACCESS
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP).  The code is untested and presumed not to be a  
 * compatible implementation of JSR 317: Java(TM) Persistence API, Version 2.0.   
 * We encourage you to migrate to an implementation of the Java(TM) Persistence 
 * API, Version 2.0 Specification that has been tested and verified to be compatible 
 * as soon as such an implementation is available, and we encourage you to retain 
 * this notice in any implementation of Java(TM) Persistence API, Version 2.0 
 * Specification that you distribute.
 ******************************************************************************/
package javax.persistence.criteria;

import java.util.List;

/**
 * The type of a simple or compound predicate: a conjunction or disjunction of
 * restrictions. A simple predicate is considered to be a conjunction with a
 * single conjunct.
 * 
 * since Java Persistence 2.0
 */
public interface Predicate extends Expression<Boolean> {
    public static enum BooleanOperator {
        AND, OR, NOT
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
     * Apply negation to the predicate.
     * 
     * @return the negated predicate
     */
    Predicate negate();
}