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
package org.eclipse.persistence.tools.workbench.utility;

/**
 * Interface defining the classic stack behavior,
 * without the backdoors allowed by java.util.Stack.
 */
public interface Stack {

	/**
	 * "Push" the specified item on to the top of the stack.
	 */
	void push(Object o);

	/**
	 * "Pop" an item from the top of the stack.
	 */
	Object pop();

	/**
	 * Return the item on the top of the stack
	 * without removing it from the stack.
	 */
	Object peek();

	/**
	 * Return whether the stack is empty.
	 */
	boolean isEmpty();

}
