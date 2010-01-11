/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * This implementation of the PluggableValidator.Delegate interface
 * will validate the node immediately.
 * 
 * This is useful for debugging in a single thread or generating
 * problem reports.
 */
public class SynchronousValidator
	implements PluggableValidator.Delegate
{
	private Node node;

	/**
	 * Construct a validator that will immediately validate the
	 * specified node.
	 */
	public SynchronousValidator(Node node) {
		super();
		this.node = node;
	}

	/**
	 * @see PluggableValidator.Delegate#validate()
	 */
	public void validate() {
		this.node.validateBranch();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.node);
	}

}
