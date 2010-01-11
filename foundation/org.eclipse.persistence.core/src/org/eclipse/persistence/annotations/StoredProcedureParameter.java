/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static org.eclipse.persistence.annotations.Direction.IN;

/** 
 * A StoredProcedureParameter annotation is used within a 
 * NamedStoredProcedureQuery annotation.
 * 
 * @see org.eclipse.persistence.annotations.NamedStoredProcedureQuery
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0 
 */ 
@Target({})
@Retention(RUNTIME)
public @interface StoredProcedureParameter {
    /**
     * (Optional) The direction of the stored procedure parameter.
     */
    Direction direction() default IN;

    /**
     * (Optional) Stored procedure parameter name.
     */
    String name() default "";

    /**
     * (Required) The query parameter name.
     */
    String queryParameter();

    /**
     * (Optional) The type of Java class desired back from the procedure, 
     * this is dependent on the type returned from the procedure.
     */
    Class type() default void.class;

    /**
     * (Optional) The JDBC type code, this is dependent on the type returned 
     * from the procedure.
     */
    int jdbcType() default -1;

    /**
     * (Optional) The JDBC type name, this may be required for ARRAY or 
     * STRUCT types.
     */
    String jdbcTypeName() default "";
}
