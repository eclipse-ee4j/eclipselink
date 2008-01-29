/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsio.legacy;

import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * Provide a public facade to the legacy I/O stuff.
 */
public class LegacyIOFacade {

	public static MWProject read60Project(File file, Preferences preferences) {
		Project60IOManager ioManager = new Project60IOManager();
		return ioManager.read(file, preferences);
	}

	/**
	 * disallow instantiation
	 */
	private LegacyIOFacade() {
		super();
		throw new UnsupportedOperationException();
	}

}
