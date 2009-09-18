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

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.FetchType.LAZY;

/**
 * Defines a collection of instances of a basic type or embeddable
 * class. 
 * Must be specified if the collection is to be mapped by
 * means of a collection table.
 * 
 * <pre>
 *    Example:
 *
 *    &#064;Entity public class Person {
 *       &#064;Id protected String ssn;
 *       protected String name;
 *       ...
 *       &#064;ElementCollection  
 *       protected Set&#060;String&#062; nickNames = new HashSet();
 *         ...
 *    } 
 *  </pre>
 *
 * @since Java Persistence 2.0
 */
@Target( { METHOD, FIELD })
@Retention(RUNTIME)
public @interface ElementCollection {

    /**
     * (Optional) The basic or embeddable class that is the element
     * type of the collection.  This element is optional only if the
     * collection field or property is defined using Java generics,
     * and must be specified otherwise.  It defaults to the
     * paramterized type of the collection when defined using
     * generics.
     */
    Class targetClass() default void.class;
    
    /**
     *  (Optional) Whether the collection should be lazily loaded or must be
     *  eagerly fetched.  The EAGER strategy is a requirement on
     *  the persistence provider runtime that the collection elements
     *  must be eagerly fetched.  The LAZY strategy is a hint to the
     *  persistence provider runtime.
     */
    FetchType fetch() default LAZY;
}
