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
 * Defines inheritance strategy options.
 *
 * @since Java Persistence API 1.0
 */
public enum InheritanceType { 

    /** A single table per class hierarchy */
    SINGLE_TABLE, 

    /** A table per concrete entity class */
    TABLE_PER_CLASS, 

    /** 
     * A strategy in which fields that are specific to a 
     * subclass are mapped to a separate table than the fields 
     * that are common to the parent class, and a join is 
     * performed to instantiate the subclass.
     */
    JOINED 
}
