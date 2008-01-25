/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta;

import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;

/**
 * Used by the UI to indirectly reference a MWClassRepository,
 * typically retrieved from the node currently associated with
 * a properties page (since the node can change over time).
 */
public interface ClassRepositoryHolder {

	/**
	 * Return the current class repository.
	 */
	MWClassRepository getClassRepository();

}
