/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
 * OrderCorrectionType defines a strategy taken in case the order list read from the data base is invalid
 * (has nulls, duplicates, negative values, values greater/equal to list size).
 * 
 * OrderCorrectionType could be set into CollectionMapping
 * using setListOrderCorrectionType method.
 * 
 * @see org.eclipse.persistence.mappings.CollectionMapping
 */ 
public enum OrderCorrectionType {
    /**
     * The read into the cache list order is corrected, but no knowledge
     * is kept about the invalid list left in the data base.
     * This is no problem in case of a read-only usage of the list,
     * but in case the list is modified and saved back into the data base
     * the order likely will not be the same as in cache and will be invalid.
     * This mode is used by default in case the mapped attribute
     * is neither List nor Vector 
     * (more precisely: is not assignable from Eclipselink internal class IndirectList).
     */
    READ,

    /**
     * The read into the cache list order is corrected, and remember
     * that the invalid list left in the data base.
     * When the list is updated and saved back into the data base
     * all the order indexes are saved ensuring that the list
     * order in the data base will be exactly the same as in cache
     * (and therefore valid).
     * This mode is used by default in case the mapped attribute
     * is either List or Vector 
     * (more precisely: is assignable from Eclipselink internal class IndirectList).
     */
    READ_WRITE,

    /**
     * Don't correct, just throw an exception.
     */
    EXCEPTION
}
