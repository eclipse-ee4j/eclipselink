/*******************************************************************************
 * Copyright (c) 2008, 2009 Sun Microsystems. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     Linda DeMichiel -Java Persistence 2.0 - Proposed Final Draft, Version 2.0 (August 31, 2009)
 *     Specification available from http://jcp.org/en/jsr/detail?id=317
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
 * Defines the types of primary key generation strategies. 
 *
 * @see GeneratedValue
 *
 * @since Java Persistence 1.0
 */
public enum GenerationType { 

    /**
     * Indicates that the persistence provider must assign 
     * primary keys for the entity using an underlying 
     * database table to ensure uniqueness.
     */
    TABLE, 

    /**
     * Indicates that the persistence provider must assign 
     * primary keys for the entity using a database sequence.
     */
    SEQUENCE, 

    /**
     * Indicates that the persistence provider must assign 
     * primary keys for the entity using a database identity column.
     */
    IDENTITY, 

    /**
     * Indicates that the persistence provider should pick an 
     * appropriate strategy for the particular database. The 
     * <code>AUTO</code> generation strategy may expect a database 
     * resource to exist, or it may attempt to create one. A vendor 
     * may provide documentation on how to create such resources 
     * in the event that it does not support schema generation 
     * or cannot create the schema resource at runtime.
     */
    AUTO
}
