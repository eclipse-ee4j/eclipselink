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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.persistence.QueryHint;

/** 
 * A NamedStoredProcedureQuery annotation allows the definition of queries that 
 * call stored procedures as named queries.
 * 
 * A NamedStoredProcedureQuery annotation may be defined on an Entity or
 * MappedSuperclass.
 * 
 * @see org.eclipse.persistence.annotations.StoredProcedureParameter.
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0 
 */ 
@Target({TYPE})
@Retention(RUNTIME)
public @interface NamedStoredProcedureQuery {
    /**
     * (Required) Unique name that references this stored procedure query.
     */
    String name();

    /**
     * (Optional) Query hints.
     */
    QueryHint[] hints() default {};

    /**
     * (Optional) Refers to the class of the result.
     */
    Class resultClass() default void.class;

    /**
     * (Optional) The name of the SQLResultMapping.
     */
    String resultSetMapping() default "";

    /**
     * (Required) The name of the stored procedure.
     */
    String procedureName();

    /**
     * (Optional) Whether the query should return a result set. You should only 
     * set this flag to true if you expect a raw JDBC ResultSet to be returned 
     * from  your stored proceduce. Otherwise, you  should let the default apply.
     */
    boolean returnsResultSet() default false; 

    /**
     * (Optional) Defines arguments to the stored procedure.
     */
    StoredProcedureParameter[] parameters() default {};
}
