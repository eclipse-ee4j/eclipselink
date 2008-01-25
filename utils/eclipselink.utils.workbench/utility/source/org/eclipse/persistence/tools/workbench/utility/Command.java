/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility;

/**
 * Simple interface for implementing the GOF Command design pattern.
 */
public interface Command {

	/**
	 * Execute the command. The semantics of the command
	 * is determined by the contract between the client and server.
	 */
	void execute();

	Command NULL_INSTANCE =
		new Command() {
			public void execute() {
				// do nothing
			}
			public String toString() {
				return "NullCommand";
			}
		};

}
