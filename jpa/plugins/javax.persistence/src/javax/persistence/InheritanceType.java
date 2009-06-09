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
package javax.persistence;

/**
 * Defines inheritance strategy options.
 *
 * @since Java Persistence 1.0
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
