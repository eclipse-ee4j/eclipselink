/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     06/10/2009 Andrei Ilitchev
//       - JPA 2.0 - OrderedList support.
package org.eclipse.persistence.annotations;

/**
 * The OrderCorrectionType enum is used with {@linkplain OrderCorrection} annotation
 * that could be applied together with {@linkplain jakarta.persistence.OrderColumn} annotation.
 * OrderCorrectionType defines a strategy taken in case the order list read from the database is invalid
 * (has nulls, duplicates, negative values, values greater/equal to list size -
 * the only valid order list of n elements is: {0, 1,..., n-1}).
 * <p>
 * OrderCorrectionType also could be set directly into {@linkplain org.eclipse.persistence.mappings.CollectionMapping}
 * using {@linkplain org.eclipse.persistence.mappings.CollectionMapping#setOrderCorrectionType(OrderCorrectionType)} method.
 *
 * @see OrderCorrection
 * @see org.eclipse.persistence.mappings.CollectionMapping
 */
public enum OrderCorrectionType {
    /**
     * Order of the list read into application is corrected, but no knowledge
     * is kept about the invalid list order left in the database.
     * This is no problem in case of a read-only usage of the list,
     * but in case the list is modified and saved back into the database
     * the order likely will not be the same as in cache and will be invalid.
     * <p>
     * This mode is used by default in case the mapped attribute is neither
     * List nor Vector (more precisely: is not assignable from
     * Eclipselink internal class {@linkplain org.eclipse.persistence.indirection.IndirectList}).
     * <p><b>Example:</b>
     * <table>
     *     <caption>a list of three objects:</caption>
     *     <tr>
     *         <td>in the database:</td>
     *         <td>{null, objectA}; {2, objectB}, {5, ObjectC};</td>
     *     </tr>
     *     <tr>
     *         <td>read into application as a list:</td>
     *         <td>{objectA, objectB, objectC};</td>
     *     </tr>
     *     <tr>
     *         <td>add a new element to the list:</td>
     *         <td>{objectA, objectB, objectC, objectD};</td>
     *     </tr>
     *     <tr>
     *         <td>updated list saved to the db:</td>
     *         <td>{null, objectA}, {2, objectB}, {5, objectC}, {3, objectD};</td>
     *     </tr>
     *     <tr>
     *         <td>read again into application:</td>
     *         <td>{objectA, objectB, objectD, objectC};</td>
     *     </tr>
     * </table>
     */
    READ,

    /**
     * Order of the list read into application is corrected, and remembered
     * that the invalid list order left in the database.
     * If the list is updated and saved back into the database
     * then all the order indexes are saved ensuring that the list
     * order in the database will be exactly the same as in cache
     * (and therefore valid).
     * <p>
     * This mode is used by default in case the mapped attribute
     * is either List or Vector (more precisely: is assignable from
     * Eclipselink internal class {@linkplain org.eclipse.persistence.indirection.IndirectList}).
     * <p>
     * JPA uses this mode in case {@linkplain OrderCorrection} annotation is not specified.
     * <p><b>Example:</b>
     * <table>
     *     <caption>a list of three objects:</caption>
     *     <tr>
     *         <td>in the database:</td>
     *         <td>{null, objectA}; {2, objectB}, {5, ObjectC};</td>
     *     </tr>
     *     <tr>
     *         <td>read into application as a list:</td>
     *         <td>{objectA, objectB, objectC};</td>
     *     </tr>
     *     <tr>
     *         <td>add a new element to the list:</td>
     *         <td>{objectA, objectB, objectC, objectD};</td>
     *     </tr>
     *     <tr>
     *         <td>updated list saved to the db:</td>
     *         <td>{0, objectA}, {1, objectB}, {2, objectC}, {3, objectD};</td>
     *     </tr>
     *     <tr>
     *         <td>read again into application:</td>
     *         <td>{objectA, objectB, objectC, objectD};</td>
     *     </tr>
     * </table>
     */
    READ_WRITE,

    /**
     * Don't correct, throw {@linkplain org.eclipse.persistence.exceptions.QueryException}
     * with error code {@linkplain org.eclipse.persistence.exceptions.QueryException#LIST_ORDER_FIELD_WRONG_VALUE}
     * <p><b>Example:</b>
     * <table>
     *     <caption>a list of three objects:</caption>
     *     <tr>
     *         <td>in the database:</td>
     *         <td>{null, objectA}; {2, objectB}, {5, ObjectC};</td>
     *     </tr>
     *     <tr>
     *         <td>read into application:</td>
     *         <td>exception</td>
     *     </tr>
     * </table>
     */
    EXCEPTION
}
