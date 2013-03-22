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
 *     06/10/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

/** 
 * The OrderCorrectionType enum is used with OrderCorrection annotation 
 * that could be applied together with OrderColumn annotation.
 * OrderCorrectionType defines a strategy taken in case the order list read from the data base is invalid
 * (has nulls, duplicates, negative values, values greater/equal to list size -
 * the only valid order list of n elements is: {0, 1,..., n-1}).
 * 
 * OrderCorrectionType also could be set directly into CollectionMapping
 * using setListOrderCorrectionType method.
 * 
 * @see org.eclipse.persistence.annotations.OrderCorrection
 * @see org.eclipse.persistence.mappings.CollectionMapping
 */ 
public enum OrderCorrectionType {
    /**
     * Order of the list read into application is corrected, but no knowledge
     * is kept about the invalid list order left in the data base.
     * This is no problem in case of a read-only usage of the list,
     * but in case the list is modified and saved back into the data base
     * the order likely will not be the same as in cache and will be invalid.
     * This mode is used by default in case the mapped attribute
     * is neither List nor Vector 
     * (more precisely: is not assignable from Eclipselink internal class IndirectList).
     * Example: a list of three objects:
     *   in the data base:               {null, objectA}; {2, objectB}, {5, ObjectC};
     *   read into application as a list:{objectA, objectB, objectC};
     *   add a new element to the list:  {objectA, objectB, objectC, objectD};
     *   updated list saved to the db:   {null, objectA}, {2, objectB}, {5, objectC}, {3, objectD};
     *   read again into application:    {objectA, objectB, objectD, objectC};
     */
    READ,

    /**
     * Order of the list read into application is corrected, and remembered
     * that the invalid list order left in the data base.
     * If the list is updated and saved back into the data base
     * then all the order indexes are saved ensuring that the list
     * order in the data base will be exactly the same as in cache
     * (and therefore valid).
     * This mode is used by default in case the mapped attribute
     * is either List or Vector 
     * (more precisely: is assignable from Eclipselink internal class IndirectList).
     * This mode is used in JPA in case OrderCorrection annotation is not specified.
     * Example: a list of three objects:
     *   in the data base:               {null, objectA}; {2, objectB}, {5, ObjectC};
     *   read into application as a list:{objectA, objectB, objectC};
     *   add a new element to the list:  {objectA, objectB, objectC, objectD};
     *   updated list saved to the db:   {0, objectA}, {1, objectB}, {2, objectC}, {3, objectD};
     *   read again into application:    {objectA, objectB, objectC, objectD};
     */
    READ_WRITE,

    /**
     * Don't correct, throw QueryException with error code QueryException.LIST_ORDER_FIELD_WRONG_VALUE
     * Example: a list of three objects:
     *   in the data base: {null, objectA}; {2, objectB}, {5, ObjectC};
     *   read into application - exception.
     */
    EXCEPTION
}
