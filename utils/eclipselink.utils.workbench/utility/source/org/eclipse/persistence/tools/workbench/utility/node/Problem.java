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
package org.eclipse.persistence.tools.workbench.utility.node;

/**
 * Define an interface describing the problems associated with a node.
 */
public interface Problem {

	/**
	 * Return the node most closely associated with the problem.
	 */
	Node getSource();

	/**
	 * Return a key that can be used to uniquely identify the problem's message.
	 */
	String getMessageKey();

	/**
	 * Return the arguments associate with the problem's message.
	 */
	Object[] getMessageArguments();

	/**
	 * Return whether the problem is equal to the specified object.
	 * It is equal if the specified object is a implementation of the
	 * Problem interface and its source, message key, and message
	 * arguments are all equal to this problem's.
	 */
	boolean equals(Object o);

	/**
	 * Return the problem's hash code, which should calculated as an
	 * XOR of the source's hash code and the message key's hash code.
	 */
	int hashCode();

}
