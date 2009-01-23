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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation specifies the primary table for the annotated 
 * entity. Additional tables may be specified using {@link SecondaryTable}
 * or {@link SecondaryTables} annotation. 
 *
 * <p> If no <code>Table</code> annotation is specified for an entity 
 * class, the default values apply.
 *
 * <pre>
 *    Example:
 *
 *    &#064;Entity
 *    &#064;Table(name="CUST", schema="RECORDS")
 *    public class Customer { ... }
 * </pre>
 *
 * @since Java Persistence API 1.0
 */
@Target(TYPE) 
@Retention(RUNTIME)

public @interface Table {

    /**
     * (Optional) The name of the table.
     * <p> Defaults to the entity name.
     */
    String name() default "";

    /** (Optional) The catalog of the table.
     * <p> Defaults to the default catalog.
     */
    String catalog() default "";

    /** (Optional) The schema of the table.
     * <p> Defaults to the default schema for user.
     */
    String schema() default "";

    /**
     * (Optional) Unique constraints that are to be placed on 
     * the table. These are only used if table generation is in 
     * effect. These constraints apply in addition to any constraints 
     * specified by the {@link Column} and {@link JoinColumn} 
     * annotations and constraints entailed by primary key mappings.
     * <p> Defaults to no additional constraints.
     */
    UniqueConstraint[] uniqueConstraints() default {};
}
