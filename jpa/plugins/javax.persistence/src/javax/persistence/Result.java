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
 *               Specification and licensing terms available from
 *               http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/

package javax.persistence;

/**
 * Interface for extracting the result items from a typed query result.
 * 
 * @since Java Persistence 2.0
 */
public interface Result {
    /**
     * Get the value of the specified result item.
     * 
     * @param resultItem
     *            result list item
     * @return value of result list item
     * @throws IllegalArgument
     *             exception if result item does not correspond to an item in
     *             the query result
     */
    <X> X get(ResultItem<X> resultItem);

    /**
     * Get the value of the result list item to which the specified alias has
     * been assigned.
     * 
     * @param alias
     *            alias assigned to result list item
     * @return type of the result list item
     * @throws IllegalArgument
     *             exception if alias does not correspond to an item in the
     *             query result or type is incorrect
     */
    <X> X get(String alias, Class<X> type);

    /**
     * Get the value of the item at the specified position in the result list.
     * The first position is 0.
     * 
     * @param i
     *            position in result list
     * @param type
     *            type of the result list item
     * @return value of the result list item
     * @throws IllegalArgument
     *             exception if i exceeds length of result list of type is
     *             incorrect
     */
    <X> X get(int i, Class<X> type);

    /**
     * Get the value of the item at the specified position in the result list.
     * The first position is 0.
     * 
     * @param i
     *            position in result list
     * @return value of the result item
     * @throws IllegalArgument
     *             exception if i exceeds length of result list
     */
    Object get(int i);

    /**
     * Return the values of the result list items as an array.
     * 
     * @return result list values
     */
    Object[] toArray();
}