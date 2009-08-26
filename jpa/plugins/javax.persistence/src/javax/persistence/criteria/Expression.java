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

import java.util.Collection;

/**
 * Type for query expressions.
 * 
 * @param <T>
 *            the type of the expression
 */
public interface Expression<T> extends Selection<T> {

    /**
     *  Apply a predicate to test whether the expression is null.
     * @return predicate testing whether the expression is null
     */
    Predicate isNull();

    /**
     * Apply a predicate to test whether the expression is not null.
     * 
     * @return predicate testing whether the expression is not null.
     */
    Predicate isNotNull();

    /**
     * Apply a predicate to test whether the expression is a member of the
     * argument list.
     * 
     * @param values
     * @return predicate testing for membership in the list
     */
    Predicate in(Object... values);

    /**
     * Apply a predicate to test whether the expression is a member
     * of the argument list.
     * @param values
     * @return predicate testing for membership
     */
    Predicate in(Expression<?>... values);

    /**
     * Apply a predicate to test whether the expression is a member
     * of the collection.
     * @param values collection
     * @return predicate testing for membership
     */
    Predicate in(Collection<?> values);

    /**
     * Apply a predicate to test whether the expression is a member
     * of the collection.
     * @param values expression corresponding to collection
     * @return predicate testing for membership
     */
    Predicate in(Expression<Collection<?>> values);

    /**
     * Perform a typecast upon the expression.
     * Warning: may result in a runtime failure.
     * @param type 
     * @return expression
     */
    <X> Expression<X> as(Class<X> type);
}