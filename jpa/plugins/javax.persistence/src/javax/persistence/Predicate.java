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
 *     dclarke - Java Persistence API 2.0 Public Draft
 *     			 Specification and licensing terms available from
 *     		   	 http://jcp.org/en/jsr/detail?id=317
 *     
 * IMPORTANT: The Criteria API is defined as per the public draft specification
 * but is not implemented in the EclipseLink's early access.
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
package javax.persistence;

/**
 * Interface used to define compound predicates.
 * 
 * @since Java Persistence API 2.0
 */
public interface Predicate {
	/**
	 * Creates an AND of the predicate with the argument.
	 * 
	 * @param predicate
	 *            - A simple or compound predicate
	 * @return the predicate that is the AND of the original simple or compound
	 *         predicate and the argument.
	 */
	Predicate and(Predicate predicate);

	/**
	 * Creates an OR of the predicate with the argument.
	 * 
	 * @param predicate
	 *            - A simple or compound predicate
	 * @return the predicate that is the OR of the original simple or compound
	 *         predicate and the argument.
	 */
	Predicate or(Predicate predicate);

	/**
	 * Creates a negation of the predicate with the argument.
	 * 
	 * @return the predicate that is the negation of the original simple or
	 *         compound predicate.
	 */
	Predicate not();
}
