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

/**
 * This interface is used by a tree cell renderer to delegate any requests
 * for an "accessible" name to the node.
 */
public interface AccessibleNode {

	/**
	 * Return a string that can add more description to a rendered node when
	 * the text is not sufficient. If null is returned, the text itself is used as the
	 * accessible name.
	 */
	String accessibleName();

}
