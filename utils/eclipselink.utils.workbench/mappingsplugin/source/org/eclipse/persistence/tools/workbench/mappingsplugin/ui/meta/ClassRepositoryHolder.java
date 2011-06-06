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
