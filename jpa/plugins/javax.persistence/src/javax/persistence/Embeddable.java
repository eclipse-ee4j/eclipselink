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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a class whose instances are stored as an intrinsic 
 * part of an owning entity and share the identity of the entity. 
 * Each of the persistent properties or fields of the embedded 
 * object is mapped to the database table for the entity. Only 
 * {@link Basic}, {@link Column}, {@link Lob}, 
 * {@link Temporal}, and {@link Enumerated} mapping 
 * annotations may portably be used to map the persistent fields 
 * or properties of classes annotated as {@link Embeddable}.
 *
 * <p> Note that the {@link Transient} annotation may be used to 
 * designate the non-persistent state of an embeddable class.
 *
 * @since Java Persistence API 1.0
 */
@Target({TYPE}) 
@Retention(RUNTIME)

public @interface Embeddable {
}
