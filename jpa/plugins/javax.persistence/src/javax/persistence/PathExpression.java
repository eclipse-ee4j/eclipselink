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
 * Interface for operations over objects reached via paths
 * 
 * @since Java Persistence API 2.0
 */
public interface PathExpression extends Expression {
	/**
	 * Return a path expression corresponding to the referenced attribute. It is
	 * not permitted to invoke this method on a path expression that corresponds
	 * to a multi-valued association or element collection. The path expression
	 * on which this method is invoked must correspond to a class containing the
	 * referenced attribute.
	 * 
	 * @param attributeName
	 *            - name of the referenced attribute
	 * @return path expression
	 */
	PathExpression get(String attributeName);

	/**
	 * Return an expression that corresponds to the type of the entity. This
	 * method can only be invoked on a path expression corresponding to an
	 * entity. It is not permitted to invoke this method on a path expression
	 * that corresponds to a multi-valued association.
	 * 
	 * @return expression denoting the entity's type
	 */
	Expression type();

	/**
	 * Return an expression that corresponds to the number of elements
	 * association or element collection corresponding to the path expression.
	 * This method can only be invoked on a path expression that corresponds to
	 * a multi-valued association or to an element collection.
	 * 
	 * @return expression denoting the size
	 */
	Expression size();

	/**
	 * Add a restriction that the path expression must correspond to an
	 * association or element collection that is empty (has no elements). This
	 * method can only be invoked on a path expression that corresponds to a
	 * multi-valued association or to an element collection.
	 * 
	 * @return predicate corresponding to the restriction
	 */
	Predicate isEmpty();

	/**
	 * Specify that the avg operation is to be applied. The path expression must
	 * correspond to an attribute of a numeric type. It is not permitted to
	 * invoke this method on a path expression that corresponds to a
	 * multi-valued association or element collection.
	 * 
	 * @return the resulting aggregate
	 */
	Aggregate avg();

	/**
	 * Specify that the max operation is to be applied. The path expression must
	 * correspond to an attribute of an orderable type. It is not permitted to
	 * invoke this method on a path expression that corresponds to a
	 * multi-valued association or element collection.
	 * 
	 * @return the resulting aggregate
	 */
	Aggregate max();

	/**
	 * Specify that the min operation is to be applied. The path expression must
	 * correspond to an attribute of an orderable type. It is not permitted to
	 * invoke this method on a path expression that corresponds to a
	 * multi-valued association or element collection.
	 * 
	 * @return the resulting aggregate
	 */
	Aggregate min();

	/**
	 * Specify that the count operation is to be applied. It is not permitted to
	 * invoke this method on a path expression that corresponds to a
	 * multi-valued association or element collection.
	 * 
	 * @return the resulting aggregate
	 */
	Aggregate count();

	/**
	 * Specify that the sum operation is to be applied. The path expression must
	 * correspond to an attribute of a numeric type. It is not permitted to
	 * invoke this method on a path expression that corresponds to a
	 * multi-valued association or element collection.
	 * 
	 * @return the resulting aggregate
	 */
	Aggregate sum();

}
