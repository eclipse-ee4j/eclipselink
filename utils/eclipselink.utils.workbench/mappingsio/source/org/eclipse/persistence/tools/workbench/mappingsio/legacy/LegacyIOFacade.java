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
