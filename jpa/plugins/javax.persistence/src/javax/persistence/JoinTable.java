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
 *               Specification available from http://jcp.org/en/jsr/detail?id=317
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
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is used in the mapping of associations. It 
 * is specified on the owning side of a many-to-many association, 
 * or in a unidirectional one-to-many association.
 *
 * <p> If the <code>JoinTable</code> annotation is missing, the 
 * default values of the annotation elements apply.  The name 
 * of the join table is assumed to be the table names of the 
 * associated primary tables concatenated together (owning side 
 * first) using an underscore.
 *
 * <pre>
 *
 *    Example:
 *    &#064;JoinTable(
 *    name="CUST_PHONE",
 *    joinColumns=
 *        &#064;JoinColumn(name="CUST_ID", referencedColumnName="ID"),
 *    inverseJoinColumns=
 *        &#064;JoinColumn(name="PHONE_ID", referencedColumnName="ID")
 *    )
 * </pre>
 * 
 * @see JoinColumn
 * @see JoinColumns
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface JoinTable {

    /**
     * (Optional) The name of the join table. 
     * 
     * <p> Defaults to the concatenated names of
     * the two associated primary entity tables, 
     * separated by an underscore.
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
     * (Optional) The foreign key columns
     * of the join table which reference the
     * primary table of the entity owning the
     * association (i.e. the owning side of
     * the association).
     *
     * <p> Uses the same defaults as for {@link JoinColumn}.
     */
    JoinColumn[] joinColumns() default {};

    /** 
     * (Optional) The foreign key columns
     * of the join table which reference the
     * primary table of the entity that does
     * not own the association (i.e. the
     * inverse side of the association).
     *
     * <p> Uses the same defaults as for {@link JoinColumn}.
     */
    JoinColumn[] inverseJoinColumns() default {};

    /**
     * (Optional) Unique constraints that are
     * to be placed on the table. These are
     * only used if table generation is in effect.
     * <p> Defaults to no additional constraints.
     */
    UniqueConstraint[] uniqueConstraints() default {};
}
