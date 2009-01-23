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
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.FetchType.EAGER;

/**
 * The <code>Basic</code> annotation is the simplest type of mapping 
 * to a database column. The <code>Basic</code> annotation can be 
 * applied to a persistent property or instance variable of any of the 
 * following types: Java primitive types, wrappers of the primitive types, 
 * {@link String}, {@link java.math.BigInteger java.math.BigInteger}, 
 * {@link java.math.BigDecimal java.math.BigDecimal}, 
 * {@link java.util.Date java.util.Date}, 
 * {@link java.util.Calendar java.util.Calendar}, 
 * {@link java.sql.Date java.sql.Date}, {@link java.sql.Time java.sql.Time}, 
 * {@link java.sql.Timestamp java.sql.Timestamp}, <code>byte[], Byte[], 
 * char[], Character[]</code>, enums, and any other type that implements 
 * {@link java.io.Serializable Serializable}. 
 * 
 * <p> The use of the <code>Basic</code> annotation is optional for 
 * persistent fields and properties of these types.
 *
 * @since Java Persistence API 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface Basic {

    /**
     * (Optional) Defines whether the value of the field or property should 
     * be lazily loaded or must be eagerly fetched. The <code>EAGER</code> 
     * strategy is a requirement on the persistence provider runtime 
     * that the value must be eagerly fetched.  The <code>LAZY</code> 
     * strategy is a hint to the persistence provider runtime.
     * If not specified, defaults to <code>EAGER</code>.
     */
    FetchType fetch() default EAGER;

    /**
     * (Optional) Defines whether the value of the field or property may be null. 
     * This is a hint and is disregarded for primitive types; it may 
     * be used in schema generation.
     * If not specified, defaults to <code>true</code>.
     */
    boolean optional() default true;
}
