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
*     pkrogh -        Java Persistence API 2.0 Public Draft
*                     Specification and licensing terms available from
*                     http://jcp.org/en/jsr/detail?id=317
*
* EARLY ACCESS - PUBLIC DRAFT
* This is an implementation of an early-draft specification developed under the 
* Java Community Process (JCP) and is made available for testing and evaluation 
* purposes only. The code is not compatible with any specification of the JCP.
******************************************************************************/
package javax.persistence;

/**
 * Defines strategies for fetching data from the database.
 * The <code>EAGER</code> strategy is a requirement on the persistence 
 * provider runtime that data must be eagerly fetched. The 
 * <code>LAZY</code> strategy is a hint to the persistence provider 
 * runtime that data should be fetched lazily when it is 
 * first accessed. The implementation is permitted to eagerly 
 * fetch data for which the <code>LAZY</code> strategy hint has been 
 * specified. In particular, lazy fetching might only be 
 * available for {@link Basic} mappings for which property-based 
 * access is used.
 *
 * <pre>
 *   Example:
 *   &#064;Basic(fetch=LAZY)
 *   protected String getName() { return name; }
 * </pre>
 *
 * @since Java Persistence API 1.0
 */
public enum FetchType {

    /** Defines that data can be lazily fetched */
    LAZY,

    /** Defines that data must be eagerly fetched */
    EAGER
}
