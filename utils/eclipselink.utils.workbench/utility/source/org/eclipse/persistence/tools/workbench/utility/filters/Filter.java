/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.filters;

/**
 * Used by various "pluggable" classes to filter objects.
 */
public interface Filter {

	/**
	 * Return whether the specified object is "accepted" by the
	 * filter. The semantics of "accept" is determined by the
	 * contract between the client and the server.
	 */
	boolean accept(Object o);


	Filter NULL_INSTANCE =
		new Filter() {
			// nothing is filtered - everything is accepted
			public boolean accept(Object next) {
				return true;
			}
			public String toString() {
				return "NullFilter";
			}
		};

}
