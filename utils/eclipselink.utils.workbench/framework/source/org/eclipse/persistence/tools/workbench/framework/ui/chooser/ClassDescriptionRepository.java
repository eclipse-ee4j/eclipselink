/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.util.Iterator;

/**
 * Used by the UI to get and refresh a collection of "class descriptions"
 * that can be queried via a ClassDescriptionAdapter.
 */
public interface ClassDescriptionRepository {

	/**
	 * Return an iterator on the collection of "class descriptions"
	 * currently in the repository.
	 */
	Iterator classDescriptions();

	/**
	 * Refresh the collection of "class descriptions" in the repository.
	 */
	void refreshClassDescriptions();

}
