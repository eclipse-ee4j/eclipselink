/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.utility.diff;

/**
 * Allow for multiple implementations of how to "diff" two objects.
 * For example, objects can be compared by
 * 	- using the identity operator (==)
 * 	- using the #equals(Object) method
 * 	- comparing the objects' attributes
 */
public interface Differentiator {

	/**
	 * Return the "diff" between the specified objects,
	 * which is determined by the particular implementation.
	 */
	Diff diff(Object object1, Object object2);

	/**
	 * Return the "key diff" between the specified objects.
	 * This is the "diff" between the objects that can be used
	 * to find which objects in a pair of collections should be
	 * compared; sorta like a primary key. If the objects do
	 * not have a pseudo primary key, the behavior for this
	 * method can be identical to normal #diff(Object, Object).
	 */
	Diff keyDiff(Object object1, Object object2);

	/**
	 * Return whether the differentiator is
	 * used to compare "value objects", as opposed to "reference objects".
	 * Client code can use this boolean to determine which differentiators are
	 * comparing values and which are comparing references. Typically reference
	 * objects should be encountered only once in a given object graph.
	 * 
	 * Value objects are objects whose identity is not significant. Typically value
	 * classes override the #equals(Object) method to compare the state of the
	 * two objects. Value objects are also typically immutable. Multiple occurrences
	 * of value objects with the same value are allowed. Examples: String, Integer,
	 * Float.
	 * 
	 * Reference objects are objects whose identity is significant. These objects
	 * are compared using the identity operator (==) and are typically mutable.
	 * Multiple occurrences of the same reference object are not allowed.
	 * Examples: Customer, Account, Class.
	 * 
	 * @see EqualityDifferentiator
	 */
	boolean comparesValueObjects();

}
