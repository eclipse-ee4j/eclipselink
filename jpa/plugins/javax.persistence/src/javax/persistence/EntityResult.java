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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * References an entity in the SELECT clause of a SQL query.
 * If this annotation is used, the SQL statement should select 
 * all of the columns that are mapped to the entity object. 
 * This should include foreign key columns to related entities. 
 * The results obtained when insufficient data is available 
 * are undefined.
 *
 * <pre>
 *   Example
 *   Query q = em.createNativeQuery(
 *       "SELECT o.id, o.quantity, o.item, i.id, i.name, i.description "+
 *           "FROM Order o, Item i " +
 *           "WHERE (o.quantity > 25) AND (o.item = i.id)",
 *       "OrderItemResults");
 *   &#064;SqlResultSetMapping(name="OrderItemResults",
 *       entities={
 *           &#064;EntityResult(entityClass=com.acme.Order.class),
 *           &#064;EntityResult(entityClass=com.acme.Item.class)
 *   })
 * </pre>
 *
 * @since Java Persistence 1.0 API
 */
@Target({}) 
@Retention(RUNTIME)

public @interface EntityResult { 

    /** The class of the result */
    Class entityClass(); 

    /** 
     * Maps the columns specified in the SELECT list of the 
     * query to the properties or fields of the entity class. 
     */
    FieldResult[] fields() default {};

    /** 
     * Specifies the column name (or alias) of the column in 
     * the SELECT list that is used to determine the type of 
     * the entity instance.
     */
    String discriminatorColumn() default "";
}
