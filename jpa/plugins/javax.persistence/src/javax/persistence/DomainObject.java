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
 * Domain objects define the domain over which a query operates. A domain object
 * plays a role analogous to that of a Java Persistence query language
 * identification variable.
 * 
 * @since Java Persistence API 2.0
 */
public interface DomainObject extends PathExpression, QueryDefinition {
	/**
	 * Extend the query domain by joining with a class that can be navigated to
	 * or that is embedded in the class corresponding to the domain object on
	 * which the method is invoked. This method is permitted to be invoked only
	 * when defining the domain of the query. It must not be invoked within the
	 * context of the select, where, groupBy, or having operations. The domain
	 * object must correspond to a class that contains the referenced attribute.
	 * The query definition is modified to include the newly joined domain
	 * object.
	 * 
	 * @param attribute
	 *            - name of the attribute that references the target of the join
	 * @return the new DomainObject that is added for the target of the join
	 */
	DomainObject join(String attribute);

	/**
	 * Extend the query domain by left outer joining with a class that can be
	 * navigated to or that is embedded in the class corresponding to the domain
	 * object on which the method is invoked. This method is permitted to be
	 * invoked only when defining the domain of the query. It must not be
	 * invoked within the context of the select, where, groupBy, or having
	 * operations. The domain object must correspond to a class that contains
	 * the referenced attribute. The query definition is modified to include the
	 * newly joined domain object.
	 * 
	 * @param attribute
	 *            - name of the attribute that references the target of the join
	 * @return the new DomainObject that is added for the target of the join
	 */
	DomainObject leftJoin(String attribute);

	/**
	 * Specify that the association or element collection that is referenced by
	 * the attribute be eagerly fetched through use of an inner join. The domain
	 * object must correspond to a class that contains the referenced attribute.
	 * The query is modified to include the joined domain object.
	 * 
	 * @param attribute
	 *            - name of the attribute that references the target of the join
	 * @return the FetchJoinObject that is added for the target of the join
	 */
	FetchJoinObject joinFetch(String attribute);

	/**
	 * Specify that the association or element collection that is referenced by
	 * the attribute be eagerly fetched through use of a left outer join. The
	 * domain object must correspond to a class that contains the referenced
	 * attribute. The query is modified to include the joined domain object.
	 * 
	 * @param attribute
	 *            - name of the attribute that references the target of the join
	 * @return the FetchJoinObject that is added for the target of the join
	 */
	FetchJoinObject leftJoinFetch(String attribute);

	/**
	 * Return a path expression corresponding to the value of a map-valued
	 * association or element collection. This method is only permitted to be
	 * invoked upon a domain object that corresponds to a map-valued association
	 * or element collection.
	 * 
	 * @return PathExpression corresponding to the map value
	 */
	PathExpression value();

	/**
	 * Return a path expression corresponding to the key of a map-valued
	 * association or element collection. This method is only permitted to be
	 * invoked upon a domain object that corresponds to a map-valued association
	 * or element collection.
	 * 
	 * @return PathExpression corresponding to the map key
	 */
	PathExpression key();

	/**
	 * Return a select item corresponding to the map entry of a map-valued
	 * association or element collection. This method is only permitted to be
	 * invoked upon a domain object that corresponds to a map-valued association
	 * or element collection.
	 * 
	 * @return SelectItem corresponding to the map entry
	 */
	SelectItem entry();

	/**
	 * Return an expression that corresponds to the index. of the domain object
	 * in the referenced association or element collection. This method is only
	 * permitted to be invoked upon a domain object that corresponds to a
	 * multi-valued association or element collection for which an order column
	 * has been defined.
	 * 
	 * @return Expression denoting the index
	 */
	Expression index();
}
