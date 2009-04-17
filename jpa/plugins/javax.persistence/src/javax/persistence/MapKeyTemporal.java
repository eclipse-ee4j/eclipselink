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

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation must be specified for persistent map keys of type 
 * {@link java.util.Date} and {@link java.util.Calendar}. It may only be 
 * specified for map keys of these types.
 * 
 * <p> The <code>MapKeyTemporal</code> annotation can be applied to an element 
 * collection or relationship of type java.util.Map, in conjunction with the 
 * {@link ElementCollection}, {@link OneToMany}, or {@link ManyToMany annotation}.
 *
 * <pre>
 *     Example:
 * 
 *     &#064;OneToMany
 *     &#064;MapKeyTemporal(DATE)
 *     protected java.util.Map<java.util.Date, Employee> employees;
 * </pre>
 *
 * @since Java Persistence 2.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface MapKeyTemporal {

    /** The type used in mapping java.util.Date or java.util.Calendar. */
    TemporalType value();
}

