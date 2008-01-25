/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.app;

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;

/**
 * Define an interface describing the problems associated with an
 * application node.
 */
public interface ApplicationProblem {

	/**
	 * Return the application node most closely associated with the problem.
	 */
	ApplicationNode getSource();

	/**
	 * Return a key that can be used to uniquely identify the type of problem.
	 */
	String getMessageCode();

	/**
	 * Return the problem's message.
	 */
	String getMessage();

	/**
	 * Print the problem on the specified stream.
	 */
	void printOn(IndentingPrintWriter writer);

}
