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
 * Factory interface for query definition objects
 * 
 * @since Java Persistence API 2.0
 */
public interface QueryBuilder {
	/**
	 * Create an uninitialized query definition object.
	 * 
	 * @return query definition instance
	 */
	QueryDefinition createQueryDefinition();

	/**
	 * Create a query definition object with the given root. The root must be an
	 * entity class.
	 * 
	 * @param cls
	 *            - an entity class
	 * @return root domain object
	 */
	DomainObject createQueryDefinition(Class root);

	/**
	 * Create a query definition object whose root is derived from a domain
	 * object of the containing query. Provides support for correlated
	 * subqueries. Joins against the resulting domain object do not affect the
	 * query domain of the containing query. The path expression must correspond
	 * to an entity class. The path expression must not be a domain object of
	 * the containing query.
	 * 
	 * @param path
	 *            - path expression corresponding to the domain object used to
	 *            derive the subquery root.
	 * @return the subquery DomainObject
	 */
	DomainObject createSubqueryDefinition(PathExpression path);
}
