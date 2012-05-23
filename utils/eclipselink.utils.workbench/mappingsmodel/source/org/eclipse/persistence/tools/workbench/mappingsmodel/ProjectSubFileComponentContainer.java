/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsmodel;

import java.util.Collection;
import java.util.Iterator;

/**
 * This interface defines the model behavior used by the ProjectIOManager to
 * read and write the project sub-files, the XML files that hold the state
 * for classes, tables, and descriptors.
 */
public interface ProjectSubFileComponentContainer {

	/**
	 * Return the project sub-file components that are saved in separate
	 * XML files.
	 * @see org.eclipse.persistence.tools.workbench.mappingsio.ProjectWriter
	 */
	Iterator projectSubFileComponents();

	/**
	 * Set the project sub-file components that are saved in separate
	 * XML files.
	 * @see org.eclipse.persistence.tools.workbench.mappingsio.ProjectReader
	 */
	void setProjectSubFileComponents(Collection projectSubFileComponents);

	/**
	 * Return the names of the project sub-file components that were
	 * present when the container was first read in
	 * (or subsequently saved).
	 * @see org.eclipse.persistence.tools.workbench.mappingsio.ProjectWriter
	 * @see org.eclipse.persistence.tools.workbench.mappingsio.ProjectReader
	 */
	Iterator originalProjectSubFileComponentNames();

	/**
	 * Set the names of the project sub-file components that were
	 * present when the container was saved.
	 * @see org.eclipse.persistence.tools.workbench.mappingsio.ProjectWriter
	 */
	void setOriginalProjectSubFileComponentNames(Collection originalSubComponentNames);

	/**
	 * Return whether the sub-component container or one of its
	 * descendants has changed in a way that requires the .mwp project
	 * file to be written.
	 */
	boolean hasChangedMainProjectSaveFile();

}
