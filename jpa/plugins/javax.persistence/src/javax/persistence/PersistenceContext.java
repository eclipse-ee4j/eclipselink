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
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Expresses a dependency on an {@link EntityManager} persistence context.
 *
 * @since Java Persistence 1.0 API
 */

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface PersistenceContext {

    /**
     * The name by which the entity manager is to be accessed in the 
     * environment referencing context, and is not needed when dependency 
     * injection is used.
     */
    String name() default "";

    /**
     * The name of the persistence unit. If the unitName element is 
     * specified, the persistence unit for the entity manager that 
     * is accessible in JNDI must have the same name.
     */
    String unitName() default "";

    /**
     * Specifies whether this is a transaction-scoped persistence context 
     * or an extended persistence context.
     */
    PersistenceContextType type() default PersistenceContextType.TRANSACTION;

    /**
     * Used to specify properties for the container or persistence
     * provider.  Vendor specific properties may be included in this
     * set of properties.  Properties that are not recognized by
     * a vendor are ignored.  
     */ 
    PersistenceProperty[] properties() default {};
}
