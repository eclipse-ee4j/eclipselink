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

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.GenerationType.AUTO;

/**
 * Provides for the specification of generation strategies for 
 * the values of primary keys. The <code>GeneratedValue</code> 
 * annotation may be applied to a primary key property or
 * field of an entity or mapped superclass in conjunction with 
 * the {@link Id} annotation.
 *
 * <pre>
 *
 * Example 1:
 *
 *     &#064;Id
 *     &#064;GeneratedValue(strategy=SEQUENCE, generator="CUST_SEQ")
 *     &#064;Column(name="CUST_ID")
 *     public Long getId() { return id; }
 *
 *     Example 2:
 *
 *     &#064;Id
 *     &#064;GeneratedValue(strategy=TABLE, generator="CUST_GEN")
 *     &#064;Column(name="CUST_ID")
 *     Long id;
 * </pre>
 *
 * @since Java Persistence API 1.0
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)

public @interface GeneratedValue {

    /**
     * (Optional) The primary key generation strategy
     * that the persistence provider must use to
     * generate the annotated entity primary key.
     */
    GenerationType strategy() default AUTO;

    /**
     * (Optional) The name of the primary key generator
     * to use as specified in the {@link SequenceGenerator} 
     * or {@link TableGenerator} annotation.
     * <p> Defaults to the id generator supplied by persistence provider.
     */
    String generator() default "";
}
