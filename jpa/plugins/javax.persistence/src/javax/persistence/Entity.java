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
<<<<<<< .mine
 * Specifies that the class is an entity. This annotation is 
 * applied to the entity class.
 *
 * @since Java Persistence API 1.0
=======
 * Specifies that the class is an entity. This annotation is applied to the
 * entity class.
 * 
 * @since Java Persistence API 1.0
>>>>>>> .theirs
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Entity {

	/**
	 * The name of an entity. Defaults to the unqualified name of the entity
	 * class. This name is used to refer to the entity in queries. The name must
	 * not be a reserved literal in the Java Persistence query language.
	 */
	String name() default "";
}
